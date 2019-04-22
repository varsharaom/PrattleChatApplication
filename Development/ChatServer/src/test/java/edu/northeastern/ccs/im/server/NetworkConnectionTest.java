package edu.northeastern.ccs.im.server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;

import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.constants.ConnectionConstants;
import edu.northeastern.ccs.im.constants.MessageTestConstants;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.NetworkConnection;

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

import static edu.northeastern.ccs.im.constants.MessageTestConstants.BROADCAST_TEXT_MESSAGE;
import static edu.northeastern.ccs.im.constants.MessageTestConstants.SIMPLE_USER;
import static edu.northeastern.ccs.im.server.ServerConstants.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
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
            logger.log(Level.INFO, "" + e.getStackTrace());
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
        Message message = Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE);
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

        Message message1 = Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE);
        Message message2 = Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE);
        Queue<Message> queue = new ConcurrentLinkedQueue<>();
        queue.add(message1);
        queue.add(message2);

        NetworkConnection nc = new NetworkConnection(sc);
        assertFalse(nc.iterator().hasNext());
        try {
            nc.iterator().next();
        } catch (NoSuchElementException e) {

            logger.log(Level.INFO, "Stack trace: " + e.getStackTrace());
        }

        Class ncClass = nc.getClass();
        try {
            Field messages = ncClass.getDeclaredField("messages");
            messages.setAccessible(true);
            messages.set(nc, queue);
            assertTrue(nc.iterator().hasNext());
            Iterator<Message> iterator = nc.iterator();

            while (iterator.hasNext()) {
                logger.log(Level.INFO, iterator.next().getText());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
        		logger.log(Level.INFO, "IOException: " + e.getStackTrace());
        }
        nc.close();
    }

    @Test
    public void testSendMessageException() {
        NetworkConnection nc = new NetworkConnection(sc);
        try {
            sc.close();
        } catch (IOException e) {
            logger.log(Level.INFO, "" + e.getStackTrace());
        }
        Message message = Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message);
        nc.close();
    }

    // @Test(expected = AssertionError.class)
    public void testConstructor() {
        ServerSocketChannel serverSocketLocal = null;
        try {
            serverSocketLocal = ServerSocketChannel.open();
            serverSocketLocal.configureBlocking(false);
        } catch (IOException e) {
            logger.log(Level.INFO, ""+e.getStackTrace());
        }

        Selector selector = null;
        IMConnection connection1 = null;
        NetworkConnection networkConnection = null;
        try {
            serverSocketLocal.socket().bind(new InetSocketAddress(ServerConstants.PORT));
            selector = SelectorProvider.provider().openSelector();
            serverSocketLocal.register(selector, SelectionKey.OP_ACCEPT);

            connection1 = new IMConnection(ConnectionConstants.HOST, ConnectionConstants.PORT,
                    BROADCAST_TEXT_MESSAGE);
            connection1.connect();

            SocketChannel socket = serverSocketLocal.accept();
            socket.close();
            networkConnection = new NetworkConnection(socket);
        } catch (IOException e) {
            logger.log(Level.INFO, ""+e.getStackTrace());
        } finally {
            try {
                serverSocketLocal.close();
                selector.close();
                connection1.disconnect();
                networkConnection.close();
            } catch (IOException e) {
                logger.log(Level.INFO, ""+e.getStackTrace());
            }
        }
    }

    // @Test
    public void testMessageIteratorHasNext() {
        ServerSocketChannel serverSocketLocal = null;
        try {
            serverSocketLocal = ServerSocketChannel.open();
            serverSocketLocal.configureBlocking(false);
        } catch (IOException e) {
            logger.log(Level.INFO, ""+e.getStackTrace());
        }


        Selector selector = null;
        IMConnection connection1 = null;
        NetworkConnection networkConnection = null;
        SocketChannel socket = null;
        try {
            serverSocketLocal.socket().bind(new InetSocketAddress(ServerConstants.PORT));
            selector = SelectorProvider.provider().openSelector();
            serverSocketLocal.register(selector, SelectionKey.OP_ACCEPT);

            connection1 = new IMConnection(ConnectionConstants.HOST, ConnectionConstants.PORT,
                    BROADCAST_TEXT_MESSAGE);
            connection1.connect();

            socket = serverSocketLocal.accept();
            connection1.sendMessage(BROADCAST_TEXT_MESSAGE);
            networkConnection = new NetworkConnection(socket);
            Iterator<Message> itr = networkConnection.iterator();
            assertTrue(itr.hasNext());
            StringBuilder builder = new StringBuilder();
            while ((itr.hasNext())) {
                builder.append(itr.next());
            }
            assertEquals("HLO 20 broadcastTextMessage 0 BCT 20 " +
                    "broadcastTextMessage 20 broadcastTextMessage", builder.toString());
            connection1.sendMessage("");
            itr = networkConnection.iterator();
            builder = new StringBuilder();
            while ((itr.hasNext())) {
                builder.append(itr.next());
            }
            assertEquals("BCT 20 broadcastTextMessage 2 --", builder.toString());

        } catch (IOException e) {
            logger.log(Level.INFO, ""+e.getStackTrace());
        } finally {
            try {
                socket.close();
                serverSocketLocal.close();
                selector.close();
                connection1.disconnect();
                networkConnection.close();
            } catch (IOException e) {
                logger.log(Level.INFO, ""+e.getStackTrace());
            }
        }
    }

}
