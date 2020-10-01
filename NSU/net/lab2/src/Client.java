import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Client {
    private FileInputStream source;
    private File file;
    private DataOutputStream output;
    private DataInputStream input;

    public static void main(String[] args) {
        if(args.length < 3) {
            System.err.println("Need more params");
            return;
        }

        int serverPort = Integer.parseInt(args[1]);

        InetAddress serverAddress;
        try {
            serverAddress = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            System.err.println(e.getMessage());
            return;
        }

        String filepath = args[2];
        new Client(serverAddress, serverPort, filepath);
    }

    private Client(InetAddress serverAddress, int serverPort, String filepath) {
        file = new File(filepath);
        try (Socket socket = new Socket(serverAddress, serverPort);
             FileInputStream s = new FileInputStream(file)) {
            source = s;

            System.out.println("\nConnected to " + socket.getInetAddress() + ":" + socket.getPort());

            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());

            //Todo: client's uuid
            sendFileName();
            sendFileSize();
            sendFile();

            System.out.println(recvFinishMess());
        } catch (IOException|NullPointerException e) {
            System.out.println(e.getMessage());
        }
    }

    private void sendFileName() throws IOException {
        String fileName = file.getName();
        byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
        output.writeInt(bytes.length);
        output.write(bytes);
    }

    private void sendFileSize() throws IOException {
        output.writeLong(file.length());
    }

    private void sendFile() throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while((length = source.read(buffer, 0, 1024)) > 0) {
            output.write(buffer, 0, length);
        }
    }

    private String recvFinishMess() throws IOException {
        int finishMessLength = input.readInt();
        byte[] finishMessByte = input.readNBytes(finishMessLength);
        return new String(finishMessByte, StandardCharsets.UTF_8);
    }

}