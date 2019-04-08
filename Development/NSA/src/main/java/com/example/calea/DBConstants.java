package com.example.calea;

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
    /** The Constant EXCEPTION_MESSAGE. */
    public static final String EXCEPTION_MESSAGE = "SQL Exception";

    // JDBC
    /** The Constant CONNECTION_STRING. */
    public static final String CONNECTION_STRING = "jdbc:mysql://prattledb.c22lvtrn2mli.us-east-2.rds.amazonaws.com:3306/prattledb";
    
    /** The Constant DB_USER. */
    public static final String DB_USER = "root";
   
    /** The Constant DB_CRED. */
    public static final String DB_CRED = "prattledb";
    
    /** The message table. */
    public static final String MESSAGE_TABLE = "message";
    
    /** The users table. */
    public static final String USER_TABLE = "users";
    
    // MESSAGE
    /** The id field in the message table. */
    public static final String MESSAGE_ID = "id";

    //Alias field for the message id constant
    public static final String MESSAGE_ID_ALIAS = "MSG_ID";
    
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
    
    /** The Constant USER_INVISIBLE. */
    public static final String USER_INVISIBLE = "invisible";
    
    /** The Constant USER_INVISIBLE_TRUE. */
    public static final Integer USER_INVISIBLE_TRUE = 1;
    
    /** The Constant USER_INVISIBLE_FALSE. */
    public static final Integer USER_INVISIBLE_FALSE = 0;
}