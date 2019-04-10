package com.example.calea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The Class MessageRepository.
 */
public class MessageRepository {

	/** The logger instance used in query methods. */
	static final Logger logger = Logger.getGlobal();
	
	/** The connection. */
	Connection connection;
	
	/**
     * The SQL exception error message.
     */
    private static final String SQL_EXCEPTION_MSG = "SQL Exception";
	
	/**
	 * Instantiates a new message repository.
	 */
	public MessageRepository() {
		connection = DBHandler.getConnection();
	}
	
	/**
	 * Gets the messages sent by user.
	 *
	 * @param request the request
	 * @param id the id
	 * @return the messages sent by user
	 */
	public List<Message> getMessagesSentByUser(RequestMessage request, long id) {
		Date from = request.fromTimestamp;
		Date to = request.toTimestamp;
		
		SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
		
		String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s > '%s' AND %s < '%s';",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, id, DBConstants.MESSAGE_TIME, format.format(from),
                DBConstants.MESSAGE_TIME, format.format(to));

        List<Message> messageList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Message msg = new Message(rs.getLong(DBConstants.MESSAGE_ID), getUserName(rs.getLong(DBConstants.MESSAGE_SENDER_ID)),
                        getUserName(rs.getInt(DBConstants.MESSAGE_RECEIVER_ID)), rs.getString(DBConstants.MESSAGE_BODY),
                        rs.getDate(DBConstants.MESSAGE_TIME));
                messageList.add(msg);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }
        return messageList;
	}
	
	/**
	 * Gets the messages received by user.
	 *
	 * @param request the request
	 * @param id the id
	 * @return the messages received by user
	 */
	public List<Message> getMessagesReceivedByUser(RequestMessage request, long id) {
		Date from = request.fromTimestamp;
		Date to = request.toTimestamp;
		
		SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
		
		String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s > '%s' AND %s < '%s';",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID, id, DBConstants.MESSAGE_TIME, format.format(from),
                DBConstants.MESSAGE_TIME, format.format(to));

        List<Message> messageList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Message msg = new Message(rs.getLong(DBConstants.MESSAGE_ID), getUserName(rs.getLong(DBConstants.MESSAGE_SENDER_ID)),
                        getUserName(rs.getInt(DBConstants.MESSAGE_RECEIVER_ID)), rs.getString(DBConstants.MESSAGE_BODY),
                        rs.getDate(DBConstants.MESSAGE_TIME));
                messageList.add(msg);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }
        return messageList;
	}
	
    /**
     * Gets the user name.
     *
     * @param userID the user ID
     * @return the user name
     */
    public String getUserName(long userID) {
        String query = String.format("SELECT %s FROM %s where %s=%s;",
                DBConstants.USER_USERNAME, DBConstants.USER_TABLE,
                DBConstants.USER_ID, userID
        );
        return nameHelper(query, DBConstants.USER_USERNAME);
    }
    
    /**
     * Name helper.
     *
     * @param query the query
     * @param selectColumn the select column
     * @return the string
     */
    //helper method that returns name of some entity
    private String nameHelper(String query, String selectColumn) {
        String name = "";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString(selectColumn);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }
        return name;
    }

}
