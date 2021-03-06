package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.persistence.QueryFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
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
     * @param queryHandler the query handler
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
     * @param message the message
     * @return true, if is action message
     */
    private boolean isActionMessage(Message message) {
        return message.isActionMessage();
    }

    /**
     * Handle delete messages.
     *
     * @param clientMessage the client message
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
     * @param msg the msg
     */
    private void handleGetInfoMessages(Message msg) {
        Prattle.sendAckMessage(msg);
    }

    /**
     * Handle action messages.
     *
     * @param message the message
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
        } else if (actualAction.equals(MessageConstants.TRACK_MESSAGE_IDENTIFIER)) {
            handleTrackMessage(message.getName(), contents);
        } else if (actualAction.equals(MessageConstants.MESSAGE_HISTORY_IDENTIFIER)) {
            handleGetChatHistory(message.getName(), contents);
        } else {
            Message errorMessage = Message.makeErrorMessage(message.getName(),
                    MessageConstants.INVALID_ACTION_TYPE_ERR);
            Prattle.sendErrorMessage(errorMessage);
        }

    }

    /**
     * @param senderName Sender name of the request
     * @param contents   Raw content of message object sent from client
     */
    private void handleUserVisibility(String senderName, String[] contents) {
        boolean isPrivate = isPrivateVisibility(contents[1]);
        boolean isActualVisibilityPrivate = queryHandler.isUserInVisible(senderName);

        if (isPrivate == isActualVisibilityPrivate) {
            Message errorMessage = Message.makeErrorMessage(senderName,
                    "[INFO] User's visibility is already " + isPrivate);
            Prattle.sendErrorMessage(errorMessage);
        } else {
            toggleUserVisibility(senderName, isPrivate);
        }
        queryHandler.updateUserVisibility(senderName, isPrivate);

    }

    /**
     * Modifies the user's visibility to the given one
     *
     * @param userName  User name whose visibility is to be toggled
     * @param isPrivate true if the visibility is private. Otherwise false
     */
    private void toggleUserVisibility(String userName, boolean isPrivate) {
        queryHandler.updateUserVisibility(userName, isPrivate);
        Message ackMessage = Message.makeAckMessage(MessageType.DIRECT, userName,
                "Visibility successfully updated to - " + isPrivate);
        Prattle.sendAckMessage(ackMessage);
    }

    /**
     * Handler for tracking message command. Sends tracked information if the
     * request is valid. Otherwise sends an error message.
     *
     * @param userName Name of the user whose message has to be tracked
     * @param contents Raw contents from the client
     */
    private void handleTrackMessage(String userName, String[] contents) {
        long messageId = Long.parseLong(contents[1]);
        Message originalMessage = queryHandler.getMessage(messageId);

        if (originalMessage.getName().equals(userName)) {
            Map<String, List<String>> trackInfo = queryHandler.trackMessage(messageId);
            String text = getBuiltTrackMessageInfo(trackInfo);
            Message responseMessage = Message.makeDirectMessage(userName, userName,
                    text, 0);
            Prattle.sendAckMessage(responseMessage);
        } else {
            Prattle.sendErrorMessage(Message.makeErrorMessage(userName,
                    MessageConstants.INVALID_MESSAGE_TRACKER_ERR));
        }
    }

    /***
     * Builds a formatted String of message track information
     * @param trackInfo Contains both group and user names who has received the message
     *                  indirectly (through forwarding)
     * @return a detailed information of all groups and users who got the message forwarded.
     */
    private String getBuiltTrackMessageInfo(Map<String, List<String>> trackInfo) {
        StringBuilder text = new StringBuilder(MessageConstants.MESSAGE_TRACK_INFO_HEADER);
        text.append(MessageConstants.GROUPS_HEADER);
        text.append(trackInfo.get(MessageConstants.FORWARDED_GROUPS)
                .stream()
                .reduce("", (group1, group2) -> group1 + "\n" + group2));
        text.append(MessageConstants.USER_HEADER);
        text.append(trackInfo.get(MessageConstants.FORWARDED_USERS)
                .stream()
                .reduce("", (user1, user2) -> user1 + "\n" + user2));
        return text.toString().trim();
    }

    /***
     * Handles request to modify group's visibility
     * @param userName Name of user who request to modify group's visibility
     * @param contents Raw message content from client
     */
    private void handleChangeGroupVisibility(String userName, String[] contents) {
        boolean toBeGroupVisibility = isPrivateVisibility(contents[1]);
        String groupName = contents[2];

        boolean isActuallyPrivate = queryHandler.isGroupInVisible(groupName);

        if (isActuallyPrivate == toBeGroupVisibility) {
            Message errorMessage = Message.makeErrorMessage(userName,
                    "[INFO] Group's visibility is already " + toBeGroupVisibility);
            Prattle.sendErrorMessage(errorMessage);
        } else {
            toggleGroupVisibility(groupName, userName, toBeGroupVisibility);
        }
    }

    /**
     * @param visibility Compared to check whether it is a private visibility or not
     * @return True if visibility is Private. Otherwise False
     */
    private boolean isPrivateVisibility(String visibility) {
        return visibility.equals(MessageConstants.PRIVATE_VISIBILITY_IDENTIFIER);
    }

    /**
     * @param groupName Group's name whose visibility is intended to be modified
     * @param userName  User's name who raise the request to toggle group visibility
     * @param isPrivate visibility of group. True if Private. False if Public
     */
    private void toggleGroupVisibility(String groupName, String userName, boolean isPrivate) {
        if (queryHandler.isModerator(userName, groupName)) {
            queryHandler.updateGroupVisibility(groupName, isPrivate);
            Message ackMessage = Message.makeAckMessage(MessageType.DIRECT, userName,
                    "Group Visibility successfully updated to - " + isPrivate);
            Prattle.sendAckMessage(ackMessage);
        } else {
            Message errorMessage = Message.makeErrorMessage(userName,
                    MessageConstants.INVALID_MODERATOR_ERR);
            Prattle.sendErrorMessage(errorMessage);
        }
    }


    /**
     * Handle leave group message.
     *
     * @param senderName the sender
     * @param contents   the contents
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

    /**
     * @param groupName  Group's name where new member has to be added
     * @param userName   User's name who raise the request add group member request
     * @param toBeMember Name of user that has to be added in the group
     * @return an acknowledgement message of the request's status
     */
    private String requestGroupAddByGroupMember(String groupName, String userName, String toBeMember) {
        String ackMessage;
        if (queryHandler.checkUserNameExists(toBeMember)) {
            ackMessage = MessageConstants.REQUEST_GROUP_ADD_SUCCESS_MSG;
            publishRequestToModerators(groupName, userName, toBeMember);
        } else {
            ackMessage = MessageConstants.INVALID_USER_ERR;
        }
        return ackMessage;
    }

    /**
     * Handle get chat history.
     *
     * @param senderName the sender name
     * @param content    the content
     */
    private void handleGetChatHistory(String senderName, String[] content) {
        int start = Integer.parseInt(content[1]);
        int limit = Integer.parseInt(content[2]);
        List<Message> messageList;
        String receiver = senderName;
        if (content.length == 4) {
            receiver = content[3];
            messageList = queryHandler.getMessagesFromUserChat(senderName, receiver, start, limit);
        } else {
            messageList = queryHandler.getMessagesFromGroupChat(receiver, start, limit);
        }
        for (Message message : messageList) {
            Message msg = Message.makeDirectMessage(message.getName(), senderName,
                    getPrependedMessageText(message.getText(), message.getId(), message.getTimeStamp()), 0);
            Prattle.sendDirectMessage(msg);
        }
    }

    /**
     * Publish request to moderators.
     *
     * @param groupName  the group name
     * @param senderName the sender name
     * @param toBeMember the to be member
     */
    private void publishRequestToModerators(String groupName, String senderName, String toBeMember) {
        List<String> moderators = queryHandler.getGroupModerators(groupName);
        Set<String> moderatorSet = new HashSet<>(moderators);
        String content = String.format("%s has requested to add %s to the group %s", senderName, toBeMember, groupName);

        Message message = Message.makeBroadcastMessage(senderName, content);

        // persist messages to publish it to offline moderators
        moderatorSet.forEach(
                moderator -> queryHandler.storeMessage(senderName, moderator, MessageType.DIRECT,
                        MessageConstants.REQUEST_PREFIX + content, System.currentTimeMillis(), 0));

        Prattle.sendMessageToMultipleUsers(message, moderatorSet, groupName);
    }

    /**
     * Handle leave group.
     *
     * @param sender   the sender
     * @param contents the contents of the leave group message
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
     * @param sender   the sender
     * @param contents the contents of the add member message
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
     * @param sender   the sender
     * @param contents the contents
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

    /***
     *
     * @param groupName Name of group for which request is raised
     * @param sender Name of user who raises the request
     * @param member Name of user who is intended to be removed from group
     * @return acknowledgement message stating the status of request
     */
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
     * @param sender   the sender
     * @param contents the contents
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

    /***
     *
     * @param groupName Name of group for which request is raised
     * @param sender Name of user who raises the request
     * @param toBeModerator User who is intended to be moderator
     * @return an acknowledgement message stating the status of request
     */
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
     * @param sender   the sender
     * @param contents the contents
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
     * @param sender   the sender
     * @param contents the contents
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
     * @param userId the user id
     */
    private void loadPendingMessages(long userId) {
        List<Message> messageList = QueryFactory.getQueryHandler().getMessagesSinceLastLogin(userId);
        for (Message message : messageList) {
            message.setText(getPrependedMessageText(message.getText(), message.getId(), message.getTimeStamp()));
            Prattle.sendDirectMessage(message);
        }
    }

    /**
     * Checks if the receiver is valid. If valid, then send the direct message to
     * the receiver. Otherwise, sends an error message to the sender.
     *
     * @param message - custom constructed message by the parser
     */
    private void handleDirectMessages(Message message) {
        if (isUserPresent(message.getMsgReceiver())) {
            long messageId = persistMessage(message);

            formatMessageTextToClientShowable(message, messageId);

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
     * @param message   Object whose text field has to be formatted to print to client window
     * @param messageId id of the message
     */
    private void formatMessageTextToClientShowable(Message message, long messageId) {
        long parentMessageId = message.getId();
        if ((parentMessageId != MessageConstants.DEFAULT_MESSAGE_ID) && (messageId != parentMessageId)) {
            message.setText(getPrependedMessageText(message.getText()
                    + MessageConstants.FORWARDED_MESSAGE_IDENTIFIER, messageId, message.getTimeStamp()));
        } else {
            message.setText(getPrependedMessageText(message.getText(), messageId, message.getTimeStamp()));
        }
    }

    /**
     * @param message Message object to be persisted in the database
     * @return id of the persisted message
     */
    private long persistMessage(Message message) {
        return queryHandler.storeMessage(message.getName(), message.getMsgReceiver(),
                message.getMessageType(), message.getText(), message.getId(),
                message.getTimeStamp(), message.getTimeOutMinutes());

    }

    /**
     * Handle group message.
     *
     * @param message the message
     */
    private void handleGroupMessages(Message message) {
        String groupName = message.getMsgReceiver();
        if (isGroupPresent(groupName)) {
            long messageId = persistMessage(message);
            formatMessageTextToClientShowable(message, messageId);
            Set<String> groupMemberNames = queryHandler.getAllGroupMembers(groupName);
            Prattle.sendMessageToMultipleUsers(message, groupMemberNames, groupName);
        } else {
            Message errorMessage = Message.makeErrorMessage(message.getName(),
                    MessageConstants.INVALID_GROUP_RECEIVER_MSG);
            Prattle.sendErrorMessage(errorMessage);
        }
    }

    /**
     * @param msg Message to be sent to multiple receivers of a group
     */
    private void handleMultiReceiverMessages(Message msg) {
        List<String> actualMembers = queryHandler.getGroupMembers(msg.getMsgReceiver());

        for (String potentialGroupMember : msg.getReceivers()) {
            if (actualMembers.contains(potentialGroupMember)) {
                handleMessageToValidReceiver(msg.getClone(), potentialGroupMember);
            } else {
                handleMessageToInvalidReceiver(msg, potentialGroupMember);
            }
        }
    }


    /**
     * @param msg                  message information
     * @param potentialGroupMember User intended to receive a group subset message
     */
    private void handleMessageToInvalidReceiver(Message msg, String potentialGroupMember) {
        Message errorMessage = Message.makeErrorMessage(msg.getName(),
                "[ERROR] : " + potentialGroupMember + " does not exist or not " +
                        "a part of the group - " + msg.getMsgReceiver());
        Prattle.sendErrorMessage(errorMessage);
    }

    /**
     * @param msg                  message information
     * @param potentialGroupMember User intended to receive a group subset message
     */
    private void handleMessageToValidReceiver(Message msg, String potentialGroupMember) {
        Message ackMessage = Message.makeAckMessage(MessageType.GROUP_SUBSET,
                msg.getName(), "[INFO] Message successfully sent to "
                        + potentialGroupMember);
        long messageId = queryHandler.storeMessage(msg.getName(), potentialGroupMember, msg.getMessageType(),
                msg.getText(), msg.getTimeStamp(), msg.getTimeOutMinutes());


        msg.setReceiver(potentialGroupMember);
        msg.setText(getPrependedMessageText(msg.getText(), messageId, msg.getTimeStamp()));
        Prattle.sendDirectMessage(msg);

        Prattle.sendAckMessage(ackMessage);
    }


    /**
     * Prepend the message text with id to parse and display in the client side.
     * This will be useful for identifying each messages uniquely from the console
     * and client side. Example: - actual message text -> "Hi there" - after
     * prepending -> "<142> Hi there" where 142 is the message id
     *
     * @param msgText   - the message text.
     * @param messageId - id for the message returned on persistence in the database.
     */
    String getPrependedMessageText(String msgText, long messageId, long timeStamp) {
        Date messageTimeStamp = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat(MessageConstants.MSG_DATE_FORMAT);

        StringBuilder text = new StringBuilder(MessageConstants.MSG_TIMESTAMP_PREFIX);
        text.append(format.format(messageTimeStamp));
        text.append(MessageConstants.MSG_TIMESTAMP_SUFFIX);
        text.append(MessageConstants.MSG_ID_PREFIX);
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
    private static boolean isGetInfoMessage(Message msg) {
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