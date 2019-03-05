package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.constants.ClientRunnableConstants;

/**
 * Class that handles all the control flow for chat messages. Every instance of this class is
 * invoked from and registered with a specific ClientRunnable instance. The relation ensures messages
 * are appropriately queued in the active message queue. This class also takes care of calling the
 * Database Access Objects for various actions related to persistence.
 */
class ClientRunnableHelper {

    private ClientRunnable clientRunnable;

    ClientRunnableHelper(ClientRunnable clientRunnable) {
        this.clientRunnable = clientRunnable;
    }


    /** Checks if the login credentials entered are valid*/
    private boolean isValidLoginCredentials(Message msg) {
        //	TODO - make a DB call to verify user credentials and then permit login
        return false;
    }

    /** Checks if the registration information are all valid enough to allow a new user creation */
    private boolean isValidRegistrationInfo(Message msg) {
        //	TODO - make a DB call to verify user credentials and then permit register.
        //	 Checks needed: validate data types and constraints. validate exclusiveness of credentials
        return false;
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
            if (messageChecks(message)) {
                handleChatMessages(message);
            }
            else {
//                TODO - modify it as per needs. In what situation would this block be reached
                Message sendMsg;
                sendMsg = Message.makeBroadcastMessage(ServerConstants.BOUNCER_ID,
                        "Last message was rejected because it specified an incorrect user name.");
                clientRunnable.enqueueMessage(sendMsg);
            }
        }
    }
    /**
     * Checks the type of message and routes the control to either private or group message handler.
     */
    private void handleChatMessages(Message msg) {
        if(msg.isPrivateMessage()) {
            Prattle.handlePrivateMessage(msg);
        }
        else if(msg.isGroupMessage()) {
            Prattle.handleGroupMessage(msg);
        }
//		TODO - persist the message. (Caveat: check if group & pvt msgs are to be differently persisted)
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

//      TODO - For this the credentials needs to be parsed / extracted from the
//       message object (or wrapper).
        if (isValidRegistrationInfo(message)) {
            acknowledgementText = ClientRunnableConstants.REGISTER_SUCCESS_MSG;
        }
        else {
            acknowledgementText = ClientRunnableConstants.REGISTER_FAILURE_MSG;
        }

        handShakeMessage = Message.makeRegisterAckMessage(MessageType.REGISTER, message.getSenderId()
                , acknowledgementText);

        //        TODO - persist the user info using a Create Operation.

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

        handShakeMessage = Message.makeLoginAckMessage(MessageType.LOGIN, message.getSenderId()
                , acknowledgementText);
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
}
