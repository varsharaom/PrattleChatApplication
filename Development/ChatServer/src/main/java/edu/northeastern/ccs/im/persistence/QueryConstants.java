package edu.northeastern.ccs.im.persistence;

public class QueryConstants {
	
	private QueryConstants() {
		
	}

    public static final String USER_TABLE = "users";
    public static final String GROUP_TABLE = "groups";
    public static final String CIRCLES_TABLE = "circles";
    public static final String MESSAGE_TABLE = "message";
    public static final String GROUP_INFO_TABLE = "group_info";

    //USER
    public static final String USER_ID = "id";
    public static final String USER_USERNAME = "name";
    public static final String USER_NICKNAME = "nickname";
    public static final String USER_LAST_SEEN = "last_seen";

    //GROUP
    public static final String GROUP_ID = "id";
    public static final String GROUP_NAME = "name";

    //GROUP_INFO
    public static final String GROUP_INFO_GROUP_ID = "group_id";
    public static final String GROUP_INFO_USER_ID = "uid";
    public static final String GROUP_INFO_USER_ROLE = "role";

    //CIRCLES
    public static final String CIRCLE_ID = "id";
    public static final String CIRCLE_USER_1_ID = "user_one";
    public static final String CIRCLE_USER_2_ID = "user_two";

    //MESSAGE
    public static final String MESSAGE_ID = "id";
    public static final String MESSAGE_SENDER_ID = "sender_id";
    public static final String MESSAGE_RECEIVER_ID = "receiver_id";
    public static final String MESSAGE_TYPE = "type";
    public static final String MESSAGE_BODY = "body";
    public static final String MESSAGE_TIME = "time_sent";
}
