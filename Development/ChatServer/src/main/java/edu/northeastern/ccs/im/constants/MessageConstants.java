package edu.northeastern.ccs.im.constants;

/**
 * The Class MessageConstants.
 */
public final class MessageConstants {

    /** The Constant DELIMITER. */
    public static final String DELIMITER = "";
    
    //  ALL MESSAGE CODES
    /** The Constant for prefix for the command scheme. */
    public static final String CUSTOM_COMMAND_PREFIX = "$$";
    
    /** The Constant for suffix for the command scheme. */
    public static final String CUSTOM_COMMAND_SUFFIX = "#";

    /** The Constant ACTION_MSG_IDENTIFIER. */
    public static final String ACTION_MSG_IDENTIFIER = "ACTN";
    
    /** The command for direct message. */
    public static final String DIRECT_MSG_IDENTIFIER = "DRCT";
    
    /** The command for group message. */
    public static final String GROUP_MSG_IDENTIFIER = "GRP";
    
    /** The command for creating a group. */
    public static final String GROUP_CREATE_IDENTIFIER = "GRP_CRT";
    
    /** The command for deleting a group. */
    public static final String GROUP_DELETE_IDENTIFIER = "GRP_DLT";
    
    /** The command to login a user. */
    public static final String LOGIN_MSG_IDENTIFIER = "LGN";
    
    /** The command to register a user. */
    public static final String REGISTER_MSG_IDENTIFIER = "RGSTR";
    
    /** The command to delete a message. */
    public static final String DELETE_MESSAGE_IDENTIFIER = "DLT";
    
    /** The command to forward a message. */
    public static final String FORWARD_MSG_IDENTIFIER = "FWD";
    
    /** The command to get all users. */
    public static final String GET_USERS_IDENTIFIER = "GT_USRS";
    
    /** The command to get all groups. */
    public static final String GET_GROUPS_IDENTIFIER = "GT_GRPS";
    
    /** The command to get users within the circle. */
    public static final String GET_MY_USERS_IDENTIFIER = "GT_MY_USRS";
    
    /** The command to get groups a user belongs to. */
    public static final String GET_MY_GROUPS_IDENTIFIER = "GT_MY_GRPS";
    
    /** The command to get information about application entities such as users and groups. */
    public static final String GET_INFO_IDENTIFIER = "GT_INFO";
    
    /** The command to add a moderator to a group. */
    public static final String GROUP_ADD_MODERATOR = "ADD_MDRTR";
    
    /** The command to remove a member from a group. */
    public static final String GROUP_REMOVE_MEMBER_IDENTIFIER = "GRP_RMV_MMBR";
    
    /** The command to add a member to a group. */
    public static final String GROUP_ADD_MEMBER_IDENTIFIER = "GRP_ADD_MMBR";
    
    /** The command to leave a group. */
    public static final String LEAVE_GROUP_IDENTIFIER = "LV_GRP";
    public static final String REQUEST_GROUP_ADD_IDENTIFIER = "GRP_REQ";

    /** An error message code for server responses. */
    public static final String ERROR_MSG_IDENTIFIER = "ERR";

    /** The prefix for message id for messages shown on the console. */
    public static final String MSG_ID_PREFIX = "<";
    
    /** The suffix for message id for messages shown on the console. */
    public static final String MSG_ID_SUFFIX = "> ";

    /** The Constant REGISTER_SUCCESS_MSG. */
    public static final String REGISTER_SUCCESS_MSG = "You have successfully registered " +
            "as a new user..";
    
    /** The Constant LOGIN_SUCCESS_MSG. */
    public static final String LOGIN_SUCCESS_MSG = "You have successfully logged in..";
    
    /** The Constant DELETE_SUCCESS_MSG. */
    public static final String DELETE_SUCCESS_MSG = "Message was successfully deleted..";

    /** The Constant GET_USERS_CONSOLE_INFO. */
    public static final String GET_USERS_CONSOLE_INFO = "List of all Users :";
    
    /** The Constant GET_MY_USERS_CONSOLE_INFO. */
    public static final String GET_MY_USERS_CONSOLE_INFO = "List of all my connections :";
    
    /** The Constant GET_GROUPS_CONSOLE_INFO. */
    public static final String GET_GROUPS_CONSOLE_INFO = "List of all Groups :";
    
    /** The Constant GET_MY_GROUPS_CONSOLE_INFO. */
    public static final String GET_MY_GROUPS_CONSOLE_INFO = "List of my groups : ";
    
    /** The Constant GROUP_DELETE_SUCCESS_MSG. */
    public static final String GROUP_DELETE_SUCCESS_MSG = "Group successfully deleted...";
    
    /** The Constant ADD_MDRTR_SUCCESS_MSG. */
    public static final String ADD_MDRTR_SUCCESS_MSG = "Moderator successfully added...";
    
    /** The Constant RMV_MMBR_SUCCESS_MSG. */
    public static final String RMV_MMBR_SUCCESS_MSG = "Member successfully removed...";
    
    /** The Constant ADD_MMBR_SUCCESS_MSG. */
    public static final String ADD_MMBR_SUCCESS_MSG = "Member successfully added...";
    
    /** The Constant LEAVE_GROUP_SUCCESS_MSG. */
    public static final String LEAVE_GROUP_SUCCESS_MSG = "Sucessfully left the group...";
    public static final String REQUEST_GROUP_ADD_SUCCESS_MSG = "Add request sucessfully sent to" +
            "all moderators of the group...";

	/** The Constant LOGIN_FAILURE_ERR. */
    public static final String LOGIN_FAILURE_ERR = "Your credentials mismatch...";
    
    /** The Constant REGISTER_FAILURE_ERR. */
    public static final String REGISTER_FAILURE_ERR = "Invalid credentials.." +
            "Please try with new user information.";
    
    /** The Constant INVALID_DIRECT_RECEIVER_MSG. */
    public static final String INVALID_DIRECT_RECEIVER_MSG = "The user name does not exist. " +
            "Please enter a valid receiver name";
    
    /** The Constant INVALID_GROUP_RECEIVER_MSG. */
    public static final String INVALID_GROUP_RECEIVER_MSG = "The group name does not exist. " +
            "Please enter a valid group name";
    
    /** The Constant UNKNOWN_MESSAGE_TYPE_ERR. */
    public static final String UNKNOWN_MESSAGE_TYPE_ERR = "Invalid Message type. " +
            "Please enter valid message type.";
    
    public static final String OPERATION_UNSUCCESSFUL_ERR = "Operation unsuccessful. ";
    
    /** The Constant EMPTY_MESSAGE_ERR. */
    public static final String EMPTY_MESSAGE_ERR = "Message Content is empty...";
    
    /** The Constant ADD_MDRTR_INVALID_ERR. */
    public static final String ADD_MDRTR_INVALID_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Only group members can be made as moderator...";

    /** The Constant RMV_MMBR_INVALID_ERR. */
    public static final String RMV_MMBR_INVALID_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Only group members can be removed.";
    
    /** The Constant ADD_MMBR_INVALID_ERR. */
    public static final String ADD_MMBR_INVALID_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Only valid members can be added to the group.";

    /** The Constant INVALID_USER_ERR. */
    public static final String INVALID_USER_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Only valid members can be added to the group.";

    /** The Constant INVALID_GROUP_MEMBER_ERR. */
    public static final String INVALID_GROUP_MEMBER_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Only group perform the requested action...";
    public static final String INVALID_ACTION_TYPE_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Invalid Action type. Please retry with a valid one.";
    public static final String INVALID_GROUP_INFO_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Invalid get info command. Please retry with a valid command... ";

    /** The Constant ERROR_DELETE_INVALID_MSG_ID. */
    public static final String ERROR_DELETE_INVALID_MSG_ID = "Message ID entered is invalid." +
            "Please try with a valid message ID";
    
    /** The Constant ERROR_DELETE_SENDER_MISMATCH. */
    public static final String ERROR_DELETE_SENDER_MISMATCH = "Deletion unsucessful. " +
            "You can only delete messages sent by you";
    
    /** The Constant ERROR_DELETE_RECEIVER_MISMATCH. */
    public static final String ERROR_DELETE_RECEIVER_MISMATCH = "Deletion unsucessful. " +
            "You can only delete messages sent to the receiver(s) in the active window.";
    
    /** The Constant INVALID_MODERATOR_ERR. */
    public static final String INVALID_MODERATOR_ERR = OPERATION_UNSUCCESSFUL_ERR +
            "Only moderators are permitted to perform this action.";

    /**
     * Private constructor for class with static methods.
     */
    private MessageConstants() {

    }
}
