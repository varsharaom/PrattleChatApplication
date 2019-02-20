package edu.northeastern.ccs.im.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.constants.MessageConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;


public class NetworkConnectionTest {
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private SocketChannel sc;

    @BeforeEach
    void setUp() {
        try {
            sc = SocketChannel.open();
            serverSocket = ServerSocketChannel.open();
            selector = SelectorProvider.provider().openSelector();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @AfterEach
    void tearDown() throws IOException {
        sc.close();
        serverSocket.close();
        selector.close();
    }
    @Test
    public void testNetworkConnection() throws IOException {
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(4545));
        sc.connect(serverSocket.socket().getLocalSocketAddress());

        NetworkConnection nc = new NetworkConnection(sc);
    }

    @Test
    public void testSendMessage() throws IOException {
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(4545));
        sc.connect(serverSocket.socket().getLocalSocketAddress());
        NetworkConnection nc = new NetworkConnection(sc);
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message);
    }

    @Test
    public void testMessageIterator() throws IOException {
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(4545));
        sc.connect(serverSocket.socket().getLocalSocketAddress());
        NetworkConnection nc = new NetworkConnection(sc);
        Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        Message message2 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        nc.sendMessage(message1);
        nc.sendMessage(message2);
    }

    @Test
    public void testClose() throws IOException {
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(4545));
        sc.connect(serverSocket.socket().getLocalSocketAddress());
        NetworkConnection nc = new NetworkConnection(sc);
        nc.close();
    }

}
