package edu.northeastern.ccs.serverim;

import edu.northeastern.ccs.im.constants.MessageConstants;

import java.util.List;

/**
 * Each instance of this class represents a single transmission by our IM
 * clients.
 * <p>
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class Message {

	private long id;
	/**
	 * The string sent when a field is null.
	 */
	private static final String NULL_OUTPUT = "--";

	/**
	 * The handle of the message.
	 */
	private MessageType msgType;

	/**
	 * The first argument used in the message. This will be the sender's identifier.
	 */
	private String msgSender;

	private String msgReceiver;

	private int isDeleted;

	/**
	 * The second argument used in the message.
	 */
	private String msgText;

	private List<String> receivers;

	private final long timeStamp;

	private int timeOutMinutes;

	/**
	 * Instantiates a new message.
	 *
	 * @param id
	 *            the id
	 * @param handle
	 *            the handle
	 * @param sender
	 *            the sender
	 * @param receiver
	 *            the receiver
	 * @param text
	 *            the text
	 * @param isDeleted
	 *            the bit to indicate if a message is deleted
	 * @param timeStamp
	 *            timestamp of message
	 * @param timeoutMinutes
	 *            timeoutMinutes for this message
	 */
	public Message(long id, MessageType handle, String sender, String receiver, String text, int isDeleted,
			long timeStamp, int timeoutMinutes) {
		this.msgType = handle;
		this.msgSender = sender;
		this.msgReceiver = receiver;
		this.msgText = text;
		this.id = id;
		this.isDeleted = isDeleted;
		this.timeStamp = timeStamp;
		this.timeOutMinutes = timeoutMinutes;
	}

	/**
	 * Instantiates a new message.
	 *
	 * @param id
	 *            the id
	 * @param handle
	 *            the handle
	 * @param sender
	 *            the sender
	 * @param receiver
	 *            the receiver
	 * @param text
	 *            the text
	 * @param isDeleted
	 *            the bit to indicate if a message is deleted
	 */
	public Message(long id, MessageType handle, String sender, String receiver, String text, int isDeleted) {
		this(id, handle, sender, receiver, text, isDeleted, System.currentTimeMillis(), 0);
	}

	/**
	 * Create a new message that contains actual IM text. The type of distribution
	 * is defined by the handle and we must also set the name of the message sender,
	 * message recipient, and the text to send.
	 *
	 * @param handle
	 *            Handle for the type of message being created. // * @param srcName
	 *            Name of the individual sending this message
	 * @param text
	 *            Text of the instant message
	 */
	private Message(MessageType handle, String srcName, String text) {
		this(handle, srcName, "", text);
	}

	/**
	 * Instantiates a new message without the user id.
	 *
	 * @param handle
	 *            the handle
	 * @param sender
	 *            the sender
	 * @param receiver
	 *            the receiver
	 * @param text
	 *            the text
	 */
	public Message(MessageType handle, String sender, String receiver, String text) {
		this(-1L, handle, sender, receiver, text, 0);
	}

	/**
	 * Create a new message that contains a command sent the server that requires a
	 * single argument. This message contains the given handle and the single
	 * argument.
	 *
	 * @param handle
	 *            Handle for the type of message being created.
	 * @param srcName
	 *            Argument for the message; at present this is the name used to
	 *            log-in to the IM server.
	 */
	private Message(MessageType handle, String srcName) {
		this(handle, srcName, "");
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Gets the isDeleted bit.
	 *
	 * @return the isDeleted bit
	 */
	public long getIsDeleted() {
		return this.isDeleted;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeOutMinutes (int timeOutMinutes) {
		this.timeOutMinutes = timeOutMinutes;
	}

	public int getTimeOutMinutes() {
		return timeOutMinutes;
	}

	/**
	 * Create a new message to continue the logout process.
	 *
	 * @param myName
	 *            The name of the client that sent the quit message.
	 * @return Instance of Message that specifies the process is logging out.
	 */
	public static Message makeQuitMessage(String myName) {
		return new Message(MessageType.QUIT, myName, "");
	}

	/**
	 * Create a new message broadcasting an announcement to the world.
	 *
	 * @param myName
	 *            Name of the sender of this very important missive.
	 * @param text
	 *            Text of the message that will be sent to all users
	 * @return Instance of Message that transmits text to all logged in users.
	 */
	public static Message makeBroadcastMessage(String myName, String text) {
		return new Message(MessageType.BROADCAST, myName, text);
	}

	public static Message makeGroupSubsetMessage(String msgSender, String groupName, String msgText) {
		return new Message(MessageType.GROUP_SUBSET, msgSender, groupName, msgText);
	}

	/**
	 * Gets the message receiver.
	 *
	 * @return the message receiver
	 */
	public String getMsgReceiver() {
		return msgReceiver;
	}

	/**
	 * Gets the message type.
	 *
	 * @return the message type
	 */
	public MessageType getMessageType() {
		return msgType;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

	/**
	 * Create a new message stating the name with which the user would like to
	 * login.
	 *
	 * @param text
	 *            Name the user wishes to use as their screen name.
	 * @return Instance of Message that can be sent to the server to try and login.
	 */
	static Message makeHelloMessage(String text) {
		return new Message(MessageType.HELLO, null, text);
	}

	/**
	 * Make register message.
	 *
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @return the message
	 */
	public static Message makeRegisterMessage(String userName, String password) {
		return new Message(MessageType.REGISTER, userName, password);
	}

	/**
	 * Make login message.
	 *
	 * @param userName
	 *            the user name
	 * @param password
	 *            the password
	 * @return the message
	 */
	public static Message makeLoginMessage(String userName, String password) {
		return new Message(MessageType.LOGIN, userName, password);
	}

	/**
	 * Make login acknowledgement message.
	 *
	 * @param handle
	 *            the handle
	 * @param sender
	 *            the sender
	 * @param receiver
	 *            the receiver
	 * @param msgText
	 *            the message text
	 * @return the message
	 */
	public static Message makeLoginAckMessage(MessageType handle, String sender, String receiver, String msgText) {
		return new Message(handle, sender, receiver, msgText);
	}

	/**
	 * Make acknowledgement message.
	 *
	 * @param handle
	 *            the handle
	 * @param msgSender
	 *            the message sender
	 * @param msgText
	 *            the message text
	 * @return the message
	 */
	public static Message makeAckMessage(MessageType handle, String msgSender, String msgText) {
		return new Message(handle, msgSender, msgText);
	}

	/**
	 * Make direct message.
	 *
	 * @param msgSender
	 *            the message sender
	 * @param msgReceiver
	 *            the message receiver
	 * @param msgText
	 *            the message text
	 * @return the message
	 */
	public static Message makeDirectMessage(String msgSender, String msgReceiver, String msgText) {
		return new Message(MessageType.DIRECT, msgSender, msgReceiver, msgText);
	}

	/**
	 * Make group message.
	 *
	 * @param msgSender
	 *            the message sender
	 * @param groupName
	 *            the group name
	 * @param msgText
	 *            the message text
	 * @return the message
	 */
	public static Message makeGroupMessage(String msgSender, String groupName, String msgText) {
		return new Message(MessageType.GROUP, msgSender, groupName, msgText);
	}

	/**
	 * Make get info message.
	 *
	 * @param msgSender
	 *            the message sender
	 * @param msgReceiver
	 *            the message receiver
	 * @param msgText
	 *            the message text
	 * @return the message
	 */
	public static Message makeGetInfoMessage(String msgSender, String msgReceiver, String msgText) {
		return new Message(MessageType.GET_INFO, msgSender, msgReceiver, msgText);
	}

	/**
	 * Make error message.
	 *
	 * @param msgSender
	 *            the message sender
	 * @param msgText
	 *            the message text
	 * @return the message
	 */
	public static Message makeErrorMessage(String msgSender, String msgText) {
		return new Message(MessageType.BROADCAST, msgSender, getErrorMessageText(msgText));
	}

	/**
	 * Make delete message.
	 *
	 * @param messageId
	 *            the message id
	 * @param msgSender
	 *            the message sender
	 * @param msgReceiver
	 *            the message receiver
	 * @return the message
	 */
	public static Message makeDeleteMessage(long messageId, String msgSender, String msgReceiver) {
		return new Message(messageId, MessageType.DELETE, msgSender, msgReceiver, "", 0);
	}

	/**
	 * Make action message.
	 *
	 * @param sender
	 *            the sender
	 * @param actualAction
	 *            the actual action
	 * @return the message
	 */
	public static Message makeActionMessage(String sender, String actualAction) {
		return new Message(MessageType.ACTION, sender, actualAction);
	}

	/**
	 * Gets the error message text.
	 *
	 * @param plainMessageText
	 *            the plain message text
	 * @return the error message text
	 */
	private static String getErrorMessageText(String plainMessageText) {
		return MessageConstants.CUSTOM_COMMAND_PREFIX + MessageConstants.ERROR_MSG_IDENTIFIER
				+ MessageConstants.CUSTOM_COMMAND_SUFFIX + " " + plainMessageText;
	}

	/**
	 * Given a handle, name and text, return the appropriate message instance or an
	 * instance from a subclass of message.
	 *
	 * @param handle
	 *            Handle of the message to be generated.
	 * @param srcName
	 *            Name of the originator of the message (may be null)
	 * @param text
	 *            Text sent in this message (may be null)
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

	/**
	 * Create a new message for the early stages when the user logs in without all
	 * the special stuff.
	 *
	 * @param myName
	 *            Name of the user who has just logged in.
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
	 * Sets the text.
	 *
	 * @param text
	 *            the new text
	 */
	public void setText(String text) {
		this.msgText = text;
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

	/**
	 * Checks if is register message.
	 *
	 * @return true, if register message
	 */
	public boolean isRegisterMessage() {
		return (msgType == MessageType.REGISTER);
	}

	/**
	 * Checks if message is a login message.
	 *
	 * @return true, if login message
	 */
	public boolean isLoginMessage() {
		return (msgType == MessageType.LOGIN);
	}

	/**
	 * Checks if message is a direct message.
	 *
	 * @return true, if direct message
	 */
	public boolean isDirectMessage() {
		return (msgType == MessageType.DIRECT);
	}

	/**
	 * Checks if message is a delete message.
	 *
	 * @return true, if delete message
	 */
	public boolean isDeleteMessage() {
		return (msgType == MessageType.DELETE);
	}

	/**
	 * Checks if message is a group message.
	 *
	 * @return true, if group message
	 */
	public boolean isGroupMessage() {
		return (msgType == MessageType.GROUP);
	}

	/**
	 * Checks if message is a getsinfo message.
	 *
	 * @return true, if getsinfo message
	 */
	public boolean isGetInfoMessage() {
		return (msgType == MessageType.GET_INFO);
	}

	/**
	 * Checks if message is an action message.
	 *
	 * @return true, if action message
	 */
	public boolean isActionMessage() {
		return (msgType == MessageType.ACTION);
	}

	/**
	 * Sets the message type.
	 *
	 * @param type
	 *            the new message type
	 */
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

	public boolean isGroupSubsetMessage() {
		return msgType == MessageType.GROUP_SUBSET;
	}

	public void setReceiver(String potentialGroupMember) {
		this.msgReceiver = potentialGroupMember;
	}

	public Message getClone() {
		return new Message(this.id, this.msgType, this.msgSender, this.msgReceiver, this.msgText, this.isDeleted,
				this.timeStamp, this.timeOutMinutes);
	}
}