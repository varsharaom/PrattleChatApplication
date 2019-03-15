package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

import java.util.List;

import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
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
            handleDeleteMessages(message);
        }
        else if (isGetUsersMessage(message)) {
        		handleGetUsersMessage(message);
        }
        else {
            handleErrorMessages(message);
        }
    }

    private void handleDeleteMessages(Message message) {

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
            acknowledgementText = ClientRunnableConstants.REGISTER_SUCCESS_MSG;
            handShakeMessage = Message.makeRegisterAckMessage(MessageType.REGISTER
                    , message.getName(), acknowledgementText);
            // Persist user details
            queryHandler.createUser(message.getName(), message.getText(), message.getName());
        }
        else {
            acknowledgementText = ClientRunnableConstants.REGISTER_FAILURE_ERR;
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

            acknowledgementText = ClientRunnableConstants.LOGIN_SUCCESS_MSG;
            handShakeMessage = Message.makeLoginAckMessage(MessageType.LOGIN, message.getName(),
                    message.getName(), acknowledgementText);
        }
        else {

            acknowledgementText = ClientRunnableConstants.LOGIN_FAILURE_ERR;
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
                    ClientRunnableConstants.INVALID_DIRECT_RECEIVER_MSG);

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

        if (content.startsWith(ClientRunnableConstants.CUSTOM_COMMAND_PREFIX)) {

            String[] arr = content.split(" ", 2);

            if (arr.length > 1) {
                String type = getType(arr[0]);
                String restOfMessageText = arr[1];

                if (type.equalsIgnoreCase(MessageType.REGISTER.toString())) {
                    message = constructCustomRegisterMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.DIRECT.toString())) {
                    message = constructCustomDirectMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.LOGIN.toString())) {
                    message = constructCustomLoginMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.GROUP.toString())){
                    message = constructCustomGroupMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.DELETE.toString())) {
                    message = constructCustomDeleteMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.GET_USERS.toString())){
                    message = constructCustomGetUsersMessage(restOfMessageText);
                }
                else {
                    message = Message.makeErrorMessage(msg.getName(),
                            ClientRunnableConstants.UNKNOWN_MESSAGE_TYPE_ERR);
                }

            }
            else {
                message = Message.makeErrorMessage(msg.getName(),
                        ClientRunnableConstants.EMPTY_MESSAGE_ERR);
            }
        }
        return message;
    }

    private Message constructCustomDeleteMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 3);

        String senderName = arr[0];
        String receiverName = arr[1];
        long messageId = Long.parseLong(arr[2]);

        return Message.makeDeleteMessage(messageId, senderName, receiverName);
    }

    /**
     * Construct a login message based on the parsed input message.
     *
     */
    private Message constructCustomLoginMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 2);

        String userName = arr[0];
        String password = arr[1];

        return Message.makeLoginMessage(userName, password);
    }

    /**
     * Construct a register message based on the parsed input message.
     *
     */
    private Message constructCustomRegisterMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 2);

        String userName = arr[0];
        String password = arr[1];

        return Message.makeRegisterMessage(userName, password);

    }

    /**
     * Construct a direct message based on the parsed input message.
     *
     */
    private Message constructCustomDirectMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 3);

        String sender = arr[0];
        String receiver = arr[1];
        String actualContent = arr[2];

        return Message.makeDirectMessage(sender, receiver, sender + " : " + actualContent);
    }

    /**
     * Construct a group message based on the parsed input message.
     *
     */
    private Message constructCustomGroupMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 3);

        String sender = arr[0];
        String groupName = arr[1];
        String actualContent = arr[2];

        return Message.makeGroupMessage(sender, groupName, actualContent);
    }

    private Message constructCustomGetUsersMessage(String restOfMessageText) {
        List<User> userList = queryHandler.getAllUsers();

        StringBuilder sb = new StringBuilder();
        sb.append("List of users:\n");
        for(User user: userList) {
        		sb.append(user.getUserName() + "\n");
        }

        return Message.makeGetUsersMessage(sender, sender, sb.toString());
    }

    /**
     * Extracts message type from the message text which has the type prefixed in it.
     *
     */
    private String getType(String s) {
        String messageTypeAsString = "";
        if(s.length() > 2) {
//            removing $$ at the beginning and # at the end
            messageTypeAsString = s.substring(2, s.length()-1);
        }
        return messageTypeAsString;
    }
}
