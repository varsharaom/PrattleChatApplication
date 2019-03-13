package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.constants.ClientRunnableConstants;

public class ClientRunnableHelperUtil {

    public static boolean isValidRegisterMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                        + ClientRunnableConstants.REGISTER_MSG_IDENTIFIER +
                        ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidLoginMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                        + ClientRunnableConstants.LOGIN_MSG_IDENTIFIER +
                        ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidDirectMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                        + ClientRunnableConstants.DIRECT_MSG_IDENTIFIER +
                        ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidGroupMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                        + ClientRunnableConstants.GROUP_MSG_IDENTIFIER +
                        ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX);
    }
}
