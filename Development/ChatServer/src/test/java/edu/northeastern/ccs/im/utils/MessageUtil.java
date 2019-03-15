package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.serverim.Message;

import static edu.northeastern.ccs.im.constants.ClientRunnableConstants.*;
import static edu.northeastern.ccs.im.constants.MessageConstants.SIMPLE_USER;

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
        return Message.makeBroadcastMessage(MessageConstants.SECOND_USER,
                getValidLoginMessageText());
    }

    public static Message getValidDirectBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidDirectMessageText());
    }
    
    public static Message getValidGetUsersMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
        		getValidGetUsersMessageText());
    }
    
    public static Message getValidDirectBroadcastMessageDifferentUser() {
        return Message.makeBroadcastMessage(MessageConstants.SECOND_USER,
                getValidDirectMessageText());
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
        return CUSTOM_COMMAND_PREFIX + GET_USER_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " senderName";
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
