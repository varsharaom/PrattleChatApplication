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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableTest {

    @InjectMocks
    ClientRunnable clientRunnable;

    @Mock
    NetworkConnection networkConnection;

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

}
