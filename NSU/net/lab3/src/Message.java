import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class Message implements Comparable {
    private int messType;
    private UUID messID;
    private int messLength;
    private byte[] mess;
    private ClientInfo clientInfo;
    private int retries = 0;

    public Message(int messType, UUID messId, byte[] mess, InetAddress ip, int port) {
        this.messType = messType;
        this.messID = messId;
        this.mess = mess;
        this.messLength = (mess == null) ? 0 : mess.length;
        this.clientInfo = new ClientInfo(ip, port);
    }

    public static byte[] encodeIp(InetAddress ip, int port) {
        byte[] ipByte = ip.getAddress();
        byte[] portByte = Decoder.getByte(port);

        byte[] result = new byte[portByte.length + ipByte.length];

        System.arraycopy(portByte, 0, result, 0, portByte.length);
        System.arraycopy(ipByte, 0, result, portByte.length, ipByte.length);
        return result;
    }

    public static ClientInfo decodeIp(byte[] data) throws UnknownHostException {

        byte[] portByte = new byte[Integer.BYTES];
        byte[] ipByte = new byte[data.length - portByte.length];

        System.arraycopy(data, 0, portByte, 0, portByte.length);
        System.arraycopy(data, portByte.length, ipByte, 0, ipByte.length);

        int port = Decoder.getInt(portByte);
        InetAddress ip = InetAddress.getByAddress(ipByte);

        return new ClientInfo(ip, port);
    }

    public static String decodeString(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    public static byte[] encodeString(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    public int getType() {
        return messType;
    }

    public byte[] getMess() {
        return mess;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void incrementRetries() {
        ++retries;
    }

    public UUID getMessID() {
        return messID;
    }

    public int getRetries() {
        return retries;
    }

    public int getMessLength() {
        return messLength;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Message) {
            Message m = (Message)o;
            return (m.messID == this.messID && m.messType == this.messType && Arrays.equals(m.mess, this.mess) &&
                            m.retries == this.retries && m.clientInfo.getIp() == this.clientInfo.getIp() &&
                            m.clientInfo.getPort() == this.clientInfo.getPort()) ? 0 : -1;
        }
        else return -1;
    }

}