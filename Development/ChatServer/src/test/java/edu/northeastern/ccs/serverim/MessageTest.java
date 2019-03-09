package edu.northeastern.ccs.serverim;

import edu.northeastern.ccs.im.constants.MessageConstants;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testMakeMessageBroadcast() {
        Message message = Message.makeMessage(MessageType.BROADCAST.toString(), MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        assertTrue(message.isBroadcastMessage());
    }

    @Test
    public void testMakeMessageQuit() {
        Message message = Message.makeMessage(MessageType.QUIT.toString(), MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        assertFalse(message.isBroadcastMessage());
    }

    @Test
    public void testMakeMessageHello() {
        Message message = Message.makeMessage(MessageType.HELLO.toString(), MessageConstants.SIMPLE_USER,
                MessageConstants.BROADCAST_TEXT_MESSAGE);
        assertFalse(message.isBroadcastMessage());
    }

    @Test
    public void makeHelloMessage() {

        Message message = Message.makeHelloMessage(MessageConstants.BROADCAST_TEXT_MESSAGE);
        String msgAsString = message.toString();
        String[] msgStringContent = msgAsString.split(" ");

        assertEquals(MessageType.HELLO.toString(), msgStringContent[0]);

    }

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
        assertTrue(message.isInitialization());
    }

    @Test
    public void testToStringNullSender() {
        Message message = Message.makeBroadcastMessage(null,
                MessageConstants.BROADCAST_TEXT_MESSAGE);

        String msgAsString = message.toString();
        String[] msgStringContent = msgAsString.split(" ");

        assertEquals(MessageType.BROADCAST.toString(), msgStringContent[0]);
        assertEquals(MessageConstants.NULL_OUTPUT.length(), Integer.parseInt(msgStringContent[1]));
        assertEquals(MessageConstants.NULL_OUTPUT, msgStringContent[2]);
        assertEquals(MessageConstants.BROADCAST_TEXT_MESSAGE.length(),
                Integer.parseInt(msgStringContent[3]));
        assertEquals(MessageConstants.BROADCAST_TEXT_MESSAGE, msgStringContent[4]);
    }

    @Test
    public void testToStringNullMessage() {
        Message message = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER, null);

        String msgAsString = message.toString();
        String[] msgStringContent = msgAsString.split(" ");

        assertEquals(MessageType.BROADCAST.toString(), msgStringContent[0]);
        assertEquals(MessageConstants.SIMPLE_USER.length(), Integer.parseInt(msgStringContent[1]));
        assertEquals(MessageConstants.SIMPLE_USER, msgStringContent[2]);
        assertEquals(MessageConstants.NULL_OUTPUT.length(),
                Integer.parseInt(msgStringContent[3]));
        assertEquals(MessageConstants.NULL_OUTPUT, msgStringContent[4]);
    }

    @Test
    public void testIsBroadcastMessage() {
    	Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER, null);
    	Message message2 = Message.makeSimpleLoginMessage(MessageConstants.SIMPLE_USER);
    	assertTrue(message1.isBroadcastMessage());
    	assertFalse(message2.isBroadcastMessage());
    }
    
    @Test
    public void testBroadcastInitialization() {
    	Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER, null);
    	Message message2 = Message.makeSimpleLoginMessage(MessageConstants.SIMPLE_USER);
    	assertTrue(message2.isInitialization());
    	assertFalse(message1.isInitialization());
    }
    
    @Test
    public void testTerminate() {
    	Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER, null);
    	Message message2 = Message.makeQuitMessage(MessageConstants.SIMPLE_USER);
    	assertTrue(message2.terminate());
    	assertFalse(message1.terminate());
    }
    
    @Test
	public void testNullHandle() {
		Message message1 = Message.makeMessage(MessageConstants.SIMPLE_USER, MessageConstants.SIMPLE_USER,
				MessageConstants.SIMPLE_USER);
		assertNull(message1);		
	}

}
