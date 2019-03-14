package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.clientextensions.CommandLineMainExtended;
import edu.northeastern.ccs.im.constants.ClientStringConstants;
import edu.northeastern.ccs.im.constants.ConnectionConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.NetworkConnection;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.emptyStandardInputStream;


public class PrattleTest {
	private static final String ACTIVE = "active";
	
    private SocketChannel sc;
    private NetworkConnection nc;
    
    @Rule
	public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();

    @Before
    public void setUp() {

        try {
            sc = SocketChannel.open();
            nc = new NetworkConnection(sc);
        } catch (IOException e) {
        		Logger logger = Logger.getGlobal();
            logger.log(Level.INFO, "Stack trace: " + e.getStackTrace());
        }
    }

    @After
    public void cleanUp() {
        try {
            nc.close();
            sc.close();

        } catch (IOException e) {
            Logger logger = Logger.getGlobal();
            logger.log(Level.INFO, "Stack trace: " + e.getStackTrace());
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

        ClientRunnable c1 = new ClientRunnable(nc);
        queue.add(c1);
        Field active = Prattle.class.getDeclaredField(ACTIVE);
        active.setAccessible(true);
        active.set(null, queue);

        Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE));

    }

    @Test
    public void testRemoveClient() throws IllegalAccessException, NoSuchFieldException {
        nc = new NetworkConnection(sc);
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();
        ClientRunnable c1 = new ClientRunnable(nc);
        queue.add(c1);
        Field active = Prattle.class.getDeclaredField(ACTIVE);
        active.setAccessible(true);
        active.set(null, queue);
        Object returned = active.get(null);
        Prattle.removeClient(c1);
        assertEquals("[]", returned.toString());
    }

    @Test
    public void testUsingClient() {

        IMConnection connection1;
        IMConnection connection2;

        try{
        	
        	       	

            Thread thread = new Thread(new MainTest());
            thread.start();

        	
    				String[] arr = { "localhost", Integer.toString(4545) };

    			systemInMock.provideLines(
    					/*ClientStringConstants.WRONG_LOGIN_MSG1,
    					ClientStringConstants.WRONG_LOGIN_MSG2,
    					ClientStringConstants.WRONG_LOGIN_MSG3,
    					ClientStringConstants.WRONG_RGSTR_MSG1,
    					ClientStringConstants.WRONG_RGSTR_MSG2,
    					ClientStringConstants.WRONG_RGSTR_MSG3,
    					ClientStringConstants.WRONG_RGSTR_MSG4,*/
    					"$$LGN# project pwd",
    					"$$GRP# ela hi",
    					"$$GRP# ela bye",
    					"$$GRP# ela go",
    					/*ClientStringConstants.WRONG_GRP_MSG,
    					ClientStringConstants.WRONG_GRP_MSG2,
    					ClientStringConstants.WRONG_GRP_MSG3,

    					ClientStringConstants.WRONG_DRCT_MSG3,
    					ClientStringConstants.WRONG_DRCT_MSG4,
    					ClientStringConstants.CORRECT_GRP_MSG,
    					ClientStringConstants.RANDOM_MSG1,
    					ClientStringConstants.RANDOM_MSG2,
    					ClientStringConstants.CHANGE_MSG,
    					ClientStringConstants.WRONG_DRCT_MSG2,
    					ClientStringConstants.CORRECT_DRCT_MSG1,
    					ClientStringConstants.RANDOM_MSG2
    					,*/
    					ClientStringConstants.BYE_MSG

    					);
    //clm.main(arr);
    			CommandLineMainExtended.main(arr);        	
            Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE));
            connection1 = new IMConnection(ConnectionConstants.HOST,
                    ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection1.connect();

            connection2 = new IMConnection(ConnectionConstants.HOST,
                    ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection2.connect();

            connection1.sendMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection2.sendMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);

            Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE));
            Prattle.stopServer();
    
        }
        catch (Exception e) {
            Prattle.stopServer();
        }
    }
    
    @Test
    public void testRemoveNonExistantClient() throws IllegalAccessException, NoSuchFieldException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();
        nc = new NetworkConnection(sc);
        ClientRunnable c1 = new ClientRunnable(nc);
        ClientRunnable c2 = new ClientRunnable(nc);
        queue.add(c1);
        Field active = Prattle.class.getDeclaredField(ACTIVE);
        active.setAccessible(true);
        active.set(null, queue);

        Object returned = active.get(null);
        Prattle.removeClient(c1);
        assertEquals("[]", returned.toString());

        Prattle.removeClient(c1);
        Prattle.removeClient(c2);
        Prattle.stopServer();
    }
    
    class MainTest implements Runnable {

        @Override
        public void run() {
            Prattle.main(new String[0]);
        }
    }
    
    @Test
    public void testBroadcastMessage() throws IllegalAccessException, NoSuchFieldException {

        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        ClientRunnable c1 = Mockito.mock(ClientRunnable.class);
        Mockito.when(c1.isInitialized()).thenReturn(true);

        queue.add(c1);
        Field active = Prattle.class.getDeclaredField(ACTIVE);
        active.setAccessible(true);
        active.set(null, queue);

        Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE));
    }
    
    @Test
    public void testPrattleMainException() {

        IMConnection connection1;
        IMConnection connection2;

        try{
            Thread thread = new Thread(new MainTest());
            thread.start();

            Class<Prattle> pr = Prattle.class;
            Field isReady = pr.getDeclaredField("isReady");
            isReady.setAccessible(true);
            isReady.set(pr, false);
            
            Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE));
            connection1 = new IMConnection(ConnectionConstants.HOST,
                    ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection1.connect();

            connection2 = new IMConnection(ConnectionConstants.HOST,
                    ConnectionConstants.PORT, MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection2.connect();

            connection1.sendMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);
            connection2.sendMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);

            Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE));
        }
        catch (Exception e) {
            Prattle.stopServer();
        }
    }

}