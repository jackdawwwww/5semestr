import java.io.IOException;
import java.net.DatagramPacket;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.io.*;

class Decoder {
    public static final int aliveChecking = 0;
    public static final int connect = 1;
    public static final int message = 2;
    public static final int accepted = 3;
    public static final int alternative = 4;
    public static final int disconnect = 5;


    public static Message decodeMess(DatagramPacket dp) throws IOException, ClassNotFoundException {
        byte[] data = dp.getData();
        byte[] type = new byte[Integer.BYTES];
        byte[] messID = getByte(UUID.randomUUID());
        byte[] messLen = new byte[Integer.BYTES];


        System.arraycopy(data, 0, type, 0, type.length);
        System.arraycopy(data, type.length, messID, 0, messID.length);
        System.arraycopy(data, type.length + messID.length, messLen, 0, messLen.length);

        int messLength = getInt(messLen);

        byte[] mess = (messLength > 0) ? new byte[messLength] : null;

        if(mess != null) {
            System.arraycopy(data, type.length + messID.length + messLen.length, mess, 0, mess.length);
        }

        return new Message(getInt(type), getUUID(messID), mess, dp.getAddress(), dp.getPort());
    }

    public static DatagramPacket encodeMessage(Message mess) throws IOException {
        ClientInfo ci = mess.getClientInfo();

        byte[] type = getByte(mess.getType());
        byte[] messID = getByte(mess.getMessID());
        int mesLen = mess.getMessLength();
        byte[] mesLenByte = getByte(mesLen);

        int resultLength = type.length + messID.length + mesLen + mesLenByte.length;


        byte[] packetData = new byte[resultLength];

        System.arraycopy(type,  0, packetData, 0, type.length);
        System.arraycopy(messID,0, packetData, type.length, messID.length);
        System.arraycopy(mesLenByte, 0, packetData, type.length + messID.length, mesLenByte.length);
        if(mesLen != 0)
        {
            System.arraycopy(mess.getMess(),   0, packetData, type.length + messID.length + mesLenByte.length, mesLen);
        }

        return new DatagramPacket(packetData, 0, packetData.length,
                mess.getClientInfo().getIp(), mess.getClientInfo().getPort());
    }

    private static final ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);

    public static byte[] getByte(int x) {
        byte[] result = new byte[Integer.BYTES];
        buffer.putInt(0, x);
        System.arraycopy(buffer.array(), 0, result, 0, result.length);
        return result;
    }

    public static int getInt(byte[] b)
    {
        return ByteBuffer.wrap(b).getInt();
    }

    public static byte[] getByte(UUID obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public static UUID getUUID(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (UUID) is.readObject();
    }

}
