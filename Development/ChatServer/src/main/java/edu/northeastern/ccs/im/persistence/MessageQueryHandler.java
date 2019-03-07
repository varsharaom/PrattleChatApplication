package edu.northeastern.ccs.im.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.User;

public class MessageQueryHandler extends QueryHandler {
	
    public void storeMessage(long senderID, long receiverID, MessageType type, String msgText) {
    		String query = String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(%d,%d,%s,%s,%d);",
    				DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
    				DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY,  DBConstants.MESSAGE_TIME,
    				senderID, receiverID, type+"", msgText, System.currentTimeMillis());
	    doInsertQuery(query);
	}

	public List<Message> getMessagesSinceLastLogin(User user) {
		String query = String.format("SELECT * from %s WHERE %d > %d;",
				DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_TIME, user.getLastSeen());
		ResultSet rs = doSelectQuery(query);
		List<Message> messages = new ArrayList<>();
		try {
			while(rs.next()) {
				Message m = Message.makeBroadcastMessage("placeholder", rs.getString("body"));
				messages.add(m);
			}
		} catch (SQLException e) {
			logger.log(Level.INFO, "SQL Exception");
		}
		return messages;
	}

}
