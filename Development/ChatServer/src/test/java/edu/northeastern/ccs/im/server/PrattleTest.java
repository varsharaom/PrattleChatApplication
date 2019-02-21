package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.client.IMConnection;
import edu.northeastern.ccs.im.constants.ConnectionConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class PrattleTest {
    private SocketChannel sc;

    @Before
    public void setUp() {

        try {
            sc = SocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUp() {
        try {
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testStopServer() {
        Prattle.stopServer();
    }

    @Test
    public void testBroadcastClient() throws IllegalAccessException,
            NoSuchFieldException {
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
    public void testRemoveClient() throws IllegalAccessException, NoSuchFieldException {
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

    @Test
    public void test() {
        Thread thread = new Thread(new MainTest());
        thread.start();
        Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE));
        IMConnection connection1 = new IMConnection(ConnectionConstants.HOST,
                ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
        connection1.connect();
        IMConnection connection2 = new IMConnection(ConnectionConstants.HOST,
                ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
        connection2.connect();
        connection1.sendMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);
        connection2.sendMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);
        assertFalse(connection1.getMessageScanner().hasNext());
        Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE));
        //assertTrue(connection2.getMessageScanner().hasNext());
        Prattle.stopServer();
    }

    class MainTest implements Runnable {

        @Override
        public void run() {
            Prattle.main(new String[0]);
        }
    }

}
