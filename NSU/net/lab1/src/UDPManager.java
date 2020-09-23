import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class UDPManager {

    private byte[] buf;
    private UDPMulticastSocket socket;
    private long timeout;
    private ConcurrentHashMap<ByteBuffer, Long> history = new ConcurrentHashMap<>();

    UDPManager(UDPMulticastSocket multicastSocket, long receiveTimeout) {
        socket = multicastSocket;
        buf = getBytesFromUUID(UUID.randomUUID());
        timeout = receiveTimeout;
    }

    void start() throws IOException {
        long startTime = currentTime();
        while (currentTime() - startTime < (long) 10000000) {
            DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, socket.getGroup(), socket.getPort());
            socket.getSocket().send(datagramPacket);

            long start = currentTime();

            do {
                DatagramPacket recDatagramPacket = new DatagramPacket(new byte[buf.length], buf.length);

                try {
                    socket.getSocket().receive(recDatagramPacket);
                } catch (IOException e) {
                    continue;
                }

                updateWith(ByteBuffer.wrap(recDatagramPacket.getData()));
            } while(currentTime() - start < timeout);
            checkForDeath();
        }
    }

    private void updateWith(ByteBuffer id) {
        if (!history.containsKey(id)) {
            history.put(id, currentTime());
            printChanges();
        }
    }

    private void checkForDeath() {
        Set<Map.Entry<ByteBuffer, Long>> historyEntry = history.entrySet();
        boolean hasChanges = false;

        long time = currentTime();
        for(Map.Entry<ByteBuffer, Long> copy : historyEntry) {
            if(isDead(time - copy.getValue())) {
                history.remove(copy.getKey());
                hasChanges = true;
            }
        }

        if (hasChanges) {
            printChanges();
        }
    }

    private void printChanges() {
        System.out.print("\n\n\nUpdated alive copy's:\n");

        for(ByteBuffer key: history.keySet()) {
            String id = "id: " + Arrays.toString(key.array()) + "\n";
            System.out.print(id);
        }
    }

    private boolean isDead(long time) {
        return (time > timeout * 10);
    }

    private long currentTime() {
        return System.currentTimeMillis();
    }

    private byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }
}

