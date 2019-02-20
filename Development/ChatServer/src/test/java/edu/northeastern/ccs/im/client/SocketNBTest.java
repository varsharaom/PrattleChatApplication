package edu.northeastern.ccs.im.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.constants.MessageConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.Assert.assertEquals;


public class SocketNBTest {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 4545;
    private static SocketNB socketNBObj;
    static ServerSocketChannel serverSocket;

    @BeforeAll
    static void suiteSetUp() throws IOException {
        serverSocket = SelectorProvider.provider().openServerSocketChannel();
        serverSocket.configureBlocking(false);
        serverSocket.socket().bind(new InetSocketAddress(PORT));
    }

    @BeforeEach
    public void setUp() {
        socketNBObj = new SocketNB(HOST, PORT);
    }

    @Test
    public void testSocketConnection() throws IOException {
        //        check the socket status without any channel
        assertEquals(false, socketNBObj.isConnected());
        socketNBObj.startIMConnection();
        assertEquals(true, socketNBObj.isConnected());
    }

    @Test
    public void testPrintMessage() throws IOException {
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        try {
            socketNBObj.print(message);
        }catch (IllegalOperationException e){
            assertEquals("Cannot send a message when we are not connected!",
                    e.getMessage());
        }
        socketNBObj.startIMConnection();
        socketNBObj.print(message);
    }

    @Test
    public void testEnqueueMessages() throws IOException {
        List<Message> messageList  = new ArrayList<>();
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        messageList.add(message);
        messageList.add(message);
        messageList.add(message);
        socketNBObj.startIMConnection();
        socketNBObj.startIMConnection();
        socketNBObj.enqueueMessages(messageList);
    }

}
