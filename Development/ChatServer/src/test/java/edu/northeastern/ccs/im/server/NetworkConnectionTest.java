package edu.northeastern.ccs.im.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.constants.MessageConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import static edu.northeastern.ccs.im.server.ServerConstants.PORT;


public class NetworkConnectionTest {
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private SocketChannel sc;

    @Before
    public void setUp() {
        try {
            sc = SocketChannel.open();
            serverSocket = ServerSocketChannel.open();
            serverSocket.configureBlocking(false);
            selector = SelectorProvider.provider().openSelector();
            serverSocket.socket().bind(new InetSocketAddress(PORT));
            sc.connect(serverSocket.socket().getLocalSocketAddress());
        } catch (IOException e) {
            e.printStackTrace();
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
    public void testNetworkConnection() throws IOException {
        NetworkConnection nc = new NetworkConnection(sc);
        nc.close();
    }

    @Test
    public void testSendMessage() throws IOException {
        NetworkConnection nc = new NetworkConnection(sc);
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message);
        nc.close();
    }

    @Test
    public void testMessageIterator() throws IOException {
        NetworkConnection nc = new NetworkConnection(sc);
        Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        Message message2 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message1);
        nc.sendMessage(message2);
        nc.close();
    }

    @Test
    public void testClose() throws IOException {
        NetworkConnection nc = new NetworkConnection(sc);
        nc.close();
    }

}
