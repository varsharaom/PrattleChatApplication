package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.utils.NetworkConnectionTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
	 * Testing client with random array of messages.
	 */
	@Test
	public void testClient1() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator1();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		networkConnection.close();
	}

	/**
	 * Testing client with a random array of messages. 
	 */
	@Test
	public void testClient2() {
		
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator2();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "Starting demo task");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, 5, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.run();
		networkConnection.close();
	}

	

	/**
	 * Testing the behavior of terminateClient
	 */
	@Test
	public void testTerminateClient() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator1();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "In testTerminateClient");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, 5, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.terminateClient();
		networkConnection.close();

	}

	/**
	* Testing with an array of terminate messages
	*/
	@Test
	public void testTerminateMessages() {
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator3();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "In testTerminateMessages");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, 5, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.run();
		networkConnection.close();

	}

	/**
	 * Testing clientRunnable with an array of 0 messages
	 */
	@Test
	public void testEmptyMessageQueue() {
	
		Iterator<Message> messageIterator = NetworkConnectionTestUtil.getMessageIterator4();
		when(networkConnection.iterator()).thenReturn(messageIterator);
		clientRunnable = new ClientRunnable(networkConnection);
		clientRunnable.run();
		logger.log(Level.INFO, ""+clientRunnable.getUserId());
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		Runnable task2 = () -> logger.log(Level.INFO, "Starting new task");
		ScheduledFuture<?> scheduledFuture = ses.schedule(task2, 5, TimeUnit.SECONDS);
		clientRunnable.setFuture(scheduledFuture);
		clientRunnable.run();
		networkConnection.close();

	}
	
    

}
