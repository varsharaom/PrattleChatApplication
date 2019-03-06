package edu.northeastern.ccs.im.constants;

public final class ClientRunnableConstants {

    /** The delimiter to parse special messages. This should be the same in the client side */
    public static final String DELIMITER = "";

    public static final String CUSTOM_COMMAND_IDENTIFIER = "$$";

    public static final String DIRECT_MSG_IDENTIFIER = "DRCT#";
    public static final String GROUP_MSG_IDENTIFIER = "GRP#";
    public static final String LOGIN_MSG_IDENTIFIER = "LGN#";
    public static final String REGISTER_MSG_IDENTIFIER = "RGSTR#";

    public static final String REGISTER_SUCCESS_MSG = "You have successfully registered as a " +
            "new user..";
    public static final String REGISTER_FAILURE_MSG = "Invalid credentials.." +
            "Please try with new user information.";
    public static final String LOGIN_SUCCESS_MSG = "You have successfully logged in..";
    public static final String LOGIN_FAILURE_MSG = "Your credentials mismatch...";

    private ClientRunnableConstants() {

    }
}
