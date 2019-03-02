package edu.northeastern.ccs.im.persistence;

import java.util.ArrayList;
import java.util.List;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.User;

public class MessageQueryHandler extends QueryHandler {

    public void storeMessage(long senderID, long receiverID, MessageType type, String msgText) {
		String query = "Insert into " + QueryConstants.MESSAGE_TABLE
            + " values(" + senderID + "," + receiverID + "," + type + "," + msgText
            + "," + System.currentTimeMillis() + ")";
	    doInsertQuery(query);
	}

	public List<Message> getMessagesSinceLastLogin(User user) {
			String query = "Select * from " + QueryConstants.MESSAGE_TABLE + " where "
			+ QueryConstants.MESSAGE_TIME + " > " + user.getLastSeen() + ";";
			doSelectQuery(query);
			return new ArrayList<>();
	}

}
