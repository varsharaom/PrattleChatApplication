package edu.northeastern.ccs.im.client;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {


    @Test
    public void testMakeLoginMessage() {
        String message = "LOGIN_TEST";
        String loginMSG = "HLO 10 LOGIN_TEST 2 --";
        Message loginMessage = Message.makeLoginMessage(message);
        assertEquals(message, loginMessage.getSender());
        assertEquals(Message.MessageType.HELLO, loginMessage.getType());
        assertNull(loginMessage.getText());
        assertEquals(loginMSG, loginMessage.toString());
        assertFalse(loginMessage.isAcknowledge());
        assertFalse(loginMessage.isBroadcastMessage());
        assertFalse(loginMessage.isDisplayMessage());
        assertTrue(loginMessage.isInitialization());
        assertFalse(loginMessage.terminate());
    }

    @Test
    public void testMakeQuitMessage() {
        String message = "QUIT_TEST";
        String stringMessage = "BYE 9 QUIT_TEST 2 --";
        Message quitMessage = Message.makeQuitMessage(message);
        assertEquals(message, quitMessage.getSender());
        assertEquals(Message.MessageType.QUIT, quitMessage.getType());
        assertNull(quitMessage.getText());
        assertEquals(stringMessage, quitMessage.toString());
        assertFalse(quitMessage.isAcknowledge());
        assertFalse(quitMessage.isBroadcastMessage());
        assertFalse(quitMessage.isDisplayMessage());
        assertFalse(quitMessage.isInitialization());
        assertTrue(quitMessage.terminate());
    }

    @Test
    public void testMakeHelloMessage() {
        String message = "HELLO_TEST";
        String stringMessage = "HLO 2 -- 10 HELLO_TEST";
        Message helloMessage = Message.makeHelloMessage(message);
        assertNull(helloMessage.getSender());
        assertEquals(Message.MessageType.HELLO, helloMessage.getType());
        assertEquals(message, helloMessage.getText());
        assertEquals(stringMessage, helloMessage.toString());
        assertFalse(helloMessage.isAcknowledge());
        assertFalse(helloMessage.isBroadcastMessage());
        assertFalse(helloMessage.isDisplayMessage());
        assertTrue(helloMessage.isInitialization());
        assertFalse(helloMessage.terminate());
    }

    @Test
    public void testMakeMessageBroadCast() {
        String message = "GENERAL_MESSAGE";
        String senderName = "Client1";
        String stringMessage = "BCT 7 Client1 15 GENERAL_MESSAGE";
        Message generalMessage = Message.makeMessage(
                Message.MessageType.BROADCAST.toString(), senderName, message);
        assertEquals(senderName, generalMessage.getSender());
        assertEquals(Message.MessageType.BROADCAST, generalMessage.getType());
        assertEquals(message, generalMessage.getText());
        assertEquals(stringMessage, generalMessage.toString());
        assertFalse(generalMessage.isAcknowledge());
        assertTrue(generalMessage.isBroadcastMessage());
        assertTrue(generalMessage.isDisplayMessage());
        assertFalse(generalMessage.isInitialization());
        assertFalse(generalMessage.terminate());
    }

    @Test
    public void testMakeMessageQuit() {
        String message = "GENERAL_MESSAGE_QUIT";
        String senderName = "Client1";
        String responseMsg = "BYE 7 Client1 2 --";
        Message generalMessage = Message.makeMessage(
                Message.MessageType.QUIT.toString(), senderName, message);
        assertEquals(senderName, generalMessage.getSender());
        assertEquals(Message.MessageType.QUIT, generalMessage.getType());
        assertNull(generalMessage.getText());
        assertEquals(responseMsg, generalMessage.toString());
        assertFalse(generalMessage.isAcknowledge());
        assertFalse(generalMessage.isBroadcastMessage());
        assertFalse(generalMessage.isDisplayMessage());
        assertFalse(generalMessage.isInitialization());
        assertTrue(generalMessage.terminate());
    }

    @Test
    public void testMakeMessageHello() {
        String message = "GENERAL_MESSAGE_HELLO";
        String senderName = "Client1";
        String stringMessage = "HLO 7 Client1 2 --";
        Message generalMessage = Message.makeMessage(
                Message.MessageType.HELLO.toString(), senderName, message);
        assertEquals(senderName, generalMessage.getSender());
        assertEquals(Message.MessageType.HELLO, generalMessage.getType());
        assertNull(generalMessage.getText());
        assertEquals(stringMessage, generalMessage.toString());
        assertFalse(generalMessage.isAcknowledge());
        assertFalse(generalMessage.isBroadcastMessage());
        assertFalse(generalMessage.isDisplayMessage());
        assertTrue(generalMessage.isInitialization());
        assertFalse(generalMessage.terminate());
    }

    @Test
    public void testMakeMessageACK() {
        String message = "GENERAL_MESSAGE_ACK";
        String msgResponse = "ACK 7 Client1 2 --";
        String senderName = "Client1";
        Message generalMessage = Message.makeMessage(
                Message.MessageType.ACKNOWLEDGE.toString(), senderName, message);
        assertEquals(senderName, generalMessage.getSender());
        assertEquals(Message.MessageType.ACKNOWLEDGE, generalMessage.getType());
        assertNull(generalMessage.getText());
        assertEquals(msgResponse, generalMessage.toString());
        assertTrue(generalMessage.isAcknowledge());
        assertFalse(generalMessage.isBroadcastMessage());
        assertFalse(generalMessage.isDisplayMessage());
        assertFalse(generalMessage.isInitialization());
        assertFalse(generalMessage.terminate());
    }

    @Test
    public void testMakeMessageNACK() {
        String message = "GENERAL_MESSAGE_NACK";
        String senderName = "Client1";
        String responseMsg = "NAK 2 -- 2 --";
        Message generalMessage = Message.makeMessage(
                Message.MessageType.NO_ACKNOWLEDGE.toString(), senderName, message);
        assertNull(generalMessage.getSender());
        assertEquals(Message.MessageType.NO_ACKNOWLEDGE, generalMessage.getType());
        assertNull(generalMessage.getText());
        assertEquals(responseMsg, generalMessage.toString());
        assertFalse(generalMessage.isAcknowledge());
        assertFalse(generalMessage.isBroadcastMessage());
        assertFalse(generalMessage.isDisplayMessage());
        assertFalse(generalMessage.isInitialization());
        assertFalse(generalMessage.terminate());
    }

    @Test
    public void testMakeMessageWithNullSender() {
        String message = "GENERAL_MESSAGE";
        String stringMessage = "BCT 2 -- 15 GENERAL_MESSAGE";
        Message generalMessage = Message.makeMessage(
                Message.MessageType.BROADCAST.toString(), null, message);
        assertNull(generalMessage.getSender());
        assertEquals(Message.MessageType.BROADCAST, generalMessage.getType());
        assertEquals(message, generalMessage.getText());
        assertEquals(stringMessage, generalMessage.toString());
        assertFalse(generalMessage.isAcknowledge());
        assertTrue(generalMessage.isBroadcastMessage());
        assertTrue(generalMessage.isDisplayMessage());
        assertFalse(generalMessage.isInitialization());
        assertFalse(generalMessage.terminate());
    }





}