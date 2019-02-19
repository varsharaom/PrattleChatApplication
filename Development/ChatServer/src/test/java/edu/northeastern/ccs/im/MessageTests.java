package edu.northeastern.ccs.im;

import edu.northeastern.ccs.im.TestConstants.MessageConstants;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTests {

    @Test
    public void testMakeBroadcastMessage() {
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);

        assertEquals(MessageConstants.SIMPLE_USER, message.getName());
        assertEquals(MessageConstants.BROADCAST_TEXT_MESSAGE, message.getText());
        assertTrue(message.isBroadcastMessage());
    }

    @Test
    public void testMakeQuitMessage() {
        Message message = Message.makeQuitMessage(MessageConstants.SIMPLE_USER);
        assertEquals(MessageConstants.SIMPLE_USER, message.getName());
        assertTrue(message.terminate());
    }

    @Test
    public void testSimpleLoginMessage() {
        String userName = MessageConstants.SIMPLE_USER;
        Message message = Message.makeSimpleLoginMessage(userName);
        assertEquals(userName, message.getName());
        assertNull(message.getText());
        assertTrue(message.isInitialization());
    }
}
