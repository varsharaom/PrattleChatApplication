package edu.northeastern.ccs.im.persistence;

/**
 * The Class DBConstants.
 */
public class DBConstants {

    /**
     * Instantiates a new DB constants.
     */
    private DBConstants() {

    }

    // LOG MESSAGES
    /** The error message for SQL exceptions. */
    public static final String EXCEPTION_MESSAGE = "SQL Exception";

    // JDBC
    /** The connection string for the application DB. */
    public static final String CONNECTION_STRING = "jdbc:mysql://prattledb.c22lvtrn2mli.us-east-2.rds.amazonaws.com:3306/prattledb";
    
    /** The user for the DB connection string. */
    public static final String DB_USER = "root";
    
    
    public static final String DB_CRED = "prattledb";


    /** The users table. */
    public static final String USER_TABLE = "users";
    
    /** The groups table. */
    public static final String GROUP_TABLE = "groups";
    
    /** The circles table. */
    public static final String CIRCLES_TABLE = "circles";
    
    /** The message table. */
    public static final String MESSAGE_TABLE = "message";
    
    /** The group_info table. */
    public static final String GROUP_INFO_TABLE = "group_info";

    //USER
    /** The id field in User table. */
    public static final String USER_ID = "id";
    
    /** The name field in User table. */
    public static final String USER_USERNAME = "name";
    
    /** The password field in User table. */
    public static final String USER_PASS = "password";
    
    /** The nickname field in User table. */
    public static final String USER_NICKNAME = "nickname";
    
    /** The last seen field in User table. */
    public static final String USER_LAST_SEEN = "last_seen";
    public static final String USER_INVISIBLE = "invisible";

    // GROUP
    /** The id field in groups table. */
    public static final String GROUP_ID = "id";
    
    /** The name field in groups table. */
    public static final String GROUP_NAME = "name";

    // GROUP_INFO
    /** The id field in group_info table. */
    public static final String GROUP_INFO_ID = "id";
    
    /** The group_id field in group_info table. */
    public static final String GROUP_INFO_GROUP_ID = "group_id";
    
    /** The uid field in group_info table. */
    public static final String GROUP_INFO_USER_ID = "uid";
    
    /** The role field in group_info table. */
    public static final String GROUP_INFO_USER_ROLE = "role";
    public static final int GROUP_INFO_ADMIN_ROLE_ID = 2;

    // CIRCLES
    /** The id field in the circles table. */
    public static final String CIRCLE_ID = "id";
    
    /** The first user id field in the circles table. */
    public static final String CIRCLE_USER_1_ID = "user_one";
    
    /** The second user id field in the circles table. */
    public static final String CIRCLE_USER_2_ID = "user_two";

    // MESSAGE
    /** The id field in the message table. */
    public static final String MESSAGE_ID = "id";
    
    /** The sender id field in the message table. */
    public static final String MESSAGE_SENDER_ID = "sender_id";
    
    /** The receiver id field in the message table. */
    public static final String MESSAGE_RECEIVER_ID = "receiver_id";
    
    /** The type field in the message table. */
    public static final String MESSAGE_TYPE = "type";
    
    /** The body field in the message table. */
    public static final String MESSAGE_BODY = "body";
    
    /** The time sent field in the message table. */
    public static final String MESSAGE_TIME = "time_sent";
    
    /** The isDeleted bit field in the message table. */
    public static final String IS_DELETED = "isDeleted";

    /** The constant for deleted state of a message. */
    public static final int IS_DELETED_TRUE = 1;

    /** The DateTime format used in the message table. */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
}
