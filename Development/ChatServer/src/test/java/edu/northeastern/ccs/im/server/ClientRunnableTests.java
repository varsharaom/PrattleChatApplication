package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.NetworkConnection;
import edu.northeastern.ccs.im.TestUtils.NetworkConnectionTestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Iterator;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableTests {

    @InjectMocks
    ClientRunnable clientRunnable;

    @Mock
    NetworkConnection networkConnection;

    Iterator<Message> messageIterator;

    @Test
    public void testValidClient() {
        messageIterator = NetworkConnectionTestUtil.getMessageIterator();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        clientRunnable.run();
        networkConnection.close();

    }

    @Test
    public void testClientWithNullUserName() {
        messageIterator = NetworkConnectionTestUtil.getMessageIteratorWithNoUsers();

        when(networkConnection.iterator()).thenReturn(messageIterator);
        clientRunnable = new ClientRunnable(networkConnection);
        clientRunnable.run();
        clientRunnable.run();
        networkConnection.close();

    }
}
