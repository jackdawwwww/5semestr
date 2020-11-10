import java.net.InetAddress;

class ClientInfo {
    private InetAddress ip;
    private int port;

    public ClientInfo(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }


    public InetAddress getIp()
    {
        return ip;
    }

    public int getPort()
    {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) {
            return true;
        }

        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        ClientInfo ci = (ClientInfo) obj;
        return ci.ip.equals(this.ip) && ci.port == this.port;
    }

    @Override
    public int hashCode() {
        String hash = this.ip.toString() + this.port;
        return hash.hashCode();
    }
}
