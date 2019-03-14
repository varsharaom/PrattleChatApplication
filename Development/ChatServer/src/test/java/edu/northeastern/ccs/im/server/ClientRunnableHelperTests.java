package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.utils.MessageUtil;
import edu.northeastern.ccs.serverim.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientRunnableHelperTests {

    @InjectMocks
    ClientRunnableHelper clientRunnableHelper;

    @Mock
    IQueryHandler iQueryHandler;

    @Test
    public void testHandleRegisterMessageWithValidCredentials() {
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

        when(iQueryHandler.validateLogin(anyString(), anyString()))
                .thenReturn(true);
        clientRunnableHelper.handleMessages(loginMessage);

    }

    @Test
    public void testHandleInvalidLogin() {
        Message message = MessageUtil.getValidLoginBroadcastMessage();
//       the text is a compound string sent from client which in turn is used as invalid password
        when(iQueryHandler.validateLogin(anyString(),anyString())).thenReturn(false);
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
    public void testHandleGroupMessage() {
        Message message = MessageUtil.getValidGroupBroadcastMessage();

        when(iQueryHandler.checkUserNameExists(anyString())).thenReturn(true);
        Message groupMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        clientRunnableHelper.handleMessages(groupMessage);

    }
}
