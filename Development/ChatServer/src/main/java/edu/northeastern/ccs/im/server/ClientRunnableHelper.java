package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;
import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;

/**
 * Class that handles all the control flow for chat messages. Every instance of this class is
 * invoked from and registered with a specific ClientRunnable instance. The relation ensures messages
 * are appropriately queued in the active message queue. This class also takes care of calling the
 * Database Access Objects for various actions related to persistence.
 */
class ClientRunnableHelper {

    private ClientRunnable clientRunnable;
    private IQueryHandler queryHandler;
    
    ClientRunnableHelper(ClientRunnable clientRunnable, IQueryHandler queryHandler) {
        this.clientRunnable = clientRunnable;
        this.queryHandler = queryHandler;
    }

    /** Checks if the login credentials entered are valid*/
    private boolean isValidLoginCredentials(Message msg) {
        String password = queryHandler.getPassword(msg.getName());
        return password.equals(msg.getText());
    }

    /** Checks if the registration information are all valid enough to allow a new user creation */
    private boolean isValidRegistrationInfo(Message msg) {
        return !queryHandler.checkUserNameExists(msg.getName());
    }

    /**
     * This API is exposed to ClientRunnable for sending any kind of message.
     * Specifically, this routes the control based on register / login / private / group messages.
     */
    void handleMessages(Message message) {
        if (isRegisterOrLogin(message)) {
            handleRegisterLoginMessages(message);
        }
        else{
//            if (messageChecks(message)) {
            handleChatMessages(message);
//            }
//            else {
////                TODO - modify it as per needs. In what situation would this block be reached
//                Message sendMsg;
//                sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
//                        "Last message was rejected because it specified an incorrect user name.");
//                clientRunnable.enqueueMessage(sendMsg);
//            }
        }
    }
    /**
     * Checks the type of message and routes the control to either private or group message handler.
     */
    private void handleChatMessages(Message msg) {
        if(msg.isDirectMessage()) {
            Prattle.handleDirectMessages(msg);
        }
        else if(msg.isGroupMessage()) {
            Prattle.handleGroupMessage(msg);
        }
//		TODO - persist the message. (Caveat: check if group & pvt msgs are to be differently persisted)
        queryHandler.storeMessage(msg.getSenderId(), msg.getReceiverId(), msg.getMessageType(), msg.getText());
    }

    /**
     * Checks if the action is register or login and performs the respective action.
     */
    private void handleRegisterLoginMessages(Message msg) {
        if(msg.isRegisterMessage()) {
            handleRegisterMessage(msg);
        }
        else if(msg.isLoginMessage()) {
            handleLoginMessage(msg);
        }
    }

    /**
     * Checks if the registration credentials are valid. Bases on that send
     */
    private void handleRegisterMessage(Message message) {
        Message handShakeMessage;
        String acknowledgementText;

        if (isValidRegistrationInfo(message)) {
            acknowledgementText = ClientRunnableConstants.REGISTER_SUCCESS_MSG;

            // Persist user details
            queryHandler.createUser(message.getName(), message.getText(), message.getName());
        }
        else {
            acknowledgementText = ClientRunnableConstants.REGISTER_FAILURE_MSG;
        }
        handShakeMessage = Message.makeRegisterAckMessage(MessageType.REGISTER, message.getName(), acknowledgementText);
        Prattle.registerUser(handShakeMessage);
    }


    /** On a login request, this verifies user credentials and then acknowledges the user with
     * a success / failure message */
    private void handleLoginMessage(Message message) {
        Message handShakeMessage;
        String acknowledgementText;

        if (isValidLoginCredentials(message)) {
            acknowledgementText = ClientRunnableConstants.LOGIN_SUCCESS_MSG;
        }
        else {
            acknowledgementText = ClientRunnableConstants.LOGIN_FAILURE_MSG;
        }
        handShakeMessage = Message.makeLoginAckMessage(MessageType.LOGIN, message.getMsgSender(), message.getMsgSender(), acknowledgementText);
        Prattle.loginUser(handShakeMessage);
    }

    /** Checks if the message is either a login or a registration request */
    private boolean isRegisterOrLogin(Message msg) {
        return (msg.isRegisterMessage() || msg.isLoginMessage());
    }

    /**
     * Check if the message is properly formed. At the moment, this means checking
     * that the identifier is set properly.
     *
     * @param msg Message to be checked
     * @return True if message is correct; false otherwise
     */
    private boolean messageChecks(Message msg) {
        // Check that the message name matches.
        return (msg.getName() != null) && (msg.getName()
                .compareToIgnoreCase(clientRunnable.getName()) == 0);
    }

    public Message getCustomConstructedMessage(Message msg) {

        String content = msg.getText();
        Message message = msg;

        if (msg.getText().startsWith(ClientRunnableConstants.CUSTOM_COMMAND_PREFIX)) {

            String[] arr = content.split(" ", 2);

            if (arr.length > 1) {
                String type = getType(arr[0]);
                String restOfMessageText = arr[1];

                if (type.equalsIgnoreCase(MessageType.REGISTER.toString())) {
                    message = constructCustomRegisterMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.DIRECT.toString())) {
                    message = constructCustomDirectMessage(restOfMessageText, message.getMsgSender());
                }
                else if (type.equalsIgnoreCase(MessageType.LOGIN.toString())) {
                    message = constructCustomLoginMessage(restOfMessageText);
                }
                else if (type.equalsIgnoreCase(MessageType.GROUP.toString())) {
                    message = constructCustomGroupMessage(restOfMessageText, message.getMsgSender());
                }
            }
        }
        return message;
    }

    private Message constructCustomLoginMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 2);

        String userName = arr[0];
        String password = arr[1];

        return Message.makeLoginMessage(userName, password);
    }

    private Message constructCustomRegisterMessage(String restOfMessageText) {
        String[] arr = restOfMessageText.split(" ", 2);

        String userName = arr[0];
        String password = arr[1];

        return Message.makeRegisterMessage(userName, password);

    }

    private Message constructCustomDirectMessage(String restOfMessageText, String sender) {
        String[] arr = restOfMessageText.split(" ", 2);

        String receiver = arr[0];
        String actualContent = arr[1];

        return Message.makeDirectMessage(sender, receiver, actualContent);
    }

    private Message constructCustomGroupMessage(String restOfMessageText, String sender) {
        String[] arr = restOfMessageText.split(" ", 2);

        String groupName = arr[0];
        String actualContent = arr[1];

        return Message.makeGroupMessage(sender, groupName, actualContent);
    }

    private String getType(String s) {
        if(s.length() > 2) {
//            removing $$ at the beginning and # at the end
            return s.substring(2, s.length()-1);
        }
        return "";
    }
}
