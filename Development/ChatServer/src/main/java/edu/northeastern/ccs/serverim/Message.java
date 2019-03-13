package edu.northeastern.ccs.serverim;


import edu.northeastern.ccs.im.datahandler.EntityHandler;

import java.util.Map;
import java.util.Set;

/**
 * Each instance of this class represents a single transmission by our IM
 * clients.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.3
 */
public class Message implements IMessage{

	/** The string sent when a field is null. */
	private static final String NULL_OUTPUT = "--";

	/** The handle of the message. */
	private MessageType msgType;

	/** The sender's user id*/
	private long senderId;

	/** This is either an user's id or a group's id - based on private or group message*/
	private long receiverId;

	/**
	 * The first argument used in the message. This will be the sender's identifier.
	 */
	private String msgSender;

	private String msgReceiver;

	/** The second argument used in the message. */
	private String msgText;

	private Set<String> groupMembers;


	/** The sender's unique user id */
	public long getSenderId() {
		return senderId;
	}

	/** The receiver's unique user id */
	public long getReceiverId() {
		return receiverId;
	}
	

	/**
	 * Create a new message that contains actual IM text. The type of distribution
	 * is defined by the handle and we must also set the name of the message sender,
	 * message recipient, and the text to send.
	 * 
	 * @param handle  Handle for the type of message being created.
//	 * @param srcName Name of the individual sending this message
	 * @param text    Text of the instant message
	 */
	private Message(MessageType handle, String srcName, String text) {
		msgType = handle;
		// Save the properly formatted identifier for the user sending the
		// message.
		msgSender = srcName;
		// Save the text of the message.
		msgText = text;
	}

	private Message(MessageType handle, long senderId, String text) {
		this.msgType = handle;
		this.senderId = senderId;
		this.msgText = text;
	}

	public Message(MessageType handle, String sender, String receiver, String text) {
		this.msgType = handle;
		this.msgSender = sender;
		this.msgReceiver = receiver;
		this.msgText = text;
	}


	/**
	 * Create a new message that contains a command sent the server that requires a
	 * single argument. This message contains the given handle and the single
	 * argument.
	 * 
	 * @param handle  Handle for the type of message being created.
	 * @param srcName Argument for the message; at present this is the name used to
	 *                log-in to the IM server.
	 */
	private Message(MessageType handle, String srcName) {
		this(handle, srcName, "");
	}

	/**
	 * Create a new message to continue the logout process.
	 * 
	 * @param myName The name of the client that sent the quit message.
	 * @return Instance of Message that specifies the process is logging out.
	 */
	public static Message makeQuitMessage(String myName) {
		return new Message(MessageType.QUIT, myName, "");
	}

	/**
	 * Create a new message broadcasting an announcement to the world.
	 * 
	 * @param myName Name of the sender of this very important missive.
	 * @param text   Text of the message that will be sent to all users
	 * @return Instance of Message that transmits text to all logged in users.
	 */
	public static Message makeBroadcastMessage(String myName, String text) {
		return new Message(MessageType.BROADCAST, myName, text);
	}

	public String getMsgSender() {
		return msgSender;
	}

	public String getMsgReceiver() {
		return msgReceiver;
	}

	public MessageType getMessageType() {
		return msgType;
	}

	/**
	 * Create a new message stating the name with which the user would like to
	 * login.
	 * 
	 * @param text Name the user wishes to use as their screen name.
	 * @return Instance of Message that can be sent to the server to try and login.
	 */
	static Message makeHelloMessage(String text) {
		return new Message(MessageType.HELLO, null, text);
	}

	public static Message makeRegisterMessage(String userName, String password) {
		return new Message(MessageType.REGISTER, userName, password);
	}

	public static Message makeLoginMessage(String userName, String password) {
		return new Message(MessageType.LOGIN, userName, password);
	}

	public static Message makeLoginAckMessage(MessageType handle, String sender, String receiver, String msgText) {
		return new Message(handle, sender, receiver, msgText);
	}

	public static Message makeRegisterAckMessage(MessageType handle, String msgSender, String msgText) {
		return new Message(handle, msgSender, msgText);
	}

	public static Message makeDirectMessage(String msgSender, String msgReceiver, String msgText) {
		return new Message(MessageType.DIRECT, msgSender, msgReceiver, msgText);
	}

	public static Message makeGroupMessage(String msgSender, String groupName, String msgText) {
		return new Message(MessageType.GROUP, msgSender, groupName, msgText);
	}

	/**
	 * Given a handle, name and text, return the appropriate message instance or an
	 * instance from a subclass of message.
	 * 
	 * @param handle  Handle of the message to be generated.
	 * @param srcName Name of the originator of the message (may be null)
	 * @param text    Text sent in this message (may be null)
	 * @return Instance of Message (or its subclasses) representing the handle,
	 *         name, & text.
	 */
	static Message makeMessage(String handle, String srcName, String text) {
		Message result = null;
		if (handle.compareTo(MessageType.QUIT.toString()) == 0) {
			result = makeQuitMessage(srcName);
		} else if (handle.compareTo(MessageType.HELLO.toString()) == 0) {
			result = makeSimpleLoginMessage(srcName);
		} else if (handle.compareTo(MessageType.BROADCAST.toString()) == 0) {
			result = makeBroadcastMessage(srcName, text);
		}
		return result;
	}

//	TODO - talk to team and see if this block is actually needed
	protected static Message makeMessage(MessageType handle, long senderId, String msgText) {

		Message result = null;
//
//		if (handle == MessageType.LOGIN) {
//			result = makeLoginAckMessage(handle, senderId, msgText);
//		}
//
		return result;
	}


	/**
	 * Create a new message for the early stages when the user logs in without all
	 * the special stuff.
	 * 
	 * @param myName Name of the user who has just logged in.
	 * @return Instance of Message specifying a new friend has just logged in.
	 */
	public static Message makeSimpleLoginMessage(String myName) {
		return new Message(MessageType.HELLO, myName);
	}

	/**
	 * Return the name of the sender of this message.
	 * 
	 * @return String specifying the name of the message originator.
	 */
	public String getName() {
		return msgSender;
	}

	/**
	 * Return the text of this message.
	 * 
	 * @return String equal to the text sent by this message.
	 */
	public String getText() {
		return msgText;
	}

	/**
	 * Determine if this message is broadcasting text to everyone.
	 * 
	 * @return True if the message is a broadcast message; false otherwise.
	 */
	boolean isBroadcastMessage() {
		return (msgType == MessageType.BROADCAST);
	}

	/**
	 * Determine if this message is sent by a new client to log-in to the server.
	 * 
	 * @return True if the message is an initialization message; false otherwise
	 */
	boolean isInitialization() {
		return (msgType == MessageType.HELLO);
	}

	public boolean isRegisterMessage() {
		return (msgType == MessageType.REGISTER);
	}

	public boolean isLoginMessage() {
		return (msgType == MessageType.LOGIN);
	}

	public boolean isDirectMessage() {
		return (msgType == MessageType.DIRECT);
	}

	public boolean isGroupMessage() {
		return (msgType == MessageType.GROUP);
	}

	public void setMessageType(MessageType type) {
		msgType = type; 
	}

	/**
	 * Determine if this message is a message signing off from the IM server.
	 * 
	 * @return True if the message is sent when signing off; false otherwise
	 */
	public boolean terminate() {
		return (msgType == MessageType.QUIT);
	}

	/**
	 * Representation of this message as a String. This begins with the message
	 * handle and then contains the length (as an integer) and the value of the next
	 * two arguments.
	 * 
	 * @return Representation of this message as a String.
	 */
	@Override
	public String toString() {
		String result = msgType.toString();
		if (msgSender != null) {
			result += " " + msgSender.length() + " " + msgSender;
		} else {
			result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
		}
		if (msgText != null) {
			result += " " + msgText.length() + " " + msgText;
		} else {
			result += " " + NULL_OUTPUT.length() + " " + NULL_OUTPUT;
		}
		return result;
	}

	@Override
	public Set<User> getReceiver() {
		return EntityHandler.getAllUsers();
	}
}
