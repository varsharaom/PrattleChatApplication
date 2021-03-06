package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.constants.ConnectionConstants;
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

import static edu.northeastern.ccs.im.constants.MessageTestConstants.BROADCAST_TEXT_MESSAGE;
import static edu.northeastern.ccs.im.constants.MessageTestConstants.SIMPLE_USER;
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
	public void testBroadcastClient() throws IllegalAccessException, NoSuchFieldException {

		ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();
		ClientRunnable c1 = new ClientRunnable(nc);
		queue.add(c1);
		Field active = Prattle.class.getDeclaredField(ACTIVE);
		active.setAccessible(true);
		active.set(null, queue);

		Prattle.broadcastMessage(
				Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE));

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

	//@Test
	public void testUsingClient() {

		IMConnection connection1;
		IMConnection connection2;

		try {

			Thread thread =new Thread(() -> Prattle.main(new String[0]));
			thread.start();
			Prattle.broadcastMessage(Message.makeBroadcastMessage(SIMPLE_USER,
					BROADCAST_TEXT_MESSAGE));
			connection1 = new IMConnection(ConnectionConstants.HOST, ConnectionConstants.PORT,
					BROADCAST_TEXT_MESSAGE);
			connection1.connect();

			connection2 = new IMConnection(ConnectionConstants.HOST, ConnectionConstants.PORT,
					BROADCAST_TEXT_MESSAGE);
			connection2.connect();

			connection1.sendMessage(BROADCAST_TEXT_MESSAGE);
			connection2.sendMessage(BROADCAST_TEXT_MESSAGE);

			Prattle.broadcastMessage(Message.makeBroadcastMessage(SIMPLE_USER,
					BROADCAST_TEXT_MESSAGE));
			Prattle.stopServer();

		} catch (Exception e) {
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

	@Test
	public void testBroadcastMessage() throws IllegalAccessException, NoSuchFieldException {

		ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

		ClientRunnable c1 = Mockito.mock(ClientRunnable.class);
		Mockito.when(c1.isInitialized()).thenReturn(true);

		queue.add(c1);
		Field active = Prattle.class.getDeclaredField(ACTIVE);
		active.setAccessible(true);
		active.set(null, queue);

		Prattle.broadcastMessage(
				Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE));
	}

	@Test
	public void testPrattleMainException() {

		IMConnection connection1;
		IMConnection connection2;

		try {
			Thread thread = new Thread(() -> Prattle.main(new String[0]));
			thread.start();

			Class<Prattle> pr = Prattle.class;
			Field isReady = pr.getDeclaredField("isReady");
			isReady.setAccessible(true);
			isReady.set(pr, false);

			Prattle.broadcastMessage(Message.makeBroadcastMessage(SIMPLE_USER,
					BROADCAST_TEXT_MESSAGE));
			connection1 = new IMConnection(ConnectionConstants.HOST, ConnectionConstants.PORT,
					BROADCAST_TEXT_MESSAGE);
			connection1.connect();

			connection2 = new IMConnection(ConnectionConstants.HOST, ConnectionConstants.PORT,
					BROADCAST_TEXT_MESSAGE);
			connection2.connect();

			connection1.sendMessage(BROADCAST_TEXT_MESSAGE);
			connection2.sendMessage(BROADCAST_TEXT_MESSAGE);

			Prattle.broadcastMessage(Message.makeBroadcastMessage(SIMPLE_USER,
					BROADCAST_TEXT_MESSAGE));
		} catch (Exception e) {
			Prattle.stopServer();

		}

	}
}
