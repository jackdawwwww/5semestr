package Controllers;

import java.net.InetAddress;

public class Node {
    private final InetAddress ip;
    private final int port;

    public Node(InetAddress _ip, int _port) {
        ip = _ip;
        port = _port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        return (ip.toString() + port).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        if(o == null || o.getClass() != this.getClass())
            return false;

        Node node = (Node)o;

        return node.ip.equals(this.ip) && node.port == this.port;
    }


    @Override
    public String toString() {
        return ip.toString() + ":" + port;
    }
}