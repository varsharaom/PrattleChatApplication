package edu.northeastern.ccs.serverim;

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

	REGISTER("RGSTR"),

	REGISTER_SUCCESS("RGSTR_SCCSS"),

	REGISTER_FAILURE("RGSTR_FLR"),

	LOGIN("LGN"),

	LOGIN_SUCCESS("LGN_SCCSS"),

	LOGIN_FAILURE("LGN_FLR"),

	DIRECT("DRCT"),

	GROUP("GRP"),

	ERROR("ERR"),
	
	GET_USERS("GET_USERS"),

	/**
	 * Message sent by the user to start the logging out process and sent by the
	 * server once the logout process completes.
	 */
	QUIT("BYE");

	/** Store the short name of this message type. */
	private String abbreviation;

	/** Integer equivalent for the key. */
	private int index;

	private static final Map<Integer, MessageType> lookup = new HashMap<>();
	
	static {
		// Create a reverse lookup hash map
		for(MessageType t : MessageType.values()) {
			lookup.put(t.getMessageTypeValue(), t);
		}
	}
	
	public static MessageType get(int messageType) {
		return lookup.get(messageType);
	}
	
	public int getMessageTypeValue() {
		return index;
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
