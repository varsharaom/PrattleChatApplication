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
    
    public static Message getValidDirectBroadcastMessageDifferentUser() {
        return Message.makeBroadcastMessage(MessageConstants.SECOND_USER,
                getValidDirectMessageText());
    }

    public static Message getValidGroupBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getValidGroupMessageText());
    }

    public static Message getInvalidBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getInvalidMessageText());
    }

    public static Message getEmptyBroadcastMessage() {
        return Message.makeBroadcastMessage(SIMPLE_USER,
                getEmptyMessageText());
    }

    private static String getEmptyMessageText() {
        return CUSTOM_COMMAND_PREFIX + DIRECT_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + "";
    }

    private static String getInvalidMessageText() {
        return CUSTOM_COMMAND_PREFIX + ""  + CUSTOM_COMMAND_SUFFIX;
    }

    private static String getValidGroupMessageText() {
        return CUSTOM_COMMAND_PREFIX + GROUP_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX
                + " receiverName Hey this is a group message to multiple senders";
    }

    private static String getValidDirectMessageText() {
        return CUSTOM_COMMAND_PREFIX + DIRECT_MSG_IDENTIFIER
                + CUSTOM_COMMAND_SUFFIX + " " + SIMPLE_USER
                + " Hey this is a direct message to sender";
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
