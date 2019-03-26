package edu.northeastern.ccs.im.constants;

public class QueryConstants {

	private QueryConstants() {
		
	}
	
	// User
	public static final String USERNAME = "TEST";
	public static final String INVALID_USERNAME = "TEST_INVALID";
	public static final String PASS = "123";
	public static final String NICKNAME = "TEST";
	
	// Message
	public static final String SENDER_USERNAME = "S_TEST";
	public static final String RECEIVER_USERNAME = "R_TEST";
	public static final long SENDER_ID = 1;
	public static final long RECEIVER_ID = 2;
	public static final String MESSAGE_TEXT = "text";
	public static final String MESSAGE_SECOND_TEXT = "second text";
	public static final long MESSAGE_ID = 1;
	
	// Groups
	public static final String GROUP_NAME = "GROUP";
	
	// Queries
	public static final String TEARDOWN_DELETE = "DELETE FROM %s where %s = %s;";
}
