package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.serverim.Group;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

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

public class QueryHandlerMySQLImpl implements IQueryHandler {

    final Logger logger = Logger.getGlobal();
    private static final String SQL_EXCEPTION_MSG = "SQL Exception";
    private Connection connection;

    QueryHandlerMySQLImpl() {
        connection = DBHandler.getConnection();
    }

    //-----------------User Queries-------------------
    public User createUser(String userName, String pass, String nickName) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
        String query = String.format("INSERT into %s (%s,%s,%s,%s) VALUES('%s','%s','%s','%s');", DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, DBConstants.USER_PASS, DBConstants.USER_NICKNAME, DBConstants.USER_LAST_SEEN,
                userName, pass, nickName, format.format(date));
        long id = doInsertQuery(query);
        User user = null;
        if (id != -1) {
            user = new User(id, userName, nickName, date.getTime());
        }
        return user;
    }

    public int updateUserLastLogin(long userID) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
        String query = String.format("UPDATE %s set %s = '%s' WHERE %s = %d;",
                DBConstants.USER_TABLE, DBConstants.USER_LAST_SEEN, format.format(date),
                DBConstants.USER_ID, userID);
        return doUpdateQuery(query);
    }

    @Override
    public long validateLogin(String username, String password) {
        String query = String.format("SELECT * from %s WHERE %s ='%s' and %s = '%s'",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, username, DBConstants.USER_PASS, password);
        long res = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if(rs.next()) {
            		res = rs.getLong(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return res;
    }

    //-----------------Message Queries-------------------
    public long storeMessage(String senderName, String receiverName, MessageType type, String msgText) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
        long senderID = getUserID(senderName);
        long receiverID = getUserID(receiverName);
        String query = String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(%d,%d,'%s','%s','%s');",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY, DBConstants.MESSAGE_TIME,
                senderID, receiverID, type, msgText, format.format(date));
        return doInsertQuery(query);
    }

    public Message getMessage(long messageID) {
        String query = String.format("Select %s,%s,%s,%s,%s,%s from %s where %s=%s",
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY, DBConstants.MESSAGE_ID, DBConstants.IS_DELETED,
                DBConstants.MESSAGE_TABLE,
                DBConstants.MESSAGE_ID, messageID
        );
        Message message = null;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                message = new Message(rs.getLong(5), MessageType.get(rs.getString(3)), getUserName(rs.getLong(1)),
                        getUserName(rs.getLong(2)), rs.getString(4), rs.getInt(6));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return message;
    }

    public void deleteMessage(long messageID) {
        String query = String.format("Update %s set %s=%s where %s=%s",
                DBConstants.MESSAGE_TABLE, DBConstants.IS_DELETED, DBConstants.IS_DELETED_TRUE,
                DBConstants.MESSAGE_ID, messageID
        );
        doUpdateQuery(query);
    }

    public List<Message> getMessagesSinceLastLogin(long userID) {
        //To-Do : join group id to get all messages where the user is a part of.
        String query = String.format(
                "SELECT %s, %s, %s, %s from %s inner join %s on %s.%s = %s.%s WHERE %s >= %s AND %s = %s;",
                //select columns
                DBConstants.MESSAGE_BODY, DBConstants.USER_LAST_SEEN,
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                //join tables
                DBConstants.MESSAGE_TABLE, DBConstants.USER_TABLE,
                //join column one
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID,
                ////join column two
                DBConstants.USER_TABLE, DBConstants.USER_ID,
                //Filters
                DBConstants.MESSAGE_TIME, DBConstants.USER_LAST_SEEN, DBConstants.MESSAGE_RECEIVER_ID, userID);
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Message m = Message.makeDirectMessage(getUserName(rs.getLong(3)), getUserName(rs.getLong(4)), rs.getString(1));
                messages.add(m);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return messages;
    }


    public boolean checkUserNameExists(String name) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);

        boolean isNameFound = false;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            isNameFound = rs.next();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return isNameFound;
    }

    public boolean checkGroupNameExists(String groupName) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName);

        boolean isNameFound = false;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            isNameFound = rs.next();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return isNameFound;
    }

    @Override
    public List<Group> getAllGroups() {
        return null;
    }

    @Override
    public List<Group> getMyGroups(String senderName) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        String query = String.format("SELECT %s, %s, %s FROM %s;",
                DBConstants.USER_ID, DBConstants.USER_USERNAME,
                DBConstants.USER_NICKNAME, DBConstants.USER_TABLE);

        List<User> userList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            Date date = new Date(System.currentTimeMillis());
            while (rs.next()) {
                User user = new User(rs.getLong(1),
                        rs.getString(2), rs.getString(3), date.getTime());
                userList.add(user);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return userList;
    }

    @Override
    public List<User> getMyUsers(String senderName) {
        return null;
    }


    public long getUserID(String userName) {
        String query = String.format("SELECT %s FROM %s where %s=\"%s\";",
                DBConstants.USER_ID, DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, userName
        );
        long id = -1l;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return id;
    }

    public String getUserName(long userID) {
        String query = String.format("SELECT %s FROM %s where %s=%s;",
                DBConstants.USER_USERNAME, DBConstants.USER_TABLE,
                DBConstants.USER_ID, userID
        );
        String name = "";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return name;
    }
    
    public List<Message> getMessagesSentByUser(long id, MessageType type) {
    		String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = '%s';",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, id, DBConstants.MESSAGE_TYPE, type);

        List<Message> messageList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Message msg = new Message(rs.getLong(1), MessageType.get(rs.getString(4)), getUserName(rs.getInt(2)), 
                		getUserName(rs.getInt(3)), rs.getString(5), rs.getInt(7));
                messageList.add(msg);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return messageList;
    }
    
    public List<Message> getMessagesSentToUser(long id, MessageType type) {
    		String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = '%s';",
    				DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID, id, DBConstants.MESSAGE_TYPE, type);

        List<Message> messageList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Message msg = new Message(rs.getLong(1), MessageType.get(rs.getString(4)), getUserName(rs.getInt(2)), 
                		getUserName(rs.getInt(3)), rs.getString(5), rs.getInt(7));
                messageList.add(msg);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return messageList;
    }
    
    public List<Message> getMessagesFromUserChat(long senderId, long receiverId) {
		String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d AND %s = '%s';",
				DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID, receiverId,
										  DBConstants.MESSAGE_SENDER_ID, senderId,
										  DBConstants.MESSAGE_TYPE, MessageType.DIRECT);

	    List<Message> messageList = new ArrayList<>();
	    try {
	        PreparedStatement statement = connection.prepareStatement(query);
	        ResultSet rs = statement.executeQuery();
	
	        while (rs.next()) {
	            Message msg = new Message(rs.getLong(1), MessageType.get(rs.getString(4)), getUserName(rs.getInt(2)), 
	            		getUserName(rs.getInt(3)), rs.getString(5), rs.getInt(7));
	            messageList.add(msg);
	        }
	        rs.close();
	        statement.close();
	    } catch (SQLException e) {
	        logger.log(Level.INFO, SQL_EXCEPTION_MSG);
	    }
	    return messageList;
	}
    
    //-----------------Group Queries----------------------------------
    
    public List<String> getGroupMembers(String name) {
    		String query = String.format("SELECT gi.%s\n" + 
    				"FROM %s as gi, %s as g\n" + 
    				"WHERE g.%s = '%s' AND gi.%s = g.%s;",
    				DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_TABLE,
    				DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, name,
    				DBConstants.GROUP_INFO_GROUP_ID, DBConstants.GROUP_ID);
    		
    		List<String> memberList = new ArrayList<>();
    		try {
    			PreparedStatement statement = connection.prepareStatement(query);
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                		memberList.add(getUserName(rs.getInt(1)));
                }
                rs.close();
                statement.close();
    		} catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
    		return memberList;
    }
    
    public List<String> getGroupModerators(String name) {
    		// TODO: Replace constant 2 with enum value for group role
		String query = String.format("SELECT gi.%s\n" + 
				"FROM %s as gi, %s as g\n" + 
				"WHERE g.%s = '%s' AND gi.%s = g.%s AND gi.%s = %d;",
				DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_TABLE,
				DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, name,
				DBConstants.GROUP_INFO_GROUP_ID, DBConstants.GROUP_ID,
				DBConstants.GROUP_INFO_USER_ROLE, 2);
		
		List<String> moderatorList = new ArrayList<>();
		try {
			PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
            		moderatorList.add(getUserName(rs.getInt(1)));
            }
            rs.close();
            statement.close();
		} catch (SQLException e) {
			logger.log(Level.INFO, SQL_EXCEPTION_MSG);
		}
		return moderatorList;
    }
    
    public long createGroup(String name) {
		String query = String.format("INSERT INTO %s (%s)\n" + 
				"VALUES ('%s');",
				DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, name);
		
		return doInsertQuery(query);
    }
    
    public long deleteGroup(String name) {
		String query = String.format("DELETE FROM %s\n" + 
				"WHERE %s = '%s' and %s > 0;",
				DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, name, DBConstants.GROUP_ID);
		return doUpdateQuery(query);
    }
    
    public long addGroupMember(String userName, String groupName, int role) {
    		long userId = getUserID(userName);
    		long groupId = getGroupID(groupName);
    		
		String query = String.format("INSERT INTO %s (%s, %s, %s)\n" + 
				"VALUES (%d, %d, %d);",
				DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
				DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_USER_ROLE,
				groupId, userId, role); 
		
		return doInsertQuery(query);
    }
    
    public long removeGroupMember(String userName, String groupName) {
		long userId = getUserID(userName);
		long groupId = getGroupID(groupName);
		
		String query = String.format("DELETE FROM %s\n" + 
				"WHERE %s = %d AND %s = %d AND id > 0;",
				DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID, groupId,
				DBConstants.GROUP_INFO_USER_ID, userId, DBConstants.GROUP_ID);
		
		return doUpdateQuery(query);
    }
    
    public void changeMemberRole(long userId, long groupId, int role) {
        String query = String.format("UPDATE %s\n" + 
        				"SET %s = %d\n" + 
        				"WHERE %s = %d AND %s = %d AND %s > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_USER_ROLE, role,
                DBConstants.GROUP_INFO_USER_ID, userId, DBConstants.GROUP_INFO_GROUP_ID, groupId, DBConstants.GROUP_ID);

        doUpdateQuery(query);
    }
    
    public long getGroupID(String groupName) {
        String query = String.format("SELECT %s FROM %s where %s='%s';",
                DBConstants.GROUP_ID, DBConstants.GROUP_TABLE,
                DBConstants.GROUP_NAME, groupName
        );
        long id = -1l;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                id = rs.getLong(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return id;
    }

    public String getGroupName(long groupID) {
        String query = String.format("SELECT %s FROM %s where %s=%d;",
                DBConstants.GROUP_NAME, DBConstants.GROUP_TABLE,
                DBConstants.GROUP_ID, groupID
        );
        String name = "";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return name;
    }
    
    //-----------------DB Insert/Update Queries-------------------
    
    protected long doInsertQuery(String query) {
        PreparedStatement statement = null;
        long key = -1;
        try {
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                key = generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return key;
    }

    public int doUpdateQuery(String query) {
        PreparedStatement statement = null;
        int updateCode = 0;
        try {
            statement = connection.prepareStatement(query);
            updateCode = statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return updateCode;
    }

}
