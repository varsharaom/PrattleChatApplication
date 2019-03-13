package edu.northeastern.ccs.im.clientextensions;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.contrib.java.lang.system.TextFromStandardInputStream.*;
import org.junit.contrib.java.lang.system.TextFromStandardInputStream;
import edu.northeastern.ccs.im.KeyboardScanner;
import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageScanner;
import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.constants.ClientStringConstants;
import edu.northeastern.ccs.im.constants.ConnectionConstants;

import edu.northeastern.ccs.im.utils.CommandLineMainUtils;
import edu.northeastern.ccs.im.utils.LogAppender;
import edu.northeastern.ccs.serverim.NetworkConnection;


public class CommandLineMainExtendedTest {

	@Rule
	public final TextFromStandardInputStream systemInMock = emptyStandardInputStream();
	/**
	 * log appender instance to get the log messages from the client console
	 */

	private static LogAppender la;

	/**
	 * This method initializes the instance of custom appender which is used to get
	 * the log messages
	 */

	@BeforeClass
	public static void initAppender() {

		la = new LogAppender();

	}

	private ServerSocketChannel serverSocket;
	private Selector selector;
	private SocketChannel sc;	
	
	@Before
	public void init() {

		try {
			sc = SocketChannel.open();
			serverSocket = ServerSocketChannel.open();
			serverSocket.configureBlocking(false);
			selector = SelectorProvider.provider().openSelector();
			serverSocket.socket().bind(new InetSocketAddress(ClientStringConstants.TEST_PORT));
			sc.connect(serverSocket.socket().getLocalSocketAddress());
		} catch (IOException e) {
			
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
	public void testLogs() {
		 NetworkConnection nc = new NetworkConnection(sc);
		Logger logger = CommandLineMainExtended.getLogger();
		logger.addAppender(la);
		try {
			String[] arr = { "localhost", Integer.toString(ClientStringConstants.TEST_PORT) };

			systemInMock.provideLines("",
					ClientStringConstants.WRONG_LOGIN_MSG1,
					ClientStringConstants.WRONG_LOGIN_MSG2,
					ClientStringConstants.WRONG_LOGIN_MSG3,
					ClientStringConstants.WRONG_RGSTR_MSG1,
					ClientStringConstants.WRONG_RGSTR_MSG2,
					ClientStringConstants.WRONG_RGSTR_MSG3,
					ClientStringConstants.WRONG_RGSTR_MSG4,
					ClientStringConstants.CORRECT_LOGIN_MSG,
					
					ClientStringConstants.WRONG_GRP_MSG,
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
					ClientStringConstants.RANDOM_MSG2,
					ClientStringConstants.BYE_MSG				
					
					);

			CommandLineMainExtended.main(arr);
			
			List<LoggingEvent> log = la.getLogs();
			
			assertEquals( ClientStringConstants.LOGIN_MSG,(String) log.get(0).getMessage());
			
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(1).getMessage());
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(2).getMessage());
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(3).getMessage());
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(4).getMessage());
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(5).getMessage());
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(6).getMessage());
			assertEquals(ClientStringConstants.ERROR_MSG, (String) log.get(7).getMessage());			
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(8).getMessage() );
			
			assertEquals( ClientStringConstants.MSG_FORMAT,(String) log.get(9).getMessage());
			
			assertEquals( ClientStringConstants.ERROR_MSG,(String) log.get(10).getMessage());
			assertEquals( ClientStringConstants.ERROR_MSG,(String) log.get(11).getMessage());
			assertEquals( ClientStringConstants.ERROR_MSG,(String) log.get(12).getMessage());
			
			assertEquals( ClientStringConstants.ERROR_MSG,(String) log.get(13).getMessage());
			assertEquals( ClientStringConstants.ERROR_MSG,(String) log.get(14).getMessage());
			assertEquals( ClientStringConstants.MSG_FORMAT,(String) log.get(15).getMessage());
			assertEquals( ClientStringConstants.ERROR_MSG,(String) log.get(16).getMessage());
			assertEquals( ClientStringConstants.DISCONNECT,(String) log.get(17).getMessage());
			
			la.close();
			
			logger.removeAppender(la);
			 nc.close();			

		} catch (Exception e) {
			
		}

	}
	
	@Test
	public void testReadMessages() {
		Logger logger = CommandLineMainExtended.getLogger();
		logger.addAppender(la);
		IMConnection c1 = new IMConnection(ConnectionConstants.HOST, ClientStringConstants.TEST_PORT, ClientStringConstants.TEST_USER);
		c1.connect();

		MessageScanner mess = c1.getMessageScanner();
		Iterator<Message> messageIterator = CommandLineMainUtils.getMessageIteratorWithSameUser();
		mess.messagesReceived(messageIterator);
		CommandLineMainExtended.readNewMessages(mess, c1);

		Iterator<Message> messageIterator1 = CommandLineMainUtils.getMessageIteratorWithDifferentUser();
		mess.messagesReceived(messageIterator1);
		CommandLineMainExtended.readNewMessages(mess, c1);

		try {
			KeyboardScanner k = c1.getKeyboardScanner();
			Class scanClass = k.getClass();
			Field messages1 = scanClass.getDeclaredField("messages");
			messages1.setAccessible(true);
			List<String> msgs = new ArrayList<>();
			msgs.add(ClientStringConstants.WRONG_RGSTR_MSG1);
			
			msgs.add(ClientStringConstants.CORRECT_REG_MSG);
			messages1.set(k, msgs);
			CommandLineMainExtended.logInUser(c1, k);
			
			List<LoggingEvent> log = la.getLogs();
			
			assertEquals(ClientStringConstants.ERROR_MSG,(String) log.get(2).getMessage());

			logger.removeAppender(la);		
			
			c1.disconnect();
		} catch (Exception e) {
			
		} 

		c1.disconnect();

	}

}
