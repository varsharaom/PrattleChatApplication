package edu.northeastern.ccs.serverim;

import edu.northeastern.ccs.im.constants.MessageConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration for the different types of messages.
 *
 * @author Maria Jump
 */
public enum MessageType {
    /**
     * Message sent by the user attempting to login using a specified username.
     */
    HELLO("HLO"),

    /**
     * Message whose contents is broadcast to all connected users.
     */
    BROADCAST("BCT"),

    /**
     * The register.
     */
    REGISTER(MessageConstants.REGISTER_MSG_IDENTIFIER),

    /**
     * The login.
     */
    LOGIN(MessageConstants.LOGIN_MSG_IDENTIFIER),

    /**
     * The direct.
     */
    DIRECT(MessageConstants.DIRECT_MSG_IDENTIFIER),

    /**
     * The group.
     */
    GROUP(MessageConstants.GROUP_MSG_IDENTIFIER),

    /**
     * The delete.
     */
    DELETE(MessageConstants.DELETE_MESSAGE_IDENTIFIER),

    /**
     * The error.
     */
    ERROR(MessageConstants.ERROR_MSG_IDENTIFIER),

    /**
     * The get info.
     */
    GET_INFO(MessageConstants.GET_INFO_IDENTIFIER),

    /**
     * The action.
     */
    ACTION(MessageConstants.ACTION_MSG_IDENTIFIER),
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

    /**
     * The Constant lookup.
     */
    private static final Map<Integer, MessageType> lookup = new HashMap<>();

    /**
     * The Constant strLookup to lookup type from the enum value.
     */
    private static final Map<String, MessageType> strLookup = new HashMap<>();

    static {
        // Create a reverse lookup hash map
        for (MessageType t : MessageType.values()) {
            lookup.put(t.getMessageTypeValue(), t);
            strLookup.put(t.abbreviation, t);
        }
    }

    /**
     * Gets the message type abbreviation.
     *
     * @param messageType the message type
     * @return the message type
     */
    public static MessageType get(int messageType) {
        return lookup.get(messageType);
    }

    /**
     * Gets the message type value.
     *
     * @return the message type value
     */
    public int getMessageTypeValue() {
        return index;
    }

    /**
     * Gets the message type from the abbreviation.
     *
     * @param abbreviation the abbreviation
     * @return the message type
     */
    public static MessageType get(String abbreviation) {
        return strLookup.get(abbreviation);
    }

    /**
     * Instantiates a new message type.
     *
     * @param abbrev the abbreviation
     */
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
