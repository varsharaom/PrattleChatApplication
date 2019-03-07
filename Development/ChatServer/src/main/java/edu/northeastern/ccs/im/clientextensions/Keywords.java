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
	
	protected static final String LOGIN_MSG = "\"Enter $$LGN# username password, if you want to login\\n\"\r\n" + 
			"				+ \"Enter $$RGSTR# username password, if you want to register. Username and password\"\r\n" + 
			"				+ \" should not contain spaces\"";
	
	protected static final String MSG_FORMAT = "\"Send direct message or group message? \\nEnter $$GRP# receiver_grp_name message_string\"\r\n" + 
			"				+ \"  to send group message \"\r\n" + 
			"				+ \"or $$DRCT# receiver_name message_string to send direct message.\"";
	
	protected static final String ERROR_MSG = "Enter the command in the specified format";
}
