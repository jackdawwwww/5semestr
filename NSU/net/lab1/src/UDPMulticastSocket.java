import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPMulticastSocket {
    private MulticastSocket socket;
    private InetAddress groupIp;
    private int port;

    UDPMulticastSocket(String group, int portNum, int socketTimeout) throws IOException {
        groupIp = InetAddress.getByName(group);
        port = portNum;

        socket = new MulticastSocket(port);
        socket.joinGroup(groupIp);
        socket.setSoTimeout(socketTimeout);
    }

    MulticastSocket getSocket() {
        return socket;
    }

    InetAddress getGroup() {
        return  groupIp;
    }

    int getPort() {
        return port;
    }

}
