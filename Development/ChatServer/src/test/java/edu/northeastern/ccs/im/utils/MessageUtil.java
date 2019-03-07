package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;

public final class MessageUtil {

    public static Message getValidRegisterBroadcastMessage() {
        return Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                getValidRegisterMessageText());
    }

    public static String getValidRegisterMessageText() {
        return ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                + ClientRunnableConstants.REGISTER_MSG_IDENTIFIER
                + ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX
                + " username password";
    }

    private MessageUtil() {

    }
}
