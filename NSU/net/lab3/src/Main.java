import java.io.IOException;
import java.net.InetAddress;

public class Main {

    public static void main(String[] args) {
        if (args.length == 3) {
            int port = Integer.parseInt(args[2]);
            int loss = Integer.parseInt(args[1]);
            try {
                System.out.println("NODE'S NAME = " + args[0] + " PORT = " + port + " LOSS PERCENT = " + loss + "\n LONELY NODE ");
                new TreeNode(args[0], port, loss);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (args.length == 5) {
            int port = Integer.parseInt(args[2]);
            int loss = Integer.parseInt(args[1]);
            try {
                InetAddress ip = InetAddress.getByName(args[3]);
                int connPort = Integer.parseInt(args[4]);

                System.out.println("NODE'S NAME = " + args[0]+ " PORT = " + port + " LOSS PERCENT = " + loss + "\n HAS INFO ABOUT ANOTHER NODE");
                new TreeNode(args[0], port, loss, ip, connPort);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid args num");
        }
    }
}
