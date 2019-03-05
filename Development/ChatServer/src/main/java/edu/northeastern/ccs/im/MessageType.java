package edu.northeastern.ccs.im;

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

	REGISTER("BCT"),

	REGISTER_SUCCESS("BCT"),

	REGISTER_FAILURE("BCT"),

	LOGIN("BCT"),

	LOGIN_SUCCESS("BCT"),

	LOGIN_FAILURE("BCT"),

	PRIVATE("BCT"),

	GROUP("BCT"),

	/**
	 * Message sent by the user to start the logging out process and sent by the
	 * server once the logout process completes.
	 */
	QUIT("BYE");




	/** Store the short name of this message type. */
	private String abbreviation;

	/**
	 * Define the message type and specify its short name.
	 * 
	 * @param abbrev Short name of this message type, as a String.
	 */
	private MessageType(String abbrev) {
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
