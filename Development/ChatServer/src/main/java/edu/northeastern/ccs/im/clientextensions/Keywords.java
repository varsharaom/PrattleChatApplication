package edu.northeastern.ccs.im.clientextensions;

public class Keywords {

	private Keywords() {
		
	}
	/** String to represent that the user wants to login */
	protected static final String LOGIN = "$$LGN#";

	/** String to represent that the user wants to register */
	protected static final String REGISTER = "$$RGSTR#";
	
	protected static final String DRCT_MESSAGE = "$$DRCT#";
	
	protected static final String GRP_MESSAGE = "$$GRP#";
	
	protected static final String SAY_HELLO = "hello";
	
	protected static final String CHANGE_OPTION = "CHANGE";
	
	protected static final String LOGIN_MSG = "Enter $$LGN# <username> <password>, if you want to login.\r\n" + 
			"      Enter $$RGSTR# <username> <password>, if you are a new user and want to register.\r\n" + 
			"      Username and password should not contain spaces.";
	
	protected static final String MSG_FORMAT = "Enter $$DRCT# <receiver_name> <message_text> to send direct message.\r\n" + 
			"      or\r\n" + 
			"      Enter $$GRP# <receiver_grp_id> <message_text> to send group message.\r\n";
	
	protected static final String ERROR_MSG = "Enter the command in the specified format";
	

	protected static final String COMMAND_PREFIX ="$$";
	
	protected static final String COMMAND_SUFFIX = "#";
	
	protected static final String DISCONNECT = "Disconnecting..";
}
