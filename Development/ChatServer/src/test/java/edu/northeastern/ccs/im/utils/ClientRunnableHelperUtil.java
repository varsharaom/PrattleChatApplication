package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.constants.MessageConstants;

public class ClientRunnableHelperUtil {
	
	private ClientRunnableHelperUtil() {
		
	}

    public static boolean isValidRegisterMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                MessageConstants.CUSTOM_COMMAND_PREFIX
                        + MessageConstants.REGISTER_MSG_IDENTIFIER +
                        MessageConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidLoginMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                MessageConstants.CUSTOM_COMMAND_PREFIX
                        + MessageConstants.LOGIN_MSG_IDENTIFIER +
                        MessageConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidDirectMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                MessageConstants.CUSTOM_COMMAND_PREFIX
                        + MessageConstants.DIRECT_MSG_IDENTIFIER +
                        MessageConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidGroupMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                MessageConstants.CUSTOM_COMMAND_PREFIX
                        + MessageConstants.GROUP_MSG_IDENTIFIER +
                        MessageConstants.CUSTOM_COMMAND_SUFFIX);
    }

    public static boolean isValidDeleteMessageIdentifer(String deleteMessageIdentifier) {
        return deleteMessageIdentifier.equalsIgnoreCase(
                MessageConstants.CUSTOM_COMMAND_PREFIX
                        + MessageConstants.DELETE_MESSAGE_IDENTIFIER +
                        MessageConstants.CUSTOM_COMMAND_SUFFIX);
    }
}
