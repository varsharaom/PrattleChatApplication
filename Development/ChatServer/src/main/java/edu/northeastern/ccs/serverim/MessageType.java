package edu.northeastern.ccs.serverim;

import edu.northeastern.ccs.im.constants.MessageConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for the different types of messages.
 * 
 * @author Maria Jump
 *
 */
public enum  MessageType {
	/**
	 * Message sent by the user attempting to login using a specified username.
	 */
	HELLO("HLO"),

	/** Message whose contents is broadcast to all connected users. */
	BROADCAST("BCT"),

	REGISTER(MessageConstants.REGISTER_MSG_IDENTIFIER),

//	REGISTER_SUCCESS("RGSTR_SCCSS"),
//
//	REGISTER_FAILURE("RGSTR_FLR"),

	LOGIN(MessageConstants.LOGIN_MSG_IDENTIFIER),

//	LOGIN_SUCCESS("LGN_SCCSS"),
//
//	LOGIN_FAILURE("LGN_FLR"),

	DIRECT(MessageConstants.DIRECT_MSG_IDENTIFIER),

	GROUP(MessageConstants.GROUP_MSG_IDENTIFIER),

	DELETE(MessageConstants.DELETE_MESSAGE_IDENTIFIER),

	ERROR(MessageConstants.ERROR_MSG_IDENTIFIER),

	GET_USERS(MessageConstants.GET_USER_IDENTIFIER),

    /**
     * Message sent by the user to start the logging out process and sent by the
     * server once the logout process completes.
     */
    QUIT("BYE");

    /**
     * Store the short name of this message type.
     */
    private String abbreviation;

    /**
     * Integer equivalent for the key.
     */
    private int index;

    private static final Map<Integer, MessageType> lookup = new HashMap<>();
    private static final Map<String, MessageType> strLookup = new HashMap<>();

    static {
        // Create a reverse lookup hash map
        for (MessageType t : MessageType.values()) {
            lookup.put(t.getMessageTypeValue(), t);
            strLookup.put(t.abbreviation, t);
        }
    }

    public static MessageType get(int messageType) {
        return lookup.get(messageType);
    }

    public int getMessageTypeValue() {
        return index;
    }

    public static MessageType get(String abbreviation) {
        return strLookup.get(abbreviation);
    }

    MessageType(String abbrev) {
        abbreviation = abbrev;
    }

    /**
     * Return a representation of this Message as a String.
     *
     * @return Three letter abbreviation for this type of message.
     */
    @Override
    public String toString() {
        return abbreviation;
    }
}
