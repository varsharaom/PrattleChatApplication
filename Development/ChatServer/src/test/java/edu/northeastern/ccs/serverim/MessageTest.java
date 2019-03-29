package edu.northeastern.ccs.serverim;

import org.junit.Test;

import static edu.northeastern.ccs.im.constants.MessageTestConstants.*;
import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testMakeMessageBroadcast() {
        Message message = Message.makeMessage(MessageType.BROADCAST.toString(), SIMPLE_USER,
                BROADCAST_TEXT_MESSAGE);
        assertTrue(message.isBroadcastMessage());
    }

    @Test
    public void testMakeMessageQuit() {
        Message message = Message.makeMessage(MessageType.QUIT.toString(), SIMPLE_USER,
                BROADCAST_TEXT_MESSAGE);
        assertFalse(message.isBroadcastMessage());
    }

    @Test
    public void testMakeMessageHello() {
        Message message = Message.makeMessage(MessageType.HELLO.toString(), SIMPLE_USER,
                BROADCAST_TEXT_MESSAGE);
        assertFalse(message.isBroadcastMessage());
    }

    @Test
    public void makeHelloMessage() {

        Message message = Message.makeHelloMessage(BROADCAST_TEXT_MESSAGE);
        String msgAsString = message.toString();
        String[] msgStringContent = msgAsString.split(" ");

        assertEquals(MessageType.HELLO.toString(), msgStringContent[0]);

    }

    @Test
    public void testMakeBroadcastMessage() {
        Message message = Message.makeBroadcastMessage(SIMPLE_USER, BROADCAST_TEXT_MESSAGE);

        assertEquals(SIMPLE_USER, message.getName());
        assertEquals(BROADCAST_TEXT_MESSAGE, message.getText());
        assertTrue(message.isBroadcastMessage());
    }


    @Test
    public void testMakeQuitMessage() {
        Message message = Message.makeQuitMessage(SIMPLE_USER);
        assertEquals(SIMPLE_USER, message.getName());

        assertTrue(message.terminate());
    }


    @Test
    public void testSimpleLoginMessage() {
        String userName = SIMPLE_USER;
        Message message = Message.makeSimpleLoginMessage(userName);
        assertEquals(userName, message.getName());
        assertTrue(message.isInitialization());
    }

    @Test
    public void testToStringNullSender() {
        Message message = Message.makeBroadcastMessage(null, BROADCAST_TEXT_MESSAGE);

        String msgAsString = message.toString();
        String[] msgStringContent = msgAsString.split(" ");

        assertEquals(MessageType.BROADCAST.toString(), msgStringContent[0]);
        assertEquals(NULL_OUTPUT.length(), Integer.parseInt(msgStringContent[1]));
        assertEquals(NULL_OUTPUT, msgStringContent[2]);
        assertEquals(BROADCAST_TEXT_MESSAGE.length(),
                Integer.parseInt(msgStringContent[3]));
        assertEquals(BROADCAST_TEXT_MESSAGE, msgStringContent[4]);
    }

    @Test
    public void testToStringNullMessage() {
        Message message = Message.makeBroadcastMessage(SIMPLE_USER, null);

        String msgAsString = message.toString();
        String[] msgStringContent = msgAsString.split(" ");

        assertEquals(MessageType.BROADCAST.toString(), msgStringContent[0]);
        assertEquals(SIMPLE_USER.length(), Integer.parseInt(msgStringContent[1]));
        assertEquals(SIMPLE_USER, msgStringContent[2]);
        assertEquals(NULL_OUTPUT.length(),
                Integer.parseInt(msgStringContent[3]));
        assertEquals(NULL_OUTPUT, msgStringContent[4]);
    }

    @Test
    public void testIsBroadcastMessage() {
        Message message1 = Message.makeBroadcastMessage(SIMPLE_USER, null);
        Message message2 = Message.makeSimpleLoginMessage(SIMPLE_USER);
        assertTrue(message1.isBroadcastMessage());
        assertFalse(message2.isBroadcastMessage());
    }

    @Test
    public void testBroadcastInitialization() {
        Message message1 = Message.makeBroadcastMessage(SIMPLE_USER, null);
        Message message2 = Message.makeSimpleLoginMessage(SIMPLE_USER);
        assertTrue(message2.isInitialization());
        assertFalse(message1.isInitialization());
    }

    @Test
    public void testTerminate() {
        Message message1 = Message.makeBroadcastMessage(SIMPLE_USER, null);
        Message message2 = Message.makeQuitMessage(SIMPLE_USER);
        assertTrue(message2.terminate());
        assertFalse(message1.terminate());
    }

    @Test
    public void testNullHandle() {
        Message message1 = Message.makeMessage(SIMPLE_USER, SIMPLE_USER,
                SIMPLE_USER);
        assertNull(message1);
    }

}
