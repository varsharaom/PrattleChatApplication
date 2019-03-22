package edu.northeastern.ccs.im.constants;

public final class MessageConstants {

    /** The delimiter to parse special messages. This should be the same in the client side */
    public static final String DELIMITER = "";

//    ALL MESSAGE CODES
    public static final String CUSTOM_COMMAND_PREFIX = "$$";
    public static final String CUSTOM_COMMAND_SUFFIX = "#";
    public static final String DIRECT_MSG_IDENTIFIER = "DRCT";
    public static final String GROUP_MSG_IDENTIFIER = "GRP";
    public static final String LOGIN_MSG_IDENTIFIER = "LGN";
    public static final String REGISTER_MSG_IDENTIFIER = "RGSTR";
    public static final String DELETE_MESSAGE_IDENTIFIER = "DLT";
    public static final String GET_USER_IDENTIFIER = "GT_USRS";
    public static final String ERROR_MSG_IDENTIFIER = "ERR";

    public static final String MSG_ID_PREFIX = "<";
    public static final String MSG_ID_SUFFIX = "> ";

//    SUCCESS HANDSHAKE MESSAGES
    public static final String REGISTER_SUCCESS_MSG = "You have successfully registered " +
            "as a new user..";
    public static final String LOGIN_SUCCESS_MSG = "You have successfully logged in..";
    public static final String DELETE_SUCCESS_MSG = "Message was successfully deleted..";

//    ERROR MESSAGES
    public static final String LOGIN_FAILURE_ERR = "Your credentials mismatch...";
    public static final String REGISTER_FAILURE_ERR = "Invalid credentials.." +
            "Please try with new user information.";
    public static final String INVALID_DIRECT_RECEIVER_MSG = "The user name does not exist. " +
            "Please enter a valid receiver name";
    public static final String INVALID_GROUP_RECEIVER_MSG = "The group name does not exist. " +
            "Please enter a valid group name";
    public static final String UNKNOWN_MESSAGE_TYPE_ERR = "Invalid Message type. " +
            "Please enter valid message type.";
    public static final String EMPTY_MESSAGE_ERR = "Message Content is empty...";

    public static final String ERROR_DELETE_INVALID_MSG_ID = "Message ID entered is invalid." +
            "Please try with a valid message ID";
    public static final String ERROR_DELETE_SENDER_MISMATCH = "Deletion unsucessful. " +
            "You can only delete messages sent by you";
    public static final String ERROR_DELETE_RECEIVER_MISMATCH = "Deletion unsucessful. " +
            "You can only delete messages sent to the receiver(s) in the active window.";


    private MessageConstants() {

    }
}
