package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.serverim.Message;

import static edu.northeastern.ccs.im.constants.MessageConstants.*;
import static edu.northeastern.ccs.im.constants.MessageTestConstants.SECOND_USER;
import static edu.northeastern.ccs.im.constants.MessageTestConstants.SIMPLE_USER;

public final class MessageUtil {

    public static Message getValidRegisterBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidRegisterMessageText());
    }

    public static Message getValidLoginBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidLoginMessageText());
    }
    
    public static Message getValidLoginBroadcastMessageWithDifferentUser() {
        return Message.makeBroadcastMessage(SECOND_USER, getValidLoginMessageText());
    }

    public static Message getValidDirectBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidDirectMessageText());
    }
    
    public static Message getValidGetUsersMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
        		getValidGetUsersMessageText());
    }

    public static Message getValidGetMyUsersMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidGetMyUsersMessageText());
    }

    public static Message getValidGetGroupsMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidGetGroupsMessageText());
    }

    public static Message getValidGetMyGroupsMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidGetMyGroupsMessageText());
    }

    public static Message getValidDirectBroadcastMessageDifferentUser() {
        return Message.makeBroadcastMessage(SECOND_USER, getValidDirectMessageText());
    }

    public static Message getValidGroupBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidGroupMessageText());
    }

    public static Message getValidDeleteBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidDeleteMessageText());
    }

    public static Message getEmptyBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getInvalidMessageText());
    }

    public static Message getInvalidPrefixBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getEmptyMessageText());
    }

    public static Message getInvalidMessageWithInvalidType() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getInvalidTypeMessageText());
    }

    public static Message getValidDeleteMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidDeleteMessageText());
    }

    public static Message getValidForwardMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidForwardMessageText());
    }

    public static Message getValidGroupCreateMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER, getValidGroupCreateMessageText());
    }

    public static Message getValidGroupDeleteMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER, getValidGroupDeleteMessageText());
    }

    public static Message getValidAddModeratorMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER, getValidAddModeratorMessageText());
    }

    public static Message getValidRemoveMemberMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER, getValidRemoveMemberMessageText());
    }

    public static Message getValidAddMemberMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER, getValidAddMemberMessageText());
    }

    public static Message getValidLeaveGroupMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER, getValidLeaveGroupMessageText());
    }

    private static String getValidLeaveGroupMessageText() {
        return CUSTOM_COMMAND_PREFIX + ACTION_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " " + LEAVE_GROUP_IDENTIFIER + " groupName senderName";
    }

    private static String getValidAddMemberMessageText() {
        return CUSTOM_COMMAND_PREFIX + ACTION_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " " + GROUP_ADD_MEMBER_IDENTIFIER + " newMember groupName senderName";
    }

    private static String getValidRemoveMemberMessageText() {
        return CUSTOM_COMMAND_PREFIX + ACTION_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " " + GROUP_REMOVE_MEMBER_IDENTIFIER + " groupMember groupName senderName";
    }

    private static String getValidAddModeratorMessageText() {
        return CUSTOM_COMMAND_PREFIX + ACTION_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " " + GROUP_ADD_MODERATOR + " toBeModerator groupName senderName";
    }

    private static String getValidGroupDeleteMessageText() {
        return CUSTOM_COMMAND_PREFIX + ACTION_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " " + GROUP_DELETE_IDENTIFIER + " groupName senderName";
    }

    private static String getValidGroupCreateMessageText() {
        return CUSTOM_COMMAND_PREFIX + ACTION_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " " + GROUP_CREATE_IDENTIFIER + " newGroupName creatorName";
    }

    private static String getValidForwardMessageText() {
        return CUSTOM_COMMAND_PREFIX + FORWARD_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " sender receiver 123";
    }

    private static String getInvalidTypeMessageText() {
        return CUSTOM_COMMAND_PREFIX
                + ""
                + " sender receiver 142";
    }

    private static String getValidDeleteMessageText() {
        return CUSTOM_COMMAND_PREFIX
                + DELETE_MESSAGE_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
                + " sender receiver 142";
    }

    private static String getEmptyMessageText() {
        return DIRECT_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + "  ";
    }

    private static String getInvalidMessageText() {
        return CUSTOM_COMMAND_PREFIX + ""  + CUSTOM_COMMAND_SUFFIX;
    }

    private static String getValidGroupMessageText() {
        return CUSTOM_COMMAND_PREFIX + GROUP_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX
                + " senderName receiverName Hey this is a group message to multiple senders";
    }

    private static String getValidDirectMessageText() {
        return CUSTOM_COMMAND_PREFIX + DIRECT_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " senderName receiverName "
                + " Hey this is a direct message to sender";
    }
    
    private static String getValidGetUsersMessageText() {
        return CUSTOM_COMMAND_PREFIX + GET_INFO_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + GET_USERS_IDENTIFIER
                + " senderName";
    }

    private static String getValidGetGroupsMessageText() {
        return CUSTOM_COMMAND_PREFIX + GET_INFO_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + GET_GROUPS_IDENTIFIER
                + " senderName";
    }

    private static String getValidGetMyGroupsMessageText() {
        return CUSTOM_COMMAND_PREFIX + GET_INFO_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + GET_MY_GROUPS_IDENTIFIER
                + " senderName";
    }

    private static String getValidGetMyUsersMessageText() {
        return CUSTOM_COMMAND_PREFIX + GET_INFO_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + GET_MY_USERS_IDENTIFIER
                + " senderName";
    }

    private static String getValidRegisterMessageText() {
        return CUSTOM_COMMAND_PREFIX + REGISTER_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + SIMPLE_USER
                + " password";
    }

    private static String getValidLoginMessageText() {
        return CUSTOM_COMMAND_PREFIX + LOGIN_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + SIMPLE_USER
                + " password";
    }


    public static boolean isErrorMessage(Message message) {
        String[] parsedMessageContents = message.getText().split(" ");
        String errorMessageKeyword = CUSTOM_COMMAND_PREFIX
                + ERROR_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX;

        if (parsedMessageContents.length == 0) {
            return false;
        }

        String actualMessageKeyword =
                parsedMessageContents[0];
        return (errorMessageKeyword.equals(actualMessageKeyword));
    }

    private MessageUtil() {

    }

}
