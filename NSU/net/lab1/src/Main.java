import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            UDPMulticastSocket socket = new UDPMulticastSocket("224.0.0.0", 8040, 1000);
            UDPManager manager = new UDPManager(socket, 1000);
            manager.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
