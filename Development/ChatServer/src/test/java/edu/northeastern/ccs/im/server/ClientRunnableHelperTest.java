package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
import edu.northeastern.ccs.im.constants.ClientRunnableHelperConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.DBConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.utils.MessageUtil;
import edu.northeastern.ccs.im.utils.NetworkConnectionTestUtil;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.NetworkConnection;
import edu.northeastern.ccs.serverim.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableHelperTest {

    @InjectMocks
    ClientRunnable clientRunnable;

    @Mock
    NetworkConnection networkConnection;

    @InjectMocks
    ClientRunnableHelper clientRunnableHelper;

    @Mock
    IQueryHandler iQueryHandler;

    private SocketChannel sc;
    private NetworkConnection nc;

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
    public void testHandleLoginMessageQueued() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        ClientRunnable tt = new ClientRunnable(nc);
        tt.setName(MessageConstants.SIMPLE_USER);
        queue.add(tt);
        Field active = Prattle.class.getDeclaredField(ClientRunnableHelperConstants.ACTIVE_FIELD);
        active.setAccessible(true);
        active.set(null, queue);

        Queue<Message> messagesQueue = new ConcurrentLinkedQueue<>();
        Message message = MessageUtil.getValidLoginBroadcastMessage();
        messagesQueue.add(message);

        Class ncClass = nc.getClass();
        Field messages = ncClass.getDeclaredField(ClientRunnableHelperConstants.MESSAGES_FIELD);
        messages.setAccessible(true);
        messages.set(nc, messagesQueue);

        tt.run();

        when(iQueryHandler.validateLogin(anyString(), anyString())).thenReturn(1L);
        Message loginMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(loginMessage);
    }

    @Test
    public void testHandleLoginMessageNotQueued() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        ClientRunnable tt = new ClientRunnable(nc);
        tt.setName(MessageConstants.SIMPLE_USER);
        queue.add(tt);
        Field active = Prattle.class.getDeclaredField(ClientRunnableHelperConstants.ACTIVE_FIELD);
        active.setAccessible(true);
        active.set(null, queue);

        Queue<Message> messagesQueue = new ConcurrentLinkedQueue<>();
        Message message = MessageUtil.getValidLoginBroadcastMessageWithDifferentUser();
        messagesQueue.add(message);

        Class ncClass = nc.getClass();
        Field messages = ncClass.getDeclaredField(ClientRunnableHelperConstants.MESSAGES_FIELD);
        messages.setAccessible(true);
        messages.set(nc, messagesQueue);

        tt.run();

        when(iQueryHandler.validateLogin(anyString(), anyString())).thenReturn(1L);
        Message loginMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(loginMessage);
    }

    @Test
    public void testHandleLoginMessageNotInitialized() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        when(networkConnection.iterator()).thenReturn(NetworkConnectionTestUtil.getMessageIterator());
        clientRunnable.setName(MessageConstants.SIMPLE_USER);
        queue.add(clientRunnable);
        Field active = Prattle.class.getDeclaredField(ClientRunnableHelperConstants.ACTIVE_FIELD);
        active.setAccessible(true);
        active.set(null, queue);

        Message message = MessageUtil.getValidLoginBroadcastMessageWithDifferentUser();

        clientRunnable.run();

        when(iQueryHandler.validateLogin(anyString(), anyString())).thenReturn(1L);
        Message loginMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(loginMessage);
    }

    @Test
    public void testHandleRegisterMessage() {
        Message message = MessageUtil.getValidRegisterBroadcastMessage();

        when(iQueryHandler.checkUserNameExists(anyString())).thenReturn(false);
        when(iQueryHandler.createUser(anyString(), anyString(), anyString())).thenReturn(null);
        Message registerMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(registerMessage);

    }

    @Test
    public void testHandleRegisterMessageWithInvalidCredentials() {
        Message message = MessageUtil.getValidRegisterBroadcastMessage();

        when(iQueryHandler.checkUserNameExists(anyString())).thenReturn(true);
        when(iQueryHandler.createUser(anyString(), anyString(), anyString())).thenReturn(null);
        Message registerMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(registerMessage);

    }

    @Test
    public void testHandleValidLogin() {
        Message message = MessageUtil.getValidLoginBroadcastMessage();
        Message loginMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        clientRunnableHelper.handleMessages(loginMessage);

    }

    @Test
    public void testHandleInvalidLogin() {
        Message message = MessageUtil.getValidLoginBroadcastMessage();
//       the text is a compound string sent from client which in turn is used as invalid password
        Message loginMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(loginMessage);
    }

    @Test
    public void testHandleDirectMessage() {
        Message message = MessageUtil.getValidDirectBroadcastMessage();

        when(iQueryHandler.checkUserNameExists(anyString())).thenReturn(true);
        Message directMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(directMessage);
    }

    @Test
    public void testHandleDirectMessageQueued() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        ClientRunnable tt = new ClientRunnable(nc);
        tt.setName(MessageConstants.SIMPLE_USER);
        queue.add(tt);
        Field active = Prattle.class.getDeclaredField(ClientRunnableHelperConstants.ACTIVE_FIELD);
        active.setAccessible(true);
        active.set(null, queue);

        Queue<Message> messagesQueue = new ConcurrentLinkedQueue<>();
        Message message = MessageUtil.getValidDirectBroadcastMessage();
        messagesQueue.add(message);

        Class ncClass = nc.getClass();
        Field messages = ncClass.getDeclaredField("messages");
        messages.setAccessible(true);
        messages.set(nc, messagesQueue);

        tt.run();

        Message directMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(directMessage);
    }

    @Test
    public void testHandleDirectMessageNotQueued() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        ClientRunnable tt = new ClientRunnable(nc);
        tt.setName(MessageConstants.SIMPLE_USER);
        queue.add(tt);
        Field active = Prattle.class.getDeclaredField("active");
        active.setAccessible(true);
        active.set(null, queue);

        Queue<Message> messagesQueue = new ConcurrentLinkedQueue<>();
        Message message = MessageUtil.getValidDirectBroadcastMessageDifferentUser();
        messagesQueue.add(message);

        Class ncClass = nc.getClass();
        Field messages = ncClass.getDeclaredField("messages");
        messages.setAccessible(true);
        messages.set(nc, messagesQueue);

        tt.run();

        Message directMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(directMessage);
    }

    @Test
    public void testHandleDirectMessageNotInitialized() throws NoSuchFieldException, IllegalAccessException {
        ConcurrentLinkedQueue<ClientRunnable> queue = new ConcurrentLinkedQueue<>();

        when(networkConnection.iterator()).thenReturn(NetworkConnectionTestUtil.getMessageIterator());
        clientRunnable.setName(MessageConstants.SIMPLE_USER);
        queue.add(clientRunnable);
        Field active = Prattle.class.getDeclaredField("active");
        active.setAccessible(true);
        active.set(null, queue);

        Message message = MessageUtil.getValidDirectBroadcastMessageDifferentUser();

        clientRunnable.run();

        Message directMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(directMessage);
    }

    @Test
    public void testHandleGroupMessage() {
        Message message = MessageUtil.getValidGroupBroadcastMessage();

        when(iQueryHandler.checkUserNameExists(anyString())).thenReturn(true);
        Message groupMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(groupMessage);

    }
    
    @Test
    public void testHandleGetUsersMessage() {
        Message message = MessageUtil.getValidGetUsersMessage();

        List<User> list = new ArrayList();
        list.add(new User(1L, DBConstants.USER_USERNAME, DBConstants.USER_USERNAME, System.currentTimeMillis()));
        when(iQueryHandler.getAllUsers()).thenReturn(list);
        Message getUsersMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(getUsersMessage);
    }

    @Test
    public void testEmptyMessageContent() {
        Message message = MessageUtil.getEmptyBroadcastMessage();
        Message invalidMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(MessageUtil.isErrorMessage(invalidMessage));
    }

    @Test
    public void testInvalidMessagePrefix() {
        Message message = MessageUtil.getInvalidPrefixBroadcastMessage();
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertEquals(message, constructedMessage);
    }

    @Test
    public void testInvalidMessageType() {
        Message message = MessageUtil.getInvalidMessageWithInvalidType();
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(MessageUtil.isErrorMessage(constructedMessage));
    }

    @Test
    public void testHandleDeleteMessage() {
        Message message = MessageUtil.getValidDeleteBroadcastMessage();

        Message groupMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(groupMessage);

    }

}
