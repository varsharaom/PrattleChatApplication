package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;


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
    private boolean isValidLoginCredentials(Message msg) {
        return queryHandler.validateLogin(msg.getName(), msg.getText());
    }

    /**
     * Checks if the registration information are all valid enough to allow a new user creation
     */
    private boolean isUserPresent(String userName) {
        return queryHandler.checkUserNameExists(userName);
    }

    /**
     * This API is exposed to ClientRunnable for sending any kind of message.
     * Specifically, this routes the control based on register / login / private / group messages.
     */
    void handleMessages(Message message) {
        if (isRegisterOrLogin(message)) {
            handleRegisterLoginMessages(message);
        }
        else if (isDirectOrGroupMessage(message)){
            handleChatMessages(message);
        }
        else if (message.isDeleteMessage()) {
            handleDeleteMessages();
        }
        else if (isGetUsersMessage(message)) {
        		handleGetUsersMessage(message);
        }
        else {
            handleErrorMessages(message);
        }
    }

    private void handleDeleteMessages() {
        // DO nothing till the query handler integration is complete.
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
        else {
            Prattle.sendGroupMessage(msg);
        }
    }

    private void handleGetUsersMessage(Message msg) {
    		Prattle.sendDirectMessage(msg);
    }

    /**
     * Error messages are routed back to the sender.
     */
    private void handleErrorMessages(Message msg) {
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
            handShakeMessage = Message.makeRegisterAckMessage(MessageType.REGISTER
                    , message.getName(), acknowledgementText);
            // Persist user details
            queryHandler.createUser(message.getName(), message.getText(), message.getName());
        }
        else {
            acknowledgementText = MessageConstants.REGISTER_FAILURE_ERR;
            handShakeMessage = Message.makeErrorMessage(message.getName(), acknowledgementText);
        }

        Prattle.registerOrLoginUser(handShakeMessage);
    }


    /** On a login request, this verifies user credentials and then acknowledges the user with
     * a success / failure message */
    private void handleLoginMessage(Message message) {
        Message handShakeMessage;
        String acknowledgementText;

        if (isValidLoginCredentials(message)) {

            acknowledgementText = MessageConstants.LOGIN_SUCCESS_MSG;
            handShakeMessage = Message.makeLoginAckMessage(MessageType.LOGIN, message.getName(),
                    message.getName(), acknowledgementText);
        }
        else {

            acknowledgementText = MessageConstants.LOGIN_FAILURE_ERR;
            handShakeMessage = Message.makeErrorMessage(message.getName(),
                    acknowledgementText);
        }

        Prattle.registerOrLoginUser(handShakeMessage);
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
            message.setId(messageId);
            Prattle.sendDirectMessage(message);
        }

        else {

            Message errorMessage = Message.makeErrorMessage(message.getName(),
                    MessageConstants.INVALID_DIRECT_RECEIVER_MSG);

            Prattle.sendErrorMessage(errorMessage);
        }
    }


    /** Checks if the message is either a login or a registration request */
    private boolean isRegisterOrLogin(Message msg) {
        return (msg.isRegisterMessage() || msg.isLoginMessage());
    }

    private boolean isDirectOrGroupMessage(Message msg) {
        return (msg.isDirectMessage() || msg.isGroupMessage());
    }

    private boolean isGetUsersMessage(Message msg) {
        return (msg.isGetUsersMessage());
    }

    /**
     * Parse the input message text and return a custom constructed message according to the type.
     *
     */
    Message getCustomConstructedMessage(Message msg) {

        String content = msg.getText();
        Message message = msg;

        if (content.startsWith(MessageConstants.CUSTOM_COMMAND_PREFIX)) {
            message = MessageFactory.createMessage(message);

        }
//        TODO - should we change the incoming message as is? Or send a 5XX Error message
        return message;
    }
}
