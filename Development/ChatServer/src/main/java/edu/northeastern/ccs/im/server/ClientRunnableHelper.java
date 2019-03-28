package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.persistence.QueryFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Class that handles all the control flow for chat messages. Every instance of this class is
 * invoked from and registered with a specific ClientRunnable instance. The relation ensures messages
 * are appropriately queued in the active message queue. This class also takes care of calling the
 * Database Access Objects for various actions related to persistence.
 */
class ClientRunnableHelper {

    private IQueryHandler queryHandler;

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
    private boolean isGroupPresent (String groupName) {
        return queryHandler.checkGroupNameExists(groupName);
    }

    /**
     * This API is exposed to ClientRunnable for sending any kind of message.
     * Specifically, this routes the control based on register / login / private / group messages.
     */
    protected void handleMessages(Message message) {
        if (isRegisterOrLogin(message)) {
            handleRegisterLoginMessages(message);
        }
        else if (isChatMessage(message)){
            handleChatMessages(message);
        }
        else if (message.isDeleteMessage()) {
            handleDeleteMessages(message);
        }
        else if (isGetInfoMessage(message)) {
        		handleGetInfoMessages(message);
        }
        else if (isActionMessage(message)) {
            handleActionMessages(message);
        }
        else {
            handleErrorMessages(message);
        }
    }

    private boolean isActionMessage(Message message) {
        return message.isActionMessage();
    }

    private void handleDeleteMessages(Message clientMessage) {
        Message dbMessage = queryHandler.getMessage(clientMessage.getId());
        Message handshakeMessage;

        if (null == dbMessage) {
//            ERROR: message not found
            handshakeMessage = Message.makeErrorMessage(clientMessage.getName(),
                    MessageConstants.ERROR_DELETE_INVALID_MSG_ID);
        }
        else if (!clientMessage.getName().equalsIgnoreCase(dbMessage.getName())) {
//            ERROR: sender invalid
            handshakeMessage = Message.makeErrorMessage(clientMessage.getName(),
                    MessageConstants.ERROR_DELETE_SENDER_MISMATCH);
        }
        else if (!clientMessage.getMsgReceiver().equalsIgnoreCase(dbMessage.getMsgReceiver())) {
//            ERROR: receiver invalid
            handshakeMessage = Message.makeErrorMessage(clientMessage.getName(),
                    MessageConstants.ERROR_DELETE_RECEIVER_MISMATCH);
        }
        else {
            queryHandler.deleteMessage(clientMessage.getId());
//            Deletion successful
            handshakeMessage = Message.makeAckMessage(MessageType.DELETE,
                    clientMessage.getName(), MessageConstants.DELETE_SUCCESS_MSG);
        }
        Prattle.sendAckMessage(handshakeMessage);
    }

    /**
     * Checks if the action is register or login and performs the respective action.
     */
    private void handleRegisterLoginMessages(Message msg) {
        if(msg.isRegisterMessage()) {
            handleRegisterMessage(msg);
        }
        else {
            handleLoginMessage(msg);
        }
    }

    /**
     * Checks the type of message and routes the control to either private or group message handler.
     */
    private void handleChatMessages(Message msg) {
        if(msg.isDirectMessage()) {
            handleDirectMessages(msg);
        }
        else if (msg.isGroupMessage()) {
            handleGroupMessages(msg);
        }
        else {
            handleForwardMessages(msg);
        }
    }

    private void handleGetInfoMessages (Message msg) {
        Prattle.sendDirectMessage(msg);
    }

    private void handleActionMessages(Message message) {
        String[] contents = message.getText().split(" ");
        String actualAction = contents[0];
        if (actualAction.equals(MessageConstants.GROUP_CREATE_IDENTIFIER)) {
            handleCreateGroup(message.getName(), contents);
        }
        else if (actualAction.equals(MessageConstants.GROUP_DELETE_IDENTIFIER)) {
            handleDeleteGroup(message.getName(), contents);
        }
        else if (actualAction.equals(MessageConstants.GROUP_ADD_MODERATOR)) {
            handleCreateModerator(message.getName(), contents);
        }
        else if (actualAction.equals(MessageConstants.GROUP_REMOVE_MEMBER_IDENTIFIER)) {
            handleRemoveMember(message.getName(), contents);
        }
        else if (actualAction.equals(MessageConstants.GROUP_ADD_MEMBER_IDENTIFIER)) {
            handleAddMember(message.getName(), contents);
        }
        else if (actualAction.equals(MessageConstants.LEAVE_GROUP_IDENTIFIER)) {
            handleLeaveGroup(message.getName(), contents);
        }
        else if (actualAction.equals(MessageConstants.REQUEST_GROUP_ADD_IDENTIFIER)) {
            handleRequestGroupAdd(message.getName(), contents);
        }

    }

    private void handleRequestGroupAdd(String senderName, String[] contents) {
        String toBeMember = contents[1];
        String groupName = contents[2];
        String ackMessage;

        if (queryHandler.isGroupMember(groupName, senderName)) {
            if (queryHandler.checkUserNameExists(toBeMember)) {
                ackMessage = MessageConstants.REQUEST_GROUP_ADD_SUCCESS_MSG;
                publishRequestToModerators(groupName, senderName, toBeMember);
            }
            else {
                ackMessage = MessageConstants.INVALID_USER_ERR;
            }
        }
        else {
            ackMessage = MessageConstants.INVALID_GROUP_MEMBER_ERR;
        }

        Message handshakeMessage = Message.makeAckMessage(MessageType.ACTION, senderName, ackMessage);
        Prattle.sendAckMessage(handshakeMessage);
    }

    private void publishRequestToModerators(String groupName, String senderName, String toBeMember) {
        List<String> moderators = queryHandler.getGroupModerators(groupName);
        Set<String> moderatorSet = new HashSet<>(moderators);
        String content = String.format("%s has requested to add %s to the group %s"
                , senderName, toBeMember , groupName);

        Message message = Message.makeBroadcastMessage(senderName, content);
        Prattle.sendMessageToMultipleUsers(message, moderatorSet);
    }

    private void handleLeaveGroup(String sender, String[] contents) {
        String groupName = contents[1];
        String ackMessage;

        if (queryHandler.isGroupMember(groupName, sender)) {
            queryHandler.removeMember(groupName, sender);
            ackMessage = MessageConstants.LEAVE_GROUP_SUCCESS_MSG;
        }
        else {
            ackMessage = MessageConstants.INVALID_GROUP_MEMBER_ERR;
        }
        Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
        Prattle.sendAckMessage(message);
    }

    private void handleAddMember(String sender, String[] contents) {
        String member = contents[1];
        String groupName = contents[2];
        String ackMessage;

        if (queryHandler.isModerator(sender, groupName)) {
            if (queryHandler.checkUserNameExists(member)) {
                queryHandler.addGroupMember(member, groupName, 1);
                ackMessage = MessageConstants.ADD_MMBR_SUCCESS_MSG;
            }
            else {
                ackMessage = MessageConstants.INVALID_USER_ERR;
            }
        }
        else {
            ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
        }

        Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
        Prattle.sendAckMessage(message);
    }

    private void handleRemoveMember(String sender, String[] contents) {
        String member = contents[1];
        String groupName = contents[2];
        String ackMessage;

        if (queryHandler.isModerator(sender, groupName)) {
            if (queryHandler.isGroupMember(groupName, sender)) {
                queryHandler.removeMember(groupName, member);
                ackMessage = MessageConstants.RMV_MMBR_SUCCESS_MSG;
            }
            else {
                ackMessage = MessageConstants.RMV_MMBR_INVALID_ERR;
            }
        }
        else {
            ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
        }

        Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
        Prattle.sendAckMessage(message);
    }

    private void handleCreateModerator(String sender, String[] contents) {
        String toBeModerator = contents[1];
        String groupName = contents[2];
        String ackMessage;

        if (queryHandler.isModerator(sender, groupName)) {
            if (queryHandler.isGroupMember(groupName, sender)) {
                queryHandler.makeModerator(groupName, toBeModerator);
                ackMessage = MessageConstants.ADD_MDRTR_SUCCESS_MSG;
            }
            else {
                ackMessage = MessageConstants.ADD_MDRTR_INVALID_ERR;
            }
        }
        else {
            ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
        }

        Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
        Prattle.sendAckMessage(message);
    }

    private void handleDeleteGroup (String sender, String[] contents) {
        String groupName = contents[1];
        String ackMessage;
        if (queryHandler.isModerator(sender, groupName)) {
            queryHandler.deleteGroup(sender, groupName);
            ackMessage = MessageConstants.GROUP_DELETE_SUCCESS_MSG;
        }
        else {
            ackMessage = MessageConstants.INVALID_MODERATOR_ERR;
        }
        Message message = Message.makeAckMessage(MessageType.ACTION, sender, ackMessage);
        Prattle.sendAckMessage(message);
    }

    private void handleCreateGroup (String sender, String[] contents) {
        String groupName = contents[1];
        queryHandler.createGroup(sender, groupName);
    }


    /**
     * Error messages are routed back to the sender.
     */
    private void handleErrorMessages (Message msg) {
        Prattle.sendErrorMessage(msg);
    }

    /**
     * Checks if the registration credentials are valid.
     * Based on that send an acknowledgement message.
     */
    private void handleRegisterMessage(Message message) {
        Message handShakeMessage;
        String acknowledgementText;

        if (!isUserPresent(message.getName())) {
            acknowledgementText = MessageConstants.REGISTER_SUCCESS_MSG;
            handShakeMessage = Message.makeAckMessage(MessageType.REGISTER
                    , message.getName(), acknowledgementText);
            // Persist user details
            queryHandler.createUser(message.getName(), message.getText(), message.getName());
        }
        else {
            acknowledgementText = MessageConstants.REGISTER_FAILURE_ERR;
            handShakeMessage = Message.makeErrorMessage(message.getName(), acknowledgementText);
        }

        Prattle.sendAckMessage(handShakeMessage);
    }

    /** On a login request, this verifies user credentials and then acknowledges the user with
     * a success / failure message */
    private void handleLoginMessage (Message message) {
        Message handShakeMessage;
        String acknowledgementText;

        long userId = isValidLoginCredentials(message);
        if (userId != -1) {

            acknowledgementText = MessageConstants.LOGIN_SUCCESS_MSG;
            handShakeMessage = Message.makeLoginAckMessage(MessageType.LOGIN, message.getName(),
                    message.getName(), acknowledgementText);
        }
        else {
            acknowledgementText = MessageConstants.LOGIN_FAILURE_ERR;
            handShakeMessage = Message.makeErrorMessage(message.getName(),
                    acknowledgementText);
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
        for(Message message : messageList) {
        		Prattle.sendDirectMessage(message);
        }
    }

    /**
     * Checks if the receiver is valid. If valid, then send the direct message to the reeciver.
     * Otherwise, sends an error message to the sender.
     * @param message - custom constructed message by the parser
     */
    private void handleDirectMessages(Message message) {
        if (isUserPresent(message.getMsgReceiver())) {
            long messageId = queryHandler.storeMessage(message.getName(), message.getMsgReceiver(),
                    message.getMessageType(),
                    message.getText());

            message.setText(getPrependedMessageText(message.getText(), messageId));
            Prattle.sendDirectMessage(message);
        }
        else {
            Message errorMessage = Message.makeErrorMessage(message.getName(),
                    MessageConstants.INVALID_DIRECT_RECEIVER_MSG);
            Prattle.sendErrorMessage(errorMessage);
        }
    }

    private void handleGroupMessages(Message message) {
        String groupName = message.getMsgReceiver();
        if (isGroupPresent(groupName)) {
            long messageId = queryHandler.storeMessage(message.getName(), message.getMsgReceiver(),
                    message.getMessageType(),
                    message.getText());

            message.setText(getPrependedMessageText(message.getText(), messageId));
            Set<String> groupMemberNames = queryHandler.getAllGroupMembers(groupName);
            Prattle.sendMessageToMultipleUsers(message, groupMemberNames);
        }
        else {
            Message errorMessage = Message.makeErrorMessage(message.getName(),
                    MessageConstants.INVALID_GROUP_RECEIVER_MSG);
            Prattle.sendErrorMessage(errorMessage);
        }
    }

    private void handleForwardMessages(Message message) {
        if (isUserPresent(message.getMsgReceiver())) {
            long messageId = queryHandler.storeMessage(message.getName(), message.getMsgReceiver(),
                    message.getMessageType(),
                    getForwardMessageText(message));

            message.setText(getPrependedMessageText(message.getText(), messageId));
            Prattle.sendDirectMessage(message);
        }
        else {
            Message errorMessage = Message.makeErrorMessage(message.getName(),
                    MessageConstants.INVALID_DIRECT_RECEIVER_MSG);
            Prattle.sendErrorMessage(errorMessage);
        }
    }

    private String getForwardMessageText(Message message) {
        StringBuilder sb = new StringBuilder(message.getText());
        sb.append(" <<< FORWARDED MESSAGE >>> ");
        return sb.toString();
    }

    /**
     * Prepend the message text with id to parse and display in the client side.
     * This will be useful for identifying each messages uniquely from the console and client side.
     * Example:
     *         - actual message text -> "Hi there"
     *         - after prepending    -> "<142> Hi there"
     *         where 142 is the message id
     *
     * @param msgText - the message text.
     * @param messageId - id for the message returned on persistence in the database.
     */
    String getPrependedMessageText(String msgText, long messageId) {
        StringBuilder text = new StringBuilder(MessageConstants.MSG_ID_PREFIX);
        text.append(messageId);
        text.append(MessageConstants.MSG_ID_SUFFIX);
        text.append(msgText);

        return text.toString();
    }


    /** Checks if the message is either a login or a registration request */
    private boolean isRegisterOrLogin(Message msg) {
        return (msg.isRegisterMessage() || msg.isLoginMessage());
    }

    /**
     * Returns true if the message is a direct of group message. Otherwise false.
     */
    private boolean isChatMessage(Message msg) {
        return (msg.isDirectMessage() || msg.isGroupMessage());
    }

    /**
     * Returns true if the message is a get all Users message. Otherwise false.
     */
    static boolean isGetInfoMessage(Message msg) {
        return msg.isGetInfoMessage();
    }

    /**
     * Parse the input message text and return a custom constructed message according to the type.
     *
     */
    Message getCustomConstructedMessage(Message msg) {

        String content = msg.getText();
        Message message = msg;

        if (content.startsWith(MessageConstants.CUSTOM_COMMAND_PREFIX)) {
            message = MessageFactory.createMessage(message, queryHandler);

        }
//        TODO - should we change the incoming message as is? Or send a 5XX Error message
        return message;
    }
}
