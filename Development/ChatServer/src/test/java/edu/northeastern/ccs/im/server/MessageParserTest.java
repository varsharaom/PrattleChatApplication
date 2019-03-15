package edu.northeastern.ccs.im.server;


import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.utils.ClientRunnableHelperUtil;
import edu.northeastern.ccs.im.utils.MessageUtil;
import edu.northeastern.ccs.serverim.Message;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageParserTest {

    private ClientRunnableHelper clientRunnableHelper;

    @Before
    public void beforeEach() {
        clientRunnableHelper = new ClientRunnableHelper(null);
    }

    @Test
    public void testParseValidRegisterMessage() {
        Message message = MessageUtil.getValidRegisterBroadcastMessage();
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isRegisterMessage());
        assertNotNull("Constructed message content is empty", message.getText());

        String[] content = message.getText().split(" ");
        assertEquals(3, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidRegisterMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);

        assertEquals(constructedMessage.getText(), content[2]);
    }

    @Test
    public void testParseValidLoginMessage() {
        Message message = MessageUtil.getValidLoginBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isLoginMessage());
        assertNotNull("Constructed message content is empty", message.getText());

        String[] content = message.getText().split(" ");
        assertEquals(3, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidLoginMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);

        assertEquals(constructedMessage.getText(), content[2]);
    }

    @Test
    public void testParseValidDirectMessage() {
        Message message = MessageUtil.getValidDirectBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isDirectMessage());
        assertNotNull("Constructed message content is empty", message.getText());

        String[] content = message.getText().split(" ", 4);
        assertEquals(4, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidDirectMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);
        assertEquals(constructedMessage.getMsgReceiver(), content[2]);
        assertEquals(constructedMessage.getText(), content[3]);
    }

    @Test
    public void testParseValidGroupMessage() {
        Message message = MessageUtil.getValidGroupBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isGroupMessage());
        assertNotNull(message.getText());

        String[] content = message.getText().split(" ", 4);
        assertEquals(4, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidGroupMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);
        assertEquals(constructedMessage.getMsgReceiver(), content[2]);
        assertEquals(constructedMessage.getText(), content[3]);

    }

    @Test
    public void testParseValidDeleteMessage() {
        Message message = MessageUtil.getValidDeleteBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isDeleteMessage());

        String[] content = message.getText().split(" ", 4);
        assertEquals(4, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidDeleteMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);
        assertEquals(constructedMessage.getMsgReceiver(), content[2]);
        assertEquals(constructedMessage.getId(), Long.parseLong(content[3]));
    }

}
