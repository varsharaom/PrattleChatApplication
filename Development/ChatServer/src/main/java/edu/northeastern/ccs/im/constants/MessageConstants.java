package edu.northeastern.ccs.im.constants;

public final class MessageConstants {

    /** The delimiter to parse special messages. This should be the same in the client side */
    public static final String DELIMITER = "";

//    ALL MESSAGE CODES
    public static final String CUSTOM_COMMAND_PREFIX = "$$";
    public static final String CUSTOM_COMMAND_SUFFIX = "#";

    public static final String ACTION_MSG_IDENTIFIER = "ACTN";
    public static final String DIRECT_MSG_IDENTIFIER = "DRCT";
    public static final String GROUP_MSG_IDENTIFIER = "GRP";
    public static final String GROUP_CREATE_IDENTIFIER = "GRP_CRT";
    public static final String GROUP_DELETE_IDENTIFIER = "GRP_DLT";
    public static final String LOGIN_MSG_IDENTIFIER = "LGN";
    public static final String REGISTER_MSG_IDENTIFIER = "RGSTR";
    public static final String DELETE_MESSAGE_IDENTIFIER = "DLT";
    public static final String FORWARD_MSG_IDENTIFIER = "FWD";
    public static final String GET_USERS_IDENTIFIER = "GT_USRS";
    public static final String GET_GROUPS_IDENTIFIER = "GT_GRPS";
    public static final String GET_MY_USERS_IDENTIFIER = "GT_MY_USRS";
    public static final String GET_MY_GROUPS_IDENTIFIER = "GT_MY_GRPS";
    public static final String GET_INFO_IDENTIFIER = "GT_INFO";
    public static final String GROUP_ADD_MODERATOR = "ADD_MDRTR";
    public static final String GROUP_REMOVE_MEMBER_IDENTIFIER = "GRP_RMV_MMBR";
    public static final String GROUP_ADD_MEMBER_IDENTIFIER = "GRP_ADD_MMBR";
    public static final String LEAVE_GROUP_IDENTIFIER = "LV_GRP";
    public static final String REQUEST_GROUP_ADD_IDENTIFIER = "REQ_GRP_ADD_MMBR";

    public static final String ERROR_MSG_IDENTIFIER = "ERR";


    public static final String MSG_ID_PREFIX = "<";
    public static final String MSG_ID_SUFFIX = "> ";

//    SUCCESS HANDSHAKE MESSAGES
    public static final String REGISTER_SUCCESS_MSG = "You have successfully registered " +
            "as a new user..";
    public static final String LOGIN_SUCCESS_MSG = "You have successfully logged in..";
    public static final String DELETE_SUCCESS_MSG = "Message was successfully deleted..";

    public static final String GET_USERS_CONSOLE_INFO = "List of all Users :";
    public static final String GET_MY_USERS_CONSOLE_INFO = "List of all my connections :";
    public static final String GET_GROUPS_CONSOLE_INFO = "List of all Groups :";
    public static final String GET_MY_GROUPS_CONSOLE_INFO = "List of my groups : ";
    public static final String GROUP_DELETE_SUCCESS_MSG = "Group successfully deleted...";
    public static final String ADD_MDRTR_SUCCESS_MSG = "Moderator successfully added...";
    public static final String RMV_MMBR_SUCCESS_MSG = "Member successfully removed...";
    public static final String ADD_MMBR_SUCCESS_MSG = "Member successfully added...";
    public static final String LEAVE_GROUP_SUCCESS_MSG = "Sucessfully left the group...";
    public static final String REQUEST_GROUP_ADD_SUCCESS_MSG = "Add request sucessfully sent to" +
            "all moderators of the group...";

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
    public static final String ADD_MDRTR_INVALID_ERR = "Operation unsuccessful." +
            "Only group members can be made as moderator...";
    public static final String RMV_MMBR_INVALID_ERR = "Operation unsuccessful. " +
            "Only group members can be removed.";
    public static final String INVALID_USER_ERR = "Operation unsuccessful. " +
            "Only valid members can be added to the group.";
    public static final String INVALID_GROUP_MEMBER_ERR = "Operation unsuccessful. " +
            "Only group perform the requested action...";

    public static final String ERROR_DELETE_INVALID_MSG_ID = "Message ID entered is invalid." +
            "Please try with a valid message ID";
    public static final String ERROR_DELETE_SENDER_MISMATCH = "Deletion unsucessful. " +
            "You can only delete messages sent by you";
    public static final String ERROR_DELETE_RECEIVER_MISMATCH = "Deletion unsucessful. " +
            "You can only delete messages sent to the receiver(s) in the active window.";
    public static final String INVALID_MODERATOR_ERR = "Operation unsuccessful. " +
            "Only moderators are permitted to perform this action.";


    private MessageConstants() {

    }
}
