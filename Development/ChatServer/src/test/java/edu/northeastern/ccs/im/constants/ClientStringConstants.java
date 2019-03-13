package edu.northeastern.ccs.im.constants;

public class ClientStringConstants {
	
	private ClientStringConstants() {
		
	}

		
	public static final String LOGIN_MSG = "Enter $$LGN# <username> <password>, if you want to login.\r\n" + 
			"      Enter $$RGSTR# <username> <password>, if you are a new user and want to register.\r\n" + 
			"      Username and password should not contain spaces.";
	
	public static final String MSG_FORMAT = "Enter $$DRCT# <receiver_name> <message_text> to send direct message.\r\n" + 
			"      or\r\n" + 
			"      Enter $$GRP# <receiver_grp_id> <message_text> to send group message.\r\n";
	
	public static final String ERROR_MSG = "Enter the command in the specified format";
	

	 public static final int TEST_PORT = 4560;
	 public static final String TEST_USER ="testuser";
	 
	 public static final String HOST_NAME = "localhost";
	 
	 public static final String CORRECT_LOGIN_MSG = "$$LGN# myusername password";
	 
	 public static final String CORRECT_REG_MSG = "$$RGSTR# myusername password";
	 
	 public static final String WRONG_LOGIN_MSG1 = "$$LGN myusername password";
	 
	 public static final String WRONG_LOGIN_MSG2 = "Login";
	 
	 public static final String WRONG_LOGIN_MSG3 = "$$LGN# myusername password hello";
	 
	 public static final String WRONG_RGSTR_MSG1 = "Register";
	 
	 public static final String WRONG_RGSTR_MSG2 = "$$RGSTR myusername password";
	 
	 public static final String WRONG_RGSTR_MSG3 = "$$RGSTR myusername password hello";
	 
	 public static final String WRONG_RGSTR_MSG4 = "$$RGSTR# myusername password hello";
	 
	 public static final String CORRECT_GRP_MSG = "$$GRP# receivername message";
	 
	 public static final String CORRECT_DRCT_MSG1 = "$$DRCT# receivername direct message";
	 
	 public static final String WRONG_GRP_MSG = "$$GRP receivername message";
	 
	 public static final String WRONG_GRP_MSG2 = "$$$GRP# receivername ";
	 
	 public static final String WRONG_GRP_MSG3 = "Groupmessage ";
	 
	 public static final String WRONG_DRCT_MSG1 = "$$$DRCT# receivername ";
	 
	 public static final String WRONG_DRCT_MSG2 = "DirectMsg receivername ";
	 
	 public static final String WRONG_DRCT_MSG3 = "DirectMsg  ";
	 
	 public static final String WRONG_DRCT_MSG4 = "$$DRCT receivername message";
	 
	 public static final String RANDOM_MSG1 = "Hi group";
	 
	 public static final String RANDOM_MSG2 = "Hi user2";
	 
	 public static final String CHANGE_MSG = "CHANGE";
	 
	 public static final String BYE_MSG = "/quit";
	 
	 public static final String DISCONNECT = "Disconnecting..";
	 
	 
	 
	 

	 
	 
	 
}
