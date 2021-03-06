import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        if(args.length < 1) {
            System.err.println("Has no params");
            return;
        }

        int serverPort = Integer.parseInt(args[0]);
        start(serverPort);
    }

    private static void start(int serverPort) {
        try(ServerSocket ss = new ServerSocket(serverPort)) {
            System.out.println("Listening " + InetAddress.getLocalHost().getHostAddress() + ":" + ss.getLocalPort());
            System.out.println("|    Address    |     Speed   |Instant Speed|Percents|  File name");

            while(true) {
                try {
                    Socket socket = ss.accept();
                    ExecutorService service = Executors.newFixedThreadPool(10);
                    service.submit(new Session(socket));
                } catch(IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}