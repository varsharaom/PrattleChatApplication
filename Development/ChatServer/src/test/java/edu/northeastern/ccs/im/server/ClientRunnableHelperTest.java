package edu.northeastern.ccs.im.server;


import edu.northeastern.ccs.im.utils.ClientRunnableHelperUtil;
import edu.northeastern.ccs.im.utils.MessageUtil;
import edu.northeastern.ccs.serverim.Message;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClientRunnableHelperTest {

    @Test
    public void testParseValidRegisterMessage() {
        Message message = MessageUtil.getValidRegisterBroadcastMessage();

        ClientRunnableHelper clientRunnableHelper = new ClientRunnableHelper(null, null);
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isRegisterMessage());
        assertNotNull("Constructed message content is empty", message.getText());

        String[] content = message.getText().split(" ");
        assertEquals(3, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidRegisterMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getMsgSender(), content[1]);

        assertEquals(constructedMessage.getText(), content[2]);
    }

    @Test
    public void testParseValidLoginMessage() {
        Message message = MessageUtil.getValidLoginBroadcastMessage();

        ClientRunnableHelper clientRunnableHelper = new ClientRunnableHelper(null, null);
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);


        assertTrue(constructedMessage.isLoginMessage());
        assertNotNull("Constructed message content is empty", message.getText());

        String[] content = message.getText().split(" ");
        assertEquals(3, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidLoginMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getMsgSender(), content[1]);

        assertEquals(constructedMessage.getText(), content[2]);
    }

}
