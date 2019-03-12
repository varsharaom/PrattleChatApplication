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
