package edu.northeastern.ccs.im.server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.client.utilities.IMConnection;
import edu.northeastern.ccs.im.constants.ConnectionConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.northeastern.ccs.im.server.ServerConstants.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class NetworkConnectionTest {
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private SocketChannel sc;
    private Logger logger = Logger.getGlobal();

    @Before
    public void setUp() {
        try {
            sc = SocketChannel.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            selector = SelectorProvider.provider().openSelector();
            serverSocket.socket().bind(new InetSocketAddress(NEW_PORT));
            sc.connect(serverSocket.socket().getLocalSocketAddress());
        } catch (IOException e) {
            Logger logger = Logger.getGlobal();
            logger.log(Level.INFO, "IOException: " + e.getStackTrace());
        }
    }

    @After
    public void tearDown() throws IOException {
        sc.close();
        serverSocket.socket().close();
        serverSocket.close();
        selector.close();
    }

    @Test
    public void testNetworkConnection() {
        NetworkConnection nc = new NetworkConnection(sc);
        nc.close();
    }

    @Test
    public void testSendMessage() {
        NetworkConnection nc = new NetworkConnection(sc);
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message);
        nc.close();
    }

    @Test
    public void testMessageIterator() throws IOException {

        sc = SocketChannel.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.configureBlocking(false);
        selector = SelectorProvider.provider().openSelector();
        serverSocket.socket().bind(new InetSocketAddress(NEW_PORT2));
        sc.connect(serverSocket.socket().getLocalSocketAddress());

        Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        Message message2 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        Queue<Message> queue = new ConcurrentLinkedQueue<>();
        queue.add(message1);
        queue.add(message2);

        NetworkConnection nc = new NetworkConnection(sc);
//        if no messages are present
        assertFalse(nc.iterator().hasNext());
        try {
            nc.iterator().next();
        } catch (NoSuchElementException e) {

            logger.log(Level.INFO, "Stack trace: " + e.getStackTrace());
        }

        Class ncClass = nc.getClass();
//        if messages are present
        try {
            Field messages = ncClass.getDeclaredField("messages");
            messages.setAccessible(true);
            messages.set(nc, queue);
            assertTrue(nc.iterator().hasNext());
            Iterator<Message> iterator = nc.iterator();

            while (iterator.hasNext()) {
                logger.log(Level.INFO, iterator.next().getText());
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        nc.close();
    }

    @Test
    public void testSendMessageException() {
        NetworkConnection nc = new NetworkConnection(sc);
        try {
            sc.close();
        } catch (IOException e) {
            Logger logger = Logger.getGlobal();
            logger.log(Level.INFO, "" + e.getStackTrace());
        }
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message);
        nc.close();
    }

    @Test(expected = AssertionError.class)
    public void testConstructor() {
        ServerSocketChannel serverSocketLocal = null;
        try {
            serverSocketLocal = ServerSocketChannel.open();
            serverSocketLocal.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Boolean status = false;

        Selector selector = null;
        IMConnection connection1 = null;
        try {
            serverSocketLocal.socket().bind(new InetSocketAddress(ServerConstants.PORT));
            selector = SelectorProvider.provider().openSelector();
            serverSocketLocal.register(selector, SelectionKey.OP_ACCEPT);

            connection1 = new IMConnection(ConnectionConstants.HOST,
                    ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection1.connect();

            SocketChannel socket = serverSocketLocal.accept();
            socket.close();
            NetworkConnection networkConnection = new NetworkConnection(socket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocketLocal.close();
                selector.close();
                connection1.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
