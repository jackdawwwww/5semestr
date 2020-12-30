package proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;

public class ChannelUtils {

    public static DatagramChannel createDatagramSocket(Selector selector, InetSocketAddress bind, int ops) throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.configureBlocking(false);
        try {
            channel.connect(bind);
        } catch (UnresolvedAddressException ignored) { }
        channel.register(selector, ops);
        return channel;
    }

    public static SocketChannel createSocket(Selector selector, int ops) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.register(selector, ops);
        return channel;
    }

    public static ServerSocketChannel createServerSocket(Selector selector, InetSocketAddress bind, int ops) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(bind);
        channel.register(selector, ops);
        return channel;
    }

    public static SocketChannel createSocket(Selector selector, InetSocketAddress bind, int ops) throws IOException {
        SocketChannel channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.socket().bind(bind);
        channel.register(selector, ops);
        return channel;
    }

    public static SocketChannel createSocket(ServerSocketChannel server, Selector selector, int ops) throws IOException {
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, ops);
        return channel;
    }

}
