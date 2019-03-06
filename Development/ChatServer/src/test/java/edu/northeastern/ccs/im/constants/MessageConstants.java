package edu.northeastern.ccs.im.constants;


import static edu.northeastern.ccs.im.constants.ClientRunnableConstants.*;

public final class MessageConstants {

	public static final String FIRST_USER = "FIRST_USER";
    public static final String SECOND_USER = "SECOND_USER";
    public static final String SIMPLE_USER = "logUser";   
    public static final String BROADCAST_TEXT_MESSAGE = "broadcastTextMessage";

    public static final String CUSTOM_REGISTER_MESSAGE = CUSTOM_COMMAND_IDENTIFIER +
            REGISTER_MSG_IDENTIFIER +  " userName password";

    public static final String SIMPLE_DRCT_MESSAGE = CUSTOM_COMMAND_IDENTIFIER
            + DIRECT_MSG_IDENTIFIER + " sender receiver hi receiver. This is a private message";


    public static final String NULL_OUTPUT = "--";

    private MessageConstants() {

    }
}
