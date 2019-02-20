package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class PrattleTest {
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private ScheduledExecutorService tPool;
    private SocketChannel sc;

    void setUp() {

        try {
            sc = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMain() throws IOException {
//        serverSocket = ServerSocketChannel.open();
//        serverSocket.configureBlocking(false);
//        serverSocket.socket().bind(new InetSocketAddress(4545));
////        Prattle.main(new String[] {"4545"});
//        sc.connect(serverSocket.socket().getLocalSocketAddress());
//
//        ByteBuffer wrapper = ByteBuffer.wrap("test".getBytes());
//        int bytesWritten = 0;
//        bytesWritten += sc.write(wrapper);
    }

    @Test
    void testStopServer() {
        Prattle.stopServer();
    }

    @Test
    void testBroadcastClient() throws IllegalAccessException,
            NoSuchFieldException{
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();
        NetworkConnection nc = new NetworkConnection(sc);

        ClientRunnable c1 = new ClientRunnable(nc);
        queue.add(c1);

        Field active = Prattle.class.getDeclaredField("active");
        active.setAccessible(true);
        active.set(null, queue);

        ConcurrentLinkedQueue<ClientRunnable> returned = (ConcurrentLinkedQueue<ClientRunnable>) active.get(null);

        Prattle.broadcastMessage(Message.makeBroadcastMessage("test","hello"));

    }

    @Test
    void testRemoveClient() throws IllegalAccessException, NoSuchFieldException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        NetworkConnection nc = new NetworkConnection(sc);
        ClientRunnable c1 = new ClientRunnable(nc);
        queue.add(c1);
        Field active = Prattle.class.getDeclaredField("active");
        active.setAccessible(true);
        active.set(null, queue);
        ConcurrentLinkedQueue<ClientRunnable> returned = (ConcurrentLinkedQueue<ClientRunnable>) active.get(null);

        Prattle.removeClient(c1);

        assertTrue(returned.isEmpty());
    }

}
