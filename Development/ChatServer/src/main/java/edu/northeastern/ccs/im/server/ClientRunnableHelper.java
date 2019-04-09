package edu.northeastern.ccs.im.server;

import com.sun.org.apache.xpath.internal.operations.Bool;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.persistence.QueryFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class that handles all the control flow for chat messages. Every instance of
 * this class is invoked from and registered with a specific ClientRunnable
 * instance. The relation ensures messages are appropriately queued in the
 * active message queue. This class also takes care of calling the Database
 * Access Objects for various actions related to persistence.
 */
class ClientRunnableHelper {

	/**
	 * The query handler.
	 */
	private IQueryHandler queryHandler;

	/**
	 * Instantiates a new client runnable helper.
	 *
	 * @param queryHandler
	 *            the query handler
	 */
	ClientRunnableHelper(IQueryHandler queryHandler) {
		this.queryHandler = queryHandler;
	}

	/**
	 * Checks if the login credentials entered are valid
	 */
	private long isValidLoginCredentials(Message msg) {
		return queryHandler.validateLogin(msg.getName(), msg.getText());
	}

	/**
	 * Checks if the user with the name is already present.
	 */
	private boolean isUserPresent(String userName) {
		return queryHandler.checkUserNameExists(userName);
	}

	/**
	 * Checks if the group with the name is already present.
	 */
	private boolean isGroupPresent(String groupName) {
		return queryHandler.checkGroupNameExists(groupName);
	}

	/**
	 * This API is exposed to ClientRunnable for sending any kind of message.
	 * Specifically, this routes the control based on register / login / private /
	 * group messages.
	 */
	protected void handleMessages(Message message) {
		if (isRegisterOrLogin(message)) {
			handleRegisterLoginMessages(message);
		} else if (isChatMessage(message)) {
			handleChatMessages(message);
		} else if (message.isDeleteMessage()) {
			handleDeleteMessages(message);
		} else if (isGetInfoMessage(message)) {
			handleGetInfoMessages(message);
		} else if (isActionMessage(message)) {
			handleActionMessages(message);
		} else {
			handleErrorMessages(message);
		}
	}

	/**
	 * Checks if a message is an action message.
	 *
	 * @param message
	 *            the message
	 * @return true, if is action message
	 */
	private boolean isActionMessage(Message message) {
		return message.isActionMessage();
	}

	/**
	 * Handle delete messages.
	 *
	 * @param clientMessage
	 *            the client message
	 */
	private void handleDeleteMessages(Message clientMessage) {
		Message dbMessage = queryHandler.getMessage(clientMessage.getId());
		Message handshakeMessage;

		if (null == dbMessage) {
			// ERROR: message not found
			handshakeMessage = Message.makeErrorMessage(clientMessage.getName(),
					MessageConstants.ERROR_DELETE_INVALID_MSG_ID);
		} else if (!clientMessage.getName().equalsIgnoreCase(dbMessage.getName())) {
			// ERROR: sender invalid
			handshakeMessage = Message.makeErrorMessage(clientMessage.getName(),
					MessageConstants.ERROR_DELETE_SENDER_MISMATCH);
		} else if (!clientMessage.getMsgReceiver().equalsIgnoreCase(dbMessage.getMsgReceiver())) {
			// ERROR: receiver invalid
			handshakeMessage = Message.makeErrorMessage(clientMessage.getName(),
					MessageConstants.ERROR_DELETE_RECEIVER_MISMATCH);
		} else {
			queryHandler.deleteMessage(clientMessage.getId());
			// Deletion successful
			handshakeMessage = Message.makeAckMessage(MessageType.DELETE, clientMessage.getName(),
					MessageConstants.DELETE_SUCCESS_MSG);
		}
		Prattle.sendAckMessage(handshakeMessage);
	}

	/**
	 * Checks if the action is register or login and performs the respective action.
	 */
	private void handleRegisterLoginMessages(Message msg) {
		if (msg.isRegisterMessage()) {
			handleRegisterMessage(msg);
		} else {
			handleLoginMessage(msg);
		}
	}

	/**
	 * Checks the type of message and routes the control to either private or group
	 * message handler.
	 */
	private void handleChatMessages(Message msg) {
		if (msg.isDirectMessage()) {
			handleDirectMessages(msg);
		} else if (msg.isGroupMessage()) {
			handleGroupMessages(msg);
		} else {
			handleMultiReceiverMessages(msg);
		}
	}


	/**
	 * Delegate get info messages to the direct messages handler.
	 *
	 * @param msg
	 *            the msg
	 */
	private void handleGetInfoMessages(Message msg) {
		Prattle.sendDirectMessage(msg);
	}

	/**
	 * Handle action messages.
	 *
	 * @param message
	 *            the message
	 */
	private void handleActionMessages(Message message) {
		String[] contents = message.getText().split(" ");
		String actualAction = contents[0];

		if (actualAction.equals(MessageConstants.GROUP_CREATE_IDENTIFIER)) {
			handleCreateGroup(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.GROUP_DELETE_IDENTIFIER)) {
			handleDeleteGroup(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.GROUP_ADD_MODERATOR)) {
			handleCreateModerator(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.GROUP_REMOVE_MEMBER_IDENTIFIER)) {
			handleRemoveMember(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.GROUP_ADD_MEMBER_IDENTIFIER)) {
			handleAddMember(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.LEAVE_GROUP_IDENTIFIER)) {
			handleLeaveGroup(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.REQUEST_GROUP_ADD_IDENTIFIER)) {
			handleRequestGroupAdd(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.CHANGE_GROUP_VISIBILITY_IDENTIFIER)) {
			handleChangeGroupVisibility(message.getName(), contents);
		} else if (actualAction.equals(MessageConstants.CHANGE_USER_VISIBILITY_IDENTIFIER)) {
			handleUserVisibility(message.getName(), contents);
		}
		else if (actualAction.equals(MessageConstants.TRACK_MESSAGE_IDENTIFIER)) {
			handleTrackMessage(message.getName(), contents);
		} else {
			Message errorMessage = Message.makeErrorMessage(message.getName(),
					MessageConstants.INVALID_ACTION_TYPE_ERR);
			Prattle.sendErrorMessage(errorMessage);
		}

	}

	private void handleUserVisibility(String senderName, String[] contents) {
		boolean isPrivate = isPrivateVisibility(contents[1]);
		boolean isActualVisibilityPrivate = false;
//				queryHandler.getUserVisibility(senderName);

		if (isPrivate == isActualVisibilityPrivate) {
			Message errorMessage = Message.makeErrorMessage(senderName,
					"[INFO] User's visibility is already " + isPrivate);
			Prattle.sendErrorMessage(errorMessage);
		}
		else {
			toggleUserVisibility(senderName, isPrivate);
		}
		queryHandler.updateUserVisibility(senderName, isPrivate);

	}

	private void toggleUserVisibility(String senderName, boolean isPrivate) {
		queryHandler.updateUserVisibility(senderName, isPrivate);
		Message ackMessage = Message.makeAckMessage(MessageType.DIRECT, senderName,
				"Visbility successfully updated to - " + isPrivate);
		Prattle.sendAckMessage(ackMessage);
	}

	private void handleTrackMessage(String senderName, String[] contents) {
		long messageId = Long.parseLong(contents[1]);
		Message originalMessage = queryHandler.getMessage(messageId);

		if (originalMessage.getName().equals(senderName)) {
			Map<String, List<String>> trackInfo = queryHandler.trackMessage(messageId);
			String text = getBuiltTrackMessageInfo(trackInfo);
			Message responseMessage = Message.makeDirectMessage(senderName, senderName,
					text);
			Prattle.sendDirectMessage(responseMessage);
		}
		else {
			Prattle.sendErrorMessage(Message.makeErrorMessage(senderName,
					MessageConstants.INVALID_MESSAGE_TRACKER_ERR));
		}
	}

	private String getBuiltTrackMessageInfo(Map<String, List<String>> trackInfo) {
		StringBuilder text = new StringBuilder(" Message Tracking information: \n");
		text.append("Groups: ");
		text.append(trackInfo.get("groups")
				.stream()
				.reduce("", (group1, group2) -> group1 + "\n" + group2));
		text.append("\nUsers: ");
		text.append(trackInfo.get("users")
				.stream()
				.reduce("", (user1, user2) -> user1 + "\n" + user2));
		return text.toString().trim();
	}

	private void handleChangeGroupVisibility(String senderName, String[] contents) {
		boolean toBeGroupVisibility = isPrivateVisibility(contents[1]);
		String groupName = contents[2];

		boolean actualGroupVisibility = false;
//				queryHandler.getGroupVisibility(groupName);

		if (actualGroupVisibility == toBeGroupVisibility) {
			Message errorMessage = Message.makeErrorMessage(senderName,
					"[INFO] Group's visibility is already " + toBeGroupVisibility);
			Prattle.sendErrorMessage(errorMessage);
		}

		else {
			toggleGroupVisibility(groupName, senderName, toBeGroupVisibility);
		}
	}

	private boolean isPrivateVisibility(String toBeGroupVisibility) {
		return toBeGroupVisibility.equals(MessageConstants.PRIVATE_VISIBILITY_IDENTIFIER);
	}

	private void toggleGroupVisibility(String groupName, String senderName, boolean isPrivate) {
		if (queryHandler.getGroupModerators(groupName).contains(senderName)) {
			queryHandler.updateGroupVisibility(groupName, isPrivate);
			Message ackMessage = Message.makeAckMessage(MessageType.DIRECT, senderName,
					"Group Visbility successfully updated to - " + isPrivate);
			Prattle.sendAckMessage(ackMessage);
		}
		else {
			Message errorMessage = Message.makeErrorMessage(senderName,
					MessageConstants.INVALID_MODERATOR_ERR);
			Prattle.sendErrorMessage(errorMessage);
		}
	}


	/**
	 * Handle leave group message.
	 *
	 * @param senderName
	 *            the sender
	 * @param contents
	 *            the contents
	 */
	private void handleRequestGroupAdd(String senderName, String[] contents) {

		String toBeMember = contents[1];
		String groupName = contents[2];

		String ackMessage;

		if (queryHandler.isGroupMember(groupName, senderName)) {
			ackMessage = requestGroupAddByGroupMember(groupName, senderName, toBeMember);
		} else {
			ackMessage = MessageConstants.INVALID_GROUP_MEMBER_ERR;
		}

		Message handshakeMessage = Message.makeAckMessage(MessageType.ACTION, senderName, ackMessage);
		Prattle.sendAckMessage(handshakeMessage);
	}

	private String requestGroupAddByGroupMember(String groupName, String senderName, String toBeMember) {
		String ackMessage;
		if (queryHandler.checkUserNameExists(toBeMember)) {
			ackMessage = MessageConstants.REQUEST_GROUP_ADD_SUCCESS_MSG;
			publishRequestToModerators(groupName, senderName, toBeMember);
		} else {
			ackMessage = MessageConstants.INVALID_USER_ERR;
		}
		return ackMessage;
	}

	/**
	 * Publish request to moderators.
	 *
	 * @param groupName
	 *            the group name
	 * @param senderName
	 *            the sender name
	 * @param toBeMember
	 *            the to be member
	 */
	private void publishRequestToModerators(String groupName, String senderName, String toBeMember) {
		List<String> moderators = queryHandler.getGroupModerators(groupName);
		Set<String> moderatorSet = new HashSet<>(moderators);
		String content = String.format("%s has requested to add %s to the group %s", senderName, toBeMember, groupName);
		
		Message message = Message.makeBroadcastMessage(senderName, content);

		// persist messages to publish it to offline moderators
		moderatorSet.forEach(
				moderator -> queryHandler.storeMessage(senderName, moderator, MessageType.DIRECT, 
						MessageConstants.REQUEST_PREFIX + content));

		Prattle.sendMessageToMultipleUsers(message, moderatorSet);
	}

	/**
	 * Handle leave group.
	 *
	 * @param sender
	 *            the sender
	 * @param contents
	 *            the contents of the leave group message
	 */
	private void handleLeaveGroup(String sender, String[] contents) {
		String groupName = contents[1];
		String ackMessage;

		if (queryHandler.isGroupMember(groupName, sender)) {
			queryHandler.removeMember(groupName, sender);
			ackMessage = MessageConstants.LEAVE_GROUP_SUCCESS_MSG;
		} else {
			ackMessage = MessageConstants.INVALID_GROUP_MEMBER_ERR;
		}
		Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
		Prattle.sendAckMessage(message);
	}

	/**
	 * Handle add member message.
	 *
	 * @param sender
	 *            the sender
	 * @param contents
	 *            the contents of the add member message
	 */
	private void handleAddMember(String sender, String[] contents) {
		String member = contents[1];
		String groupName = contents[2];
		String ackMessage;

		if (queryHandler.isModerator(sender, groupName)) {
			ackMessage = addMemberByModerator(groupName, member);
		} else {
			ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
		}

		Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
		Prattle.sendAckMessage(message);
	}

	private String addMemberByModerator(String groupName, String member) {
		String ackMessage;
		if (queryHandler.checkUserNameExists(member)) {
			queryHandler.addGroupMember(member, groupName, 1);
			ackMessage = MessageConstants.ADD_MMBR_SUCCESS_MSG;
		} else {
			ackMessage = MessageConstants.INVALID_USER_ERR;
		}
		return ackMessage;
	}

	/**
	 * Handle remove member message.
	 *
	 * @param sender
	 *            the sender
	 * @param contents
	 *            the contents
	 */
	private void handleRemoveMember(String sender, String[] contents) {
		String member = contents[1];
		String groupName = contents[2];
		String ackMessage;

		if (queryHandler.isModerator(sender, groupName)) {
			ackMessage = removeMemberByModerator(groupName, sender, member);
		} else {
			ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
		}

		Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
		Prattle.sendAckMessage(message);
	}

	private String removeMemberByModerator(String groupName, String sender, String member) {
		String ackMessage;
		if (queryHandler.isGroupMember(groupName, sender)) {
			queryHandler.removeMember(groupName, member);
			ackMessage = MessageConstants.RMV_MMBR_SUCCESS_MSG;
		} else {
			ackMessage = MessageConstants.RMV_MMBR_INVALID_ERR;
		}
		return ackMessage;
	}

	/**
	 * Handle create moderator message.
	 *
	 * @param sender
	 *            the sender
	 * @param contents
	 *            the contents
	 */
	private void handleCreateModerator(String sender, String[] contents) {
		String toBeModerator = contents[1];
		String groupName = contents[2];
		String ackMessage;

		if (queryHandler.isModerator(sender, groupName)) {
			ackMessage = createModeratorByModerator(groupName, sender, toBeModerator);
		} else {
			ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
		}

		Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
		Prattle.sendAckMessage(message);
	}

	private String createModeratorByModerator(String groupName, String sender, String toBeModerator) {
		String ackMessage;
		if (queryHandler.isGroupMember(groupName, sender)) {
			queryHandler.makeModerator(groupName, toBeModerator);
			ackMessage = MessageConstants.ADD_MDRTR_SUCCESS_MSG;
		} else {
			ackMessage = MessageConstants.ADD_MDRTR_INVALID_ERR;
		}
		return ackMessage;
	}

	/**
	 * Handle delete group message.
	 *
	 * @param sender
	 *            the sender
	 * @param contents
	 *            the contents
	 */
	private void handleDeleteGroup(String sender, String[] contents) {
		String groupName = contents[1];
		String ackMessage;
		if (queryHandler.isModerator(sender, groupName)) {
			queryHandler.deleteGroup(sender, groupName);
			ackMessage = MessageConstants.GROUP_DELETE_SUCCESS_MSG;
		} else {
			ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
		}
		Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
		Prattle.sendAckMessage(message);
	}

	/**
	 * Handle create group message.
	 *
	 * @param sender
	 *            the sender
	 * @param contents
	 *            the contents
	 */
	private void handleCreateGroup(String sender, String[] contents) {
		String groupName = contents[1];
		queryHandler.createGroup(sender, groupName);
	}

	/**
	 * Error messages are routed back to the sender.
	 */
	private void handleErrorMessages(Message msg) {
		Prattle.sendErrorMessage(msg);
	}

	/**
	 * Checks if the registration credentials are valid. Based on that send an
	 * acknowledgement message.
	 */
	private void handleRegisterMessage(Message message) {
		Message handShakeMessage;
		String acknowledgementText;

		if (!isUserPresent(message.getName())) {
			acknowledgementText = MessageConstants.REGISTER_SUCCESS_MSG;
			handShakeMessage = Message.makeAckMessage(MessageType.REGISTER, message.getName(), acknowledgementText);
			// Persist user details
			queryHandler.createUser(message.getName(), message.getText(), message.getName());
		} else {
			acknowledgementText = MessageConstants.REGISTER_FAILURE_ERR;
			handShakeMessage = Message.makeErrorMessage(message.getName(), acknowledgementText);
		}

		Prattle.sendAckMessage(handShakeMessage);
	}

	/**
	 * On a login request, this verifies user credentials and then acknowledges the
	 * user with a success / failure message
	 */
	private void handleLoginMessage(Message message) {
		Message handShakeMessage;
		String acknowledgementText;

		long userId = isValidLoginCredentials(message);
		if (userId != -1) {

			acknowledgementText = MessageConstants.LOGIN_SUCCESS_MSG;
			handShakeMessage = Message.makeLoginAckMessage(MessageType.LOGIN, message.getName(), message.getName(),
					acknowledgementText);
		} else {
			acknowledgementText = MessageConstants.LOGIN_FAILURE_ERR;
			handShakeMessage = Message.makeErrorMessage(message.getName(), acknowledgementText);
		}

		Prattle.sendAckMessage(handShakeMessage);

		if (userId != -1) {
			loadPendingMessages(userId);
			QueryFactory.getQueryHandler().updateUserLastLogin(userId);
		}
	}

	/**
	 * Load pending messages received since user logged off.
	 *
	 * @param userId
	 *            the user id
	 */
	private void loadPendingMessages(long userId) {
		List<Message> messageList = QueryFactory.getQueryHandler().getMessagesSinceLastLogin(userId);
		for (Message message : messageList) {
			Prattle.sendDirectMessage(message);
		}
	}

	/**
	 * Checks if the receiver is valid. If valid, then send the direct message to
	 * the reeciver. Otherwise, sends an error message to the sender.
	 *
	 * @param message
	 *            - custom constructed message by the parser
	 */
	private void handleDirectMessages(Message message) {
		if (isUserPresent(message.getMsgReceiver())) {
			
			long messageId = queryHandler.storeMessage(message.getName(), message.getMsgReceiver(),
					message.getMessageType(), message.getText());
			message.setText(getPrependedMessageText(message.getText(), messageId));
			
			Message ackMessage = Message.makeAckMessage(MessageType.BROADCAST, message.getName(),
					MessageConstants.MESSAGE_SENT_INFO + messageId);
			
			Prattle.sendAckMessage(ackMessage);
			Prattle.sendDirectMessage(message);
		} else {
			Message errorMessage = Message.makeErrorMessage(message.getName(),
					MessageConstants.INVALID_DIRECT_RECEIVER_MSG);
			Prattle.sendErrorMessage(errorMessage);
		}
	}

	/**
	 * Handle group message.
	 *
	 * @param message
	 *            the message
	 */
	private void handleGroupMessages(Message message) {
		String groupName = message.getMsgReceiver();
		if (isGroupPresent(groupName))
		{
			long messageId = queryHandler.storeMessage(message.getName(), message.getMsgReceiver(),
					message.getMessageType(), message.getText());

			message.setText(getPrependedMessageText(message.getText(), messageId));
			Set<String> groupMemberNames = queryHandler.getAllGroupMembers(groupName);
			Prattle.sendMessageToMultipleUsers(message, groupMemberNames);
		} else {
			Message errorMessage = Message.makeErrorMessage(message.getName(),
					MessageConstants.INVALID_GROUP_RECEIVER_MSG);
			Prattle.sendErrorMessage(errorMessage);
		}
	}

	/***
	 *
	 * @param msg
	 * NOTES: GRP_SBST SENDER_NAME 'RCVRS' RCVR1 RCVR2 RCVR3 RCVR4 'RCVRS' GRP_SBST GROUP_NAME message text
	 */
	private void handleMultiReceiverMessages(Message msg) {
		List<String> actualMembers =  queryHandler.getGroupMembers(msg.getMsgReceiver());
		Set<String> finalizedReceivers = new HashSet<>();

		for (String potentialGroupMember : msg.getReceivers()) {
			if (actualMembers.contains(potentialGroupMember)) {
				finalizedReceivers.add(potentialGroupMember);
				handleMessageToValidReceiver(msg, potentialGroupMember);
			}

			else {
				handleMessageToInvalidReceiver(msg, potentialGroupMember);
			}
		}

		Prattle.sendMessageToMultipleUsers(msg, finalizedReceivers);
	}

	private void handleMessageToInvalidReceiver(Message msg, String potentialGroupMember) {
		Message errorMessage = Message.makeErrorMessage(msg.getName(),
				"[ERROR] : " + potentialGroupMember + " does not exist or not " +
						"a part of the group - " + msg.getMsgReceiver() );
		Prattle.sendErrorMessage(errorMessage);
	}

	private void handleMessageToValidReceiver(Message msg, String potentialGroupMember) {
		Message ackMessage = Message.makeAckMessage(MessageType.GROUP_SUBSET,
				msg.getName(), "[INFO] Message successfully sent to "
						+ potentialGroupMember);
		Prattle.sendAckMessage(ackMessage);
	}


	/**
	 * Prepend the message text with id to parse and display in the client side.
	 * This will be useful for identifying each messages uniquely from the console
	 * and client side. Example: - actual message text -> "Hi there" - after
	 * prepending -> "<142> Hi there" where 142 is the message id
	 *
	 * @param msgText
	 *            - the message text.
	 * @param messageId
	 *            - id for the message returned on persistence in the database.
	 */
	String getPrependedMessageText(String msgText, long messageId) {
		StringBuilder text = new StringBuilder(MessageConstants.MSG_ID_PREFIX);
		text.append(messageId);
		text.append(MessageConstants.MSG_ID_SUFFIX);
		text.append(msgText);

		return text.toString();
	}

	/**
	 * Checks if the message is either a login or a registration request
	 */
	private boolean isRegisterOrLogin(Message msg) {
		return (msg.isRegisterMessage() || msg.isLoginMessage());
	}

	/**
	 * Returns true if the message is a direct of group message. Otherwise false.
	 */
	private boolean isChatMessage(Message msg) {
		return (msg.isDirectMessage() || msg.isGroupMessage() || msg.isGroupSubsetMessage());
	}

	/**
	 * Returns true if the message is a get all Users message. Otherwise false.
	 */
	static boolean isGetInfoMessage(Message msg) {
		return msg.isGetInfoMessage();
	}

	/**
	 * Parse the input message text and return a custom constructed message
	 * according to the type.
	 */
	Message getCustomConstructedMessage(Message msg) {

		String content = msg.getText();
		Message message = msg;

		if (content.startsWith(MessageConstants.CUSTOM_COMMAND_PREFIX)) {
			message = MessageFactory.createMessage(message, queryHandler);

		}
		return message;
	}
}
