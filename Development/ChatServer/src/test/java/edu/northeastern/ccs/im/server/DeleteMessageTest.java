package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static edu.northeastern.ccs.im.utils.MessageUtil.getValidDeleteBroadcastMessage;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteMessageTest {

    @InjectMocks
    ClientRunnableHelper clientRunnableHelper;

    @Mock
    IQueryHandler queryHandler;

    @Test
    public void testValidDelete() {
        Message message = getValidDeleteBroadcastMessage();
        Message customConstructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.getMessage(anyLong())).thenReturn(customConstructedMessage);
        clientRunnableHelper.handleMessages(customConstructedMessage);
    }

    @Test
    public void testSenderInvalid() {
        Message message = getValidDeleteBroadcastMessage();
        Message customConstructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        Message dbMessage = new Message(customConstructedMessage.getId(), MessageType.DELETE,
                customConstructedMessage.getMsgReceiver(), customConstructedMessage.getMsgReceiver(),
                "", 0);

        when(queryHandler.getMessage(anyLong())).thenReturn(dbMessage);
        clientRunnableHelper.handleMessages(customConstructedMessage);
    }

    @Test
    public void testReceiverInvalid() {
        Message message = getValidDeleteBroadcastMessage();
        Message customConstructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        Message dbMessage = new Message(customConstructedMessage.getId(), MessageType.DELETE,
                customConstructedMessage.getName(), customConstructedMessage.getName(),
                "", 0);
        when(queryHandler.getMessage(anyLong())).thenReturn(dbMessage);
        clientRunnableHelper.handleMessages(customConstructedMessage);
    }


}
