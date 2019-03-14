package edu.northeastern.ccs.im.constants;


import static edu.northeastern.ccs.im.constants.ClientRunnableConstants.*;

public final class MessageConstants {

	public static final String FIRST_USER = "FIRST_USER";
    public static final String SECOND_USER = "SECOND_USER";
    public static final String SIMPLE_USER = "logUser";   
    public static final String BROADCAST_TEXT_MESSAGE = "broadcastTextMessage";

    public static final String CUSTOM_REGISTER_MESSAGE = CUSTOM_COMMAND_PREFIX +
            REGISTER_MSG_IDENTIFIER +  CUSTOM_COMMAND_SUFFIX + " logUser password";

    public static final String SIMPLE_DRCT_MESSAGE = CUSTOM_COMMAND_PREFIX
            + DIRECT_MSG_IDENTIFIER + CUSTOM_COMMAND_SUFFIX
            + " sender receiver hi receiver. This is a private message";


    public static final String NULL_OUTPUT = "--";

    private MessageConstants() {

    }
}
