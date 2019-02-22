package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.constants.ConnectionConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.utils.NetworkConnectionTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.Iterator;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableTest {

    @InjectMocks
    ClientRunnable clientRunnable;

    @Mock
    NetworkConnection networkConnection;

    private final Logger logger = Logger.getLogger(ClientRunnableTest.class.getName());

    @Test
    public void testValidClient() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator();
        when(networkConnection.iterator()).thenReturn(messageIterator);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        clientRunnable.run();
        networkConnection.close();
    }

    @Test
    public void testClientInitialization() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator();
        when(networkConnection.iterator()).thenReturn(messageIterator);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        assertTrue(clientRunnable.isInitialized());
        Prattle.broadcastMessage(Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE));
    }

    @Test
    public void testClientId() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithNoUsers();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        assertEquals(-1, clientRunnable.getUserId());
    }

    @Test
    public void testClientWithNullUserName() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithNoUsers();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        clientRunnable.run();
        networkConnection.close();
    }

    @Test
    public void testEnqueueMessage() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithDifferentUsers();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        when(networkConnection.sendMessage(any())).thenReturn(true);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        clientRunnable.run();
    }

    @Test
    public void testEnqueMessageAndTerminate() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithDifferentUsers();
        when(networkConnection.iterator()).thenReturn(messageIterator);
        when(networkConnection.sendMessage(any())).thenReturn(false);
        clientRunnable = new ClientRunnable(networkConnection);
        ScheduledFuture<?> clientFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(clientRunnable, ServerConstants.CLIENT_CHECK_DELAY,
                        ServerConstants.CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS);
        clientRunnable.setFuture(clientFuture);

        clientRunnable.run();
        clientRunnable.run();

    }

    @Test
    public void testQuitMessageAndTerminate() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithQuitMessages();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        when(networkConnection.sendMessage(any())).thenReturn(false);
        clientRunnable = new ClientRunnable(networkConnection);
        ScheduledFuture<?> clientFuture = Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(clientRunnable, ServerConstants.CLIENT_CHECK_DELAY,
                        ServerConstants.CLIENT_CHECK_DELAY, TimeUnit.MILLISECONDS);
        clientRunnable.setFuture(clientFuture);
        clientRunnable.run();
        clientRunnable.run();
    }

	/**
	 * Testing client with a random array of multiple messages.
	 */
	@Test
	public void testWithMultipleMessages() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIteratorWithManyMessages();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "Starting demo task");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, ConnectionConstants.SCHEDULER_DELAY, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.run();
		networkConnection.close();
	}

	/**
	 * Testing the behavior of terminateClient with an array of multiple messages
	 */
	@Test
	public void testTerminateClient() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIteratorWithManyMessages();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "In testTerminateClient");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, ConnectionConstants.SCHEDULER_DELAY, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.terminateClient();
		networkConnection.close();
	}

	/**
	* Testing with an array of terminate messages only
	*/
	@Test
	public void testTerminateMessages() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIteratorWithMultipleQuitMessages();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "In testTerminateMessages");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, ConnectionConstants.SCHEDULER_DELAY, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.run();
		networkConnection.close();
	}

	/**
	 * Testing clientRunnable with an array of 0 messages
	 */
	@Test
	public void testEmptyMessageQueue() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIteratorWithNoMessages();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		logger.log(Level.INFO, ""+clientRunnable.getUserId());
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "Starting new task");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, ConnectionConstants.SCHEDULER_DELAY, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.run();
		networkConnection.close();
	}
	
	/**
 	 * Test single message queue.
 	 */
 	@Test
 	public void testSingleMessageQueue() {
 		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIteratorWithManyMessages();
 		when(networkConnection.iterator()).thenReturn(messageIterator);
 		clientRunnable = new ClientRunnable(networkConnection);
 		clientRunnable.run();
 		logger.log(Level.INFO, ""+clientRunnable.getUserId());
 		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
 		Runnable task2 = () -> logger.log(Level.INFO, "Starting new task");
 		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, ConnectionConstants.SCHEDULER_DELAY, TimeUnit.SECONDS);
 		clientRunnable.setFuture(scheduledFuture);
 		while(messageIterator.hasNext()) {
 			messageIterator.next();
 		}
 		clientRunnable.run();
 		networkConnection.close();
 	}
 	
 	@Test
    public void testNullConnection() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithDifferentUsers();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        when(networkConnection.sendMessage(any())).thenReturn(true);
        clientRunnable = new ClientRunnable(null);
        clientRunnable.run();
        clientRunnable.run();
    }
 	
 	@Test
    public void testNullMessage() {
        Iterator<Message> messageIterator = NetworkConnectionTestUtil
                .getMessageIteratorWithNullAndNonNullMessages();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        when(networkConnection.sendMessage(any())).thenReturn(true);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        clientRunnable.run();
    }
 	
}
