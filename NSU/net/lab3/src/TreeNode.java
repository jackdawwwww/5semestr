import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.*;

import static java.lang.System.currentTimeMillis;

class TreeNode {
    private String name;
    private int loss;

    private DatagramSocket socket;
    private ClientInfo alternative = null;

    private Map<ClientInfo, ClientInfo> neighbors = new HashMap<>();
    private Map<UUID, MessageDp> messagesToSend = new HashMap<>();
    private Map<UUID, Long> receivedMessagesUUIDs = new HashMap<>();

    private final Random rand = new Random(currentTimeMillis());

    private int disconnectRetriesValue = 5;
    private long timeout = 5000;

    private byte[] buffer = new byte[1024];

    private void commonInit(String name, int port, int loss) throws IOException {
        this.name = name;
        this.loss = loss;
        socket = new DatagramSocket(port);
    }

    public TreeNode(String name, int port, int loss) throws IOException {
        commonInit(name, port, loss);
        begin();
    }

    public TreeNode(String name, int port, int loss, InetAddress connIp, int connPort) throws IOException {
        commonInit(name, port, loss);
        connectMessage(new ClientInfo(connIp, connPort));
        begin();
    }


    private void begin() throws IOException {

        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                checkMaps();
                trySendMessages();

                long timeStart = currentTimeMillis();
                socket.setSoTimeout((int)timeout);

                while(currentTimeMillis() - timeStart < timeout) {
                    try {

                        if (in.ready()) {
                            String message = in.readLine();
                            addMessage(message);
                        }

                        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
                        socket.receive(dp);

                        if(rand.nextInt(100) >= loss) {
                            proceedDP(dp);
                        }

                    } catch(SocketTimeoutException ignored) {}
                }

            }
        }
        finally {
            System.out.println("I'm over");
            socket.close();
        }
    }

    //проверка на отвалившийся узел по полученным ранее сообщениям
    private void checkMaps() {
        Set<ClientInfo> forDelete = new HashSet<>();
        for(Map.Entry<UUID, MessageDp> entry: messagesToSend.entrySet()) {
            if(entry.getValue().getMess().getRetries() >= disconnectRetriesValue) {
                forDelete.add(entry.getValue().getMess().getClientInfo());
            }
        }

        //удаляем отвалившиеся узлы
        for(ClientInfo ci: forDelete) {
            disconnectFromClient(ci);
        }

        long timeNow = currentTimeMillis();
        receivedMessagesUUIDs.entrySet().removeIf(entry -> timeNow - entry.getValue() > timeout * disconnectRetriesValue);
    }

    //проверка полученного пакета
    private void proceedDP(DatagramPacket dp) {
        Message mess;

        try {
            mess = Decoder.decodeMess(dp);
        } catch (IOException | ClassNotFoundException ignored) {
            System.out.println("Can not decode message");
            return;
        }

        switch(mess.getType()) {
            case Decoder.aliveChecking: {
                if(!neighbors.containsKey(mess.getClientInfo())) return;
                if(!receivedMessagesUUIDs.containsKey(mess.getMessID())) {
                    receivedMessagesUUIDs.put(mess.getMessID(), currentTimeMillis());
                }

                sendAcceptMess(mess);
                break;
            }

            case Decoder.connect: {
                if(!receivedMessagesUUIDs.containsKey(mess.getMessID())) {
                    ClientInfo ci = mess.getClientInfo();
                    if(!neighbors.containsKey(ci)) {
                        neighbors.put(ci, null);
                        sendAliveChecking(mess);
                    }

                    if(alternative == null) {
                        alternative = ci;
                    }
                    sendAlternative();

                    receivedMessagesUUIDs.put(mess.getMessID(), currentTimeMillis());
                }

                sendAcceptMess(mess);
                break;
            }

            case Decoder.message: {
                if(!neighbors.containsKey(mess.getClientInfo())) return;
                if(!receivedMessagesUUIDs.containsKey(mess.getMessID())) {
                    receivedMessagesUUIDs.put(mess.getMessID(), System.currentTimeMillis());
                    System.out.println(Message.decodeString(mess.getMess()));
                }

                sendAcceptMess(mess);
                break;
            }

            case Decoder.accepted: {
                if(!receivedMessagesUUIDs.containsKey(mess.getMessID()) && messagesToSend.containsKey(mess.getMessID())) {
                    if(messagesToSend.get(mess.getMessID()).getMess().getType() == Decoder.aliveChecking) {
                        sendAliveChecking(mess);
                    } else if(messagesToSend.get(mess.getMessID()).getMess().getType() == Decoder.connect) {
                        if(neighbors.containsKey(mess.getClientInfo())) return;

                        neighbors.put(mess.getClientInfo(), null);
                        sendAliveChecking(mess);

                        if(alternative == null) {
                            alternative = mess.getClientInfo();
                        }
                        sendAlternative();
                    }
                    messagesToSend.remove(mess.getMessID());
                    receivedMessagesUUIDs.put(mess.getMessID(), currentTimeMillis());
                }

                break;
            }

            case Decoder.alternative: {
                if(!receivedMessagesUUIDs.containsKey(mess.getMessID())) {
                    try {
                        if(!neighbors.containsKey(mess.getClientInfo())) return;

                        ClientInfo newAlter = Message.decodeIp(mess.getMess());
                        neighbors.put(mess.getClientInfo(), newAlter);
                    } catch (UnknownHostException ignored) {
                        System.out.println("Can not decode IP from Message");
                    }

                    receivedMessagesUUIDs.put(mess.getMessID(), System.currentTimeMillis());
                }

                sendAcceptMess(mess);
                break;
            }

            case Decoder.disconnect: {
                disconnectFromClient(mess.getClientInfo());
            }
        }
    }

    //отправка сообщений
    private void sendMessage(Message m) {
        try {
            DatagramPacket dp = Decoder.encodeMessage(m);
            messagesToSend.put(m.getMessID(), new MessageDp(m, dp));
            socket.send(dp);
        } catch (IOException ignored) {
            System.out.println("Can't encode or send message");
        }
    }

    private void trySendMessages() {
        for(Map.Entry<UUID, MessageDp> m : messagesToSend.entrySet()) {
            try {
                socket.send(m.getValue().getDp());
                m.getValue().getMess().incrementRetries();
            } catch (IOException ignored) {
                System.out.println("Message sending failed");
            }
        }
    }


    private void sendAcceptMess(Message mess) {
        Message acceptMess = new Message(Decoder.accepted, mess.getMessID(),
                null, mess.getClientInfo().getIp(), mess.getClientInfo().getPort());

        try {
            DatagramPacket acceptDp = Decoder.encodeMessage(acceptMess);
            socket.send(acceptDp);
        } catch (IOException e) {
            System.out.println("Error while encoding or sending mess:" + e.getMessage());
        }
    }


    private void sendAlternative() {
        for(Map.Entry<ClientInfo, ClientInfo> entry: neighbors.entrySet()) {
            if(!entry.getKey().equals(alternative)) {
                Message newMess = new Message(Decoder.alternative, UUID.randomUUID(),
                        Message.encodeIp(alternative.getIp(), alternative.getPort()), entry.getKey().getIp(), entry.getKey().getPort());
                sendMessage(newMess);
            }
        }
    }


    //инициализация связи с клентом
    private void connectMessage(ClientInfo ci) {
        if(ci != null) {
            neighbors.put(ci, null);
            Message connMess = new Message(Decoder.connect, UUID.randomUUID(), null, ci.getIp(), ci.getPort());
            sendMessage(connMess);
        }
    }

    //проверка на жизнь
    private void sendAliveChecking(Message mess) {
        Message healthMess = new Message(Decoder.aliveChecking, UUID.randomUUID(), null,
                mess.getClientInfo().getIp(), mess.getClientInfo().getPort());
        sendMessage(healthMess);
    }


    //очистка сообщений от соседа
    private void deleteMessagesFromClient(ClientInfo ci) {
        for(Iterator<Map.Entry<UUID, MessageDp>> iter = messagesToSend.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<UUID, MessageDp> entry = iter.next();
            Message m = entry.getValue().getMess();
            if(m.getClientInfo().equals(ci)) {
                iter.remove();
            }
        }
    }

    //отключение соседа
    private void disconnectFromClient(ClientInfo ci) {
        System.out.println("Disconnected: " + ci.getPort());
        deleteMessagesFromClient(ci);

        connectMessage(neighbors.get(ci));
        neighbors.remove(ci);

        if(ci.equals(alternative)) {
            if(neighbors.size() != 0) {
                alternative = neighbors.entrySet().iterator().next().getKey();
                sendAlternative();
                return;
            }
            alternative = null;
        } else if (alternative != null) {
            neighbors.put(alternative, null);
            alternative = null;
        }
    }

    //отправка сообщения из ввода
    private void addMessage(String mess) {
        byte[] message = Message.encodeString(name + ": " + mess);

        for(Map.Entry<ClientInfo, ClientInfo> n: neighbors.entrySet()) {
            Message newMess = new Message(Decoder.message, UUID.randomUUID(), message,
                    n.getKey().getIp(), n.getKey().getPort());
            sendMessage(newMess);
        }
    }


    static class MessageDp {
        private Message mess;
        private DatagramPacket dp;

        MessageDp(Message m, DatagramPacket d) {
            mess = m;
            dp = d;
        }

        Message getMess()
        {
            return mess;
        }

        DatagramPacket getDp()
        {
            return dp;
        }
    }
}