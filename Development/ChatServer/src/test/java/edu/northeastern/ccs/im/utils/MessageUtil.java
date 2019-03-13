package edu.northeastern.ccs.im.utils;

import com.sun.xml.internal.ws.api.model.MEP;
import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.serverim.Message;

public final class MessageUtil {

    public static Message getValidRegisterBroadcastMessage() {
        return Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                getValidRegisterMessageText());
    }

    public static Message getValidLoginBroadcastMessage() {
        return Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                getValidLoginMessageText());
    }

    public static Message getValidDirectBroadcastMessage() {
        return Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                getValidDirectMessageText());
    }

    public static Message getValidGroupBroadcastMessage() {
        return Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                getValidGroupMessageText());
    }

    private static String getValidGroupMessageText() {
        return ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                + ClientRunnableConstants.GROUP_MSG_IDENTIFIER
                + ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX
                + " receiverName Hey this is a group message to multiple senders";
    }

    private static String getValidDirectMessageText() {
        return ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                + ClientRunnableConstants.DIRECT_MSG_IDENTIFIER
                + ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX
                + " receiverName Hey this is a direct message to sender";
    }

    private static String getValidRegisterMessageText() {
        return ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                + ClientRunnableConstants.REGISTER_MSG_IDENTIFIER
                + ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX
                + " username password";
    }

    private static String getValidLoginMessageText() {
        return ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                + ClientRunnableConstants.LOGIN_MSG_IDENTIFIER
                + ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX
                + " username password";
    }

    private MessageUtil() {

    }
}
