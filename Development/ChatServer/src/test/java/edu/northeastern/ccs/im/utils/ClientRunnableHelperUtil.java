package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.constants.ClientRunnableConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;

public class ClientRunnableHelperUtil {

    public static boolean isValidRegisterMessageIdentifer(String registerMessageIdentifier) {
        return registerMessageIdentifier.equalsIgnoreCase(
                ClientRunnableConstants.CUSTOM_COMMAND_PREFIX
                        + ClientRunnableConstants.REGISTER_MSG_IDENTIFIER +
                        ClientRunnableConstants.CUSTOM_COMMAND_SUFFIX);
    }
}
