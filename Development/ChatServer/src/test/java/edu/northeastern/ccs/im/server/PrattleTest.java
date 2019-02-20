package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.constants.MessageConstants;
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
    private SocketChannel sc;

    @BeforeEach
    void setUp() {

        try {
            sc = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE));

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
