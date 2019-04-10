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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Class QueryHandlerMySQLImpl.
 */
public class QueryHandlerMySQLImpl implements IQueryHandler {

    /**
     * The logger object.
     */
    final Logger logger = Logger.getGlobal();

    /**
     * The SQL exception error message.
     */
    private static final String SQL_EXCEPTION_MSG = "SQL Exception";

    /**
     * The JDBC connection.
     */
    private Connection connection;

    /**
     * Instantiates a new query handler for this MySQL implementation.
     */
    QueryHandlerMySQLImpl() {
        connection = DBHandler.getConnection();
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#createUser(java.lang.String, java.lang.String, java.lang.String)
     */
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
            user = new User(id, userName, nickName, date.getTime(), 0);
        }
        return user;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#updateUserLastLogin(long)
     */
    public int updateUserLastLogin(long userID) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
        String query = String.format("UPDATE %s set %s = '%s' WHERE %s = %d;",
                DBConstants.USER_TABLE, DBConstants.USER_LAST_SEEN, format.format(date),
                DBConstants.USER_ID, userID);
        return doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#validateLogin(java.lang.String, java.lang.String)
     */
    @Override
    public long validateLogin(String username, String password) {
        String query = String.format("SELECT %s from %s WHERE %s ='%s' and %s = '%s'",
                DBConstants.USER_ID, DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, username, DBConstants.USER_PASS, password);
        return idHelper(query, DBConstants.USER_ID);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#updateUserVisibility(java.lang.String, java.lang.Boolean)
     */
    @Override
    public void updateUserVisibility(String userName, Boolean makeInVisible) {
        String query = String.format("UPDATE %s SET %s=%d WHERE %s='%s';",
                DBConstants.USER_TABLE, DBConstants.USER_INVISIBLE,
                makeInVisible ? DBConstants.USER_INVISIBLE_TRUE : DBConstants.USER_INVISIBLE_FALSE,
                DBConstants.USER_USERNAME, userName);
        doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#storeMessage(java.lang.String, java.lang.String, edu.northeastern.ccs.serverim.MessageType, java.lang.String)
     */
    //-----------------Message Queries-------------------
    public long storeMessage(String senderName, String receiverName, MessageType type, String msgText) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
        long senderID = getUserID(senderName);
        long receiverID = type.equals(MessageType.GROUP) ? getGroupID(receiverName) : getUserID(receiverName);
        String query = String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(%d,%d,'%s','%s','%s');",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY, DBConstants.MESSAGE_TIME,
                senderID, receiverID, type, msgText, format.format(date));
        return doInsertQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMessage(long)
     */
    public Message getMessage(long messageID) {
        String query = String.format("Select %s,%s,%s,%s,%s,%s from %s where %s=%s;",
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY, DBConstants.MESSAGE_ID, DBConstants.IS_DELETED,
                DBConstants.MESSAGE_TABLE,
                DBConstants.MESSAGE_ID, messageID
        );
        Message message = null;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                message = new Message(rs.getLong(DBConstants.MESSAGE_ID),
                        MessageType.get(rs.getString(DBConstants.MESSAGE_TYPE)),
                        getUserName(rs.getLong(DBConstants.MESSAGE_SENDER_ID)),
                        getUserName(rs.getLong(DBConstants.MESSAGE_RECEIVER_ID)),
                        rs.getString(DBConstants.MESSAGE_BODY), rs.getInt(DBConstants.IS_DELETED));
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return message;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#deleteMessage(long)
     */
    public void deleteMessage(long messageID) {
        String query = String.format("Update %s set %s=%s where %s=%s;",
                DBConstants.MESSAGE_TABLE, DBConstants.IS_DELETED, DBConstants.IS_DELETED_TRUE,
                DBConstants.MESSAGE_ID, messageID
        );
        doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMessagesSinceLastLogin(long)
     */
    public List<Message> getMessagesSinceLastLogin(long userID) {
        List<Message> messages = new ArrayList<>();
        messages.addAll(getPrivateMessagesSinceLogin(userID));
        messages.addAll(getGroupMessagesSinceLogin(userID));
        return messages;
    }


    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#addUserToCircle(java.lang.String, java.lang.String)
     */
    public long addUserToCircle(String senderName, String receiverName) {
        long senderID = getUserID(senderName);
        long receiverID = getUserID(receiverName);

        String query = String.format("INSERT INTO %s (%s, %s) VALUES(%d,%d);",
                DBConstants.CIRCLES_TABLE, DBConstants.CIRCLE_USER_1_ID,
                DBConstants.CIRCLE_USER_2_ID, senderID, receiverID);
        return doInsertQuery(query);
    }

    @Override
    public Map<String, List<String>> trackMessage(long messageId) {
        return new HashMap<>();
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#checkUserNameExists(java.lang.String)
     */
    public boolean checkUserNameExists(String name) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
        return nameAvailabilityHelper(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#checkGroupNameExists(java.lang.String)
     */
    public boolean checkGroupNameExists(String groupName) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName);
        return nameAvailabilityHelper(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getAllGroups()
     */
    @Override
    public List<Group> getAllGroups() {
        String query = String.format("Select %s, %s from %s WHERE %s = %d;",
                //Select columns
                DBConstants.GROUP_ID, DBConstants.GROUP_NAME,
                //table
                DBConstants.GROUP_TABLE, DBConstants.GROUP_IS_PRIVATE, DBConstants.GROUP_PUBLIC_CODE);

        return getGroupsHelper(query, DBConstants.GROUP_ID, DBConstants.GROUP_NAME);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMyGroups(java.lang.String)
     */
    @Override
    public List<Group> getMyGroups(String senderName) {
        String query = String.format("Select %s.%s, %s.%s from %s "
                        + " inner join %s on %s.%s = %s.%s "
                        + "  inner join %s on %s.%s = %s.%s "
                        + "where %s.%s = '%s';",
                //Select columns
                DBConstants.GROUP_TABLE, DBConstants.GROUP_ID,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME,
                //table
                DBConstants.GROUP_TABLE,
                //join one
                DBConstants.GROUP_INFO_TABLE,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_ID,
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                //join two
                DBConstants.USER_TABLE,
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_USER_ID,
                DBConstants.USER_TABLE, DBConstants.USER_ID,
                //where clause
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, senderName
        );

        return getGroupsHelper(query, DBConstants.GROUP_TABLE + "." + DBConstants.GROUP_ID,
                DBConstants.GROUP_TABLE + "." + DBConstants.GROUP_NAME);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getAllUsers()
     */
    @Override
    public List<User> getAllUsers() {
        String query = String.format("SELECT %s, %s, %s FROM %s where %s = %d;",
                DBConstants.USER_ID, DBConstants.USER_USERNAME,
                DBConstants.USER_NICKNAME, DBConstants.USER_TABLE,
                DBConstants.USER_INVISIBLE, DBConstants.USER_INVISIBLE_FALSE);

        List<User> userList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();

            Date date = new Date(System.currentTimeMillis());
            while (rs.next()) {
                User user = new User(rs.getLong(DBConstants.USER_ID),
                        rs.getString(DBConstants.USER_USERNAME), rs.getString(DBConstants.USER_NICKNAME), date.getTime(), 0);
                userList.add(user);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return userList;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMyUsers(java.lang.String)
     */
    @Override
    public List<User> getMyUsers(String senderName) {
        long senderID = getUserID(senderName);
        String query = String.format("Select %s, %s from %s where %s = %s or %s = %s",
                DBConstants.CIRCLE_USER_1_ID, DBConstants.CIRCLE_USER_2_ID,
                DBConstants.CIRCLES_TABLE,
                DBConstants.CIRCLE_USER_1_ID, senderID,
                DBConstants.CIRCLE_USER_2_ID, senderID);
        Set<Long> circleIDs = new HashSet<>();

        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                long idToAdd = rs.getLong(DBConstants.CIRCLE_USER_1_ID) == senderID ?
                        rs.getLong(DBConstants.CIRCLE_USER_2_ID) : rs.getLong(DBConstants.CIRCLE_USER_1_ID);
                circleIDs.add(idToAdd);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        List<User> circleList = new ArrayList<>();

        for (long userId : circleIDs) {
            query = String.format("SELECT u.* FROM %s as u WHERE %s = %d;",
                    DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            try {
                statement = connection.prepareStatement(query);
                rs = statement.executeQuery();

                Date date = new Date(System.currentTimeMillis());
                while (rs.next()) {
                    User user = new User(rs.getLong(DBConstants.USER_ID),
                            rs.getString(DBConstants.USER_USERNAME), rs.getString(DBConstants.USER_NICKNAME), date.getTime(), rs.getInt(DBConstants.USER_INVISIBLE));
                    circleList.add(user);
                }
            } catch (SQLException e) {
                logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
            } finally {
		    		try {
					closeDBResources(rs, statement);
				} catch (NullPointerException | SQLException e) {
					logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
				}
		    }
        }

        return circleList;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getUserID(java.lang.String)
     */
    public long getUserID(String userName) {
        String query = String.format("SELECT %s FROM %s where %s=\"%s\";",
                DBConstants.USER_ID, DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, userName
        );
        return idHelper(query, DBConstants.USER_ID);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getUserName(long)
     */
    public String getUserName(long userID) {
        String query = String.format("SELECT %s FROM %s where %s=%s;",
                DBConstants.USER_USERNAME, DBConstants.USER_TABLE,
                DBConstants.USER_ID, userID
        );
        return nameHelper(query, DBConstants.USER_USERNAME);
    }


    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMessagesSentByUser(long, edu.northeastern.ccs.serverim.MessageType)
     */
    public List<Message> getMessagesSentByUser(long id, MessageType type, int start, int limit) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = '%s' ORDER BY %s DESC",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, id, DBConstants.MESSAGE_TYPE, type, DBConstants.MESSAGE_TIME);

        if (limit == -1) {
            query += ";";
        } else {
            query += "  LIMIT " + (start + limit) + ";";
        }

        List<Message> messageList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();

            rs.relative(start);
            while (rs.next()) {
                Message msg = new Message(rs.getLong(DBConstants.MESSAGE_ID), MessageType.get(rs.getString(DBConstants.MESSAGE_TYPE)), getUserName(rs.getLong(DBConstants.MESSAGE_SENDER_ID)),
                        getUserName(rs.getInt(DBConstants.MESSAGE_RECEIVER_ID)), rs.getString(DBConstants.MESSAGE_BODY), rs.getInt(DBConstants.IS_DELETED));
                messageList.add(msg);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return messageList;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMessagesSentToUser(long, edu.northeastern.ccs.serverim.MessageType)
     */
    public List<Message> getMessagesSentToUser(long id, MessageType type, int start, int limit) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = '%s' ORDER BY %s DESC",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID, id, DBConstants.MESSAGE_TYPE, type, DBConstants.MESSAGE_TIME);

        if (limit == -1) {
            query += ";";
        } else {
            query += " LIMIT " + (start + limit) + ";";
        }

        List<Message> messageList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();

            rs.relative(start);
            while (rs.next()) {
                Message msg = new Message(rs.getLong(DBConstants.MESSAGE_ID), MessageType.get(rs.getString(DBConstants.MESSAGE_TYPE)), getUserName(rs.getInt(DBConstants.MESSAGE_SENDER_ID)),
                        getUserName(rs.getInt(DBConstants.MESSAGE_RECEIVER_ID)), rs.getString(DBConstants.MESSAGE_BODY), rs.getInt(DBConstants.IS_DELETED));
                messageList.add(msg);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return messageList;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getMessagesFromUserChat(long, long)
     */
    public List<Message> getMessagesFromUserChat(String sender, String receiver, int start, int limit) {
        String query = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d AND %s = '%s' ORDER BY %s DESC",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID, getUserID(receiver),
                DBConstants.MESSAGE_SENDER_ID, getUserID(sender),
                DBConstants.MESSAGE_TYPE, MessageType.DIRECT, DBConstants.MESSAGE_TIME);

        if (limit == -1) {
            query += ";";
        } else {
            query += " LIMIT " + start + "," + limit + ";";
        }

        List<Message> messageList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();

            rs.relative(start);
            while (rs.next()) {
                Message msg = new Message(rs.getLong(DBConstants.MESSAGE_ID),
                        MessageType.get(rs.getString(DBConstants.MESSAGE_TYPE)),
                        getUserName(rs.getInt(DBConstants.MESSAGE_SENDER_ID)),
                        getUserName(rs.getInt(DBConstants.MESSAGE_RECEIVER_ID)),
                        rs.getString(DBConstants.MESSAGE_BODY), rs.getInt(DBConstants.IS_DELETED));
                messageList.add(msg);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return messageList;
    }

    //-----------------Group Queries----------------------------------

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getGroupMembers(java.lang.String)
     */
    public List<String> getGroupMembers(String name) {
        String query = String.format("SELECT gi.%s\n" +
                        "FROM %s as gi, %s as g\n" +
                        "WHERE g.%s = '%s' AND gi.%s = g.%s;",
                DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_TABLE,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, name,
                DBConstants.GROUP_INFO_GROUP_ID, DBConstants.GROUP_ID);
        return getPeopleHelper(query, "gi." + DBConstants.GROUP_INFO_USER_ID);

    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getGroupModerators(java.lang.String)
     */
    public List<String> getGroupModerators(String name) {
        String query = String.format("SELECT gi.%s\n" +
                        "FROM %s as gi, %s as g\n" +
                        "WHERE g.%s = '%s' AND gi.%s = g.%s AND gi.%s = %d;",
                DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_TABLE,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, name,
                DBConstants.GROUP_INFO_GROUP_ID, DBConstants.GROUP_ID,
                DBConstants.GROUP_INFO_USER_ROLE, DBConstants.GROUP_INFO_USER_ROLE_MODERATOR);
        return getPeopleHelper(query, "gi." + DBConstants.GROUP_INFO_USER_ID);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#createGroup(java.lang.String, java.lang.String)
     */
    @Override
    public long createGroup(String sender, String groupName) {
        String query = String.format("INSERT INTO %s (%s) values ('%s');",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName);
        long groupId = doInsertQuery(query);
        addGroupMember(sender, groupName, DBConstants.GROUP_INFO_USER_ROLE_MODERATOR);
        return groupId;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#deleteGroup(java.lang.String, java.lang.String)
     */
    @Override
    public void deleteGroup(String sender, String groupName) {
        String groupInfoDeleteQuery = String.format(
                "DELETE t.* FROM %s as t inner join %s on " +
                        "t.%s = %s.%s WHERE %s = \'%s\' AND t.%s > 0",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_ID,
                DBConstants.GROUP_NAME, groupName, DBConstants.GROUP_INFO_GROUP_ID);
        String groupDeleteQuery = String.format("DELETE FROM %s" +
                        " WHERE %s = '%s' AND %s > 0;",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName,
                DBConstants.GROUP_ID);

        //delete entries from group info
        doUpdateQuery(groupInfoDeleteQuery);
        //delete group
        doUpdateQuery(groupDeleteQuery);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#isModerator(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isModerator(String sender, String groupName) {
        String query = String.format("SELECT gi.%s FROM %s as gi\n" +
                        "where gi.%s = %d AND gi.%s = %d;",
                DBConstants.GROUP_INFO_USER_ROLE, DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                getGroupID(groupName), DBConstants.GROUP_INFO_USER_ID, getUserID(sender));
        int role = -1;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                role = rs.getInt(DBConstants.GROUP_INFO_USER_ROLE);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return role == DBConstants.GROUP_INFO_USER_ROLE_MODERATOR;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#isGroupMember(java.lang.String, java.lang.String)
     */
    @Override
    public boolean isGroupMember(String groupName, String sender) {
        String query = String.format("SELECT * FROM %s as gi\n" +
                        "where gi.%s = %d AND gi.%s = %d;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                getGroupID(groupName), DBConstants.GROUP_INFO_USER_ID, getUserID(sender));
        boolean isMember = false;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            isMember = rs.next();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return isMember;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#makeModerator(java.lang.String, java.lang.String)
     */
    @Override
    public void makeModerator(String groupName, String toBeModerator) {
        String query = String.format("UPDATE %s as gi SET %s = %d\n" +
                        "where gi.%s = %d AND gi.%s = %s AND gi.%s > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_USER_ROLE, DBConstants.GROUP_INFO_USER_ROLE_MODERATOR,
                DBConstants.GROUP_INFO_GROUP_ID, getGroupID(groupName),
                DBConstants.GROUP_INFO_USER_ID, getUserID(toBeModerator), DBConstants.GROUP_INFO_ID);
        doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#removeMember(java.lang.String, java.lang.String)
     */
    @Override
    public void removeMember(String groupName, String member) {
        String query = String.format("DELETE FROM %s " +
                        "WHERE %s = %d AND %s = %d AND %s > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID, getGroupID(groupName),
                DBConstants.GROUP_INFO_USER_ID, getUserID(member), DBConstants.GROUP_INFO_ID);
        doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getAllGroupMembers(java.lang.String)
     */
    @Override
    public Set<String> getAllGroupMembers(String groupName) {
        String query = String.format("SELECT gi.%s FROM %s as gi\n" +
                        "WHERE gi.%s = %d;",
                DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID, getGroupID(groupName));
        Set<String> groupMembers = new HashSet<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                groupMembers.add(getUserName(rs.getLong(DBConstants.GROUP_INFO_USER_ID)));
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return groupMembers;
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#addGroupMember(java.lang.String, java.lang.String, int)
     */
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

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#removeGroupMember(java.lang.String, java.lang.String)
     */
    public long removeGroupMember(String userName, String groupName) {
        long userId = getUserID(userName);
        long groupId = getGroupID(groupName);

        String query = String.format("DELETE FROM %s\n" +
                        "WHERE %s = %d AND %s = %d AND id > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID, groupId,
                DBConstants.GROUP_INFO_USER_ID, userId, DBConstants.GROUP_ID);

        return doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#changeMemberRole(long, long, int)
     */
    public void changeMemberRole(long userId, long groupId, int role) {
        String query = String.format("UPDATE %s\n" +
                        "SET %s = %d\n" +
                        "WHERE %s = %d AND %s = %d AND %s > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_USER_ROLE, role,
                DBConstants.GROUP_INFO_USER_ID, userId, DBConstants.GROUP_INFO_GROUP_ID, groupId, DBConstants.GROUP_ID);

        doUpdateQuery(query);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getGroupID(java.lang.String)
     */
    public long getGroupID(String groupName) {
        String query = String.format("SELECT %s FROM %s where %s='%s';",
                DBConstants.GROUP_ID, DBConstants.GROUP_TABLE,
                DBConstants.GROUP_NAME, groupName
        );

        return idHelper(query, DBConstants.GROUP_ID);
    }

    /* (non-Javadoc)
     * @see edu.northeastern.ccs.im.persistence.IQueryHandler#getGroupName(long)
     */
    public String getGroupName(long groupID) {
        String query = String.format("SELECT %s FROM %s where %s=%d;",
                DBConstants.GROUP_NAME, DBConstants.GROUP_TABLE,
                DBConstants.GROUP_ID, groupID
        );
        return nameHelper(query, DBConstants.GROUP_NAME);
    }

    @Override
    public void updateGroupVisibility(String groupName, Boolean makeInVisible) {
        String query = String.format("UPDATE %s SET %s=%d WHERE %s='%s';",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_IS_PRIVATE,
                makeInVisible ? DBConstants.GROUP_PRIVATE_CODE : DBConstants.GROUP_PUBLIC_CODE,
                DBConstants.GROUP_NAME, groupName);
        doUpdateQuery(query);
    }

    //-----------------DB Insert/Update Queries-------------------

    /**
     * Do insert query.
     *
     * @param query the query
     * @return the long
     */
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
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(null, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return key;
    }

    /**
     * Do update query.
     *
     * @param query the query
     * @return the int
     */
    public int doUpdateQuery(String query) {
        PreparedStatement statement = null;
        int updateCode = 0;
        try {
            statement = connection.prepareStatement(query);
            updateCode = statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(null, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return updateCode;
    }

    //---------------------helper methods------------------------------

    /**
     * Id helper.
     *
     * @param query the query
     * @return the long
     */
    //helper method for queries that return ID
    private long idHelper(String query, String selectColumn) {
        long id = -1l;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                id = rs.getLong(selectColumn);
            }
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return id;
    }


    /**
     * Gets the people helper.
     *
     * @param query the query
     * @return the people helper
     */
    //helper method for group members or moderator list
    private List<String> getPeopleHelper(String query, String selectColumn) {
        List<String> memberList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while(rs.next()) {
                memberList.add(getUserName(rs.getInt(selectColumn)));
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        }  finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return memberList;
    }

    /**
     * Name helper.
     *
     * @param query the query
     * @return the string
     */
    //helper method that returns name of some entity
    private String nameHelper(String query, String selectColumn) {
        String name = "";
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                name = rs.getString(selectColumn);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }
        return name;
    }

    /**
     * Name availability helper.
     *
     * @param query the query
     * @return true, if successful
     */
    // helper method that checks if the given person or group name is present
    private boolean nameAvailabilityHelper(String query) {
        boolean isNameFound = false;
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            isNameFound = rs.next();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
    		try {
			closeDBResources(rs, statement);
		} catch (NullPointerException | SQLException e) {
			logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
		}
    }
        return isNameFound;
    }

    /**
     * Gets the private messages since login.
     *
     * @param userID the user ID
     * @return the private messages since login
     */
    private List<Message> getPrivateMessagesSinceLogin(long userID) {
        String query = String.format(
                "SELECT %s, %s, %s, %s, %s, %s.%s as %s from %s inner join %s on %s.%s = %s.%s WHERE %s >= %s AND %s = %s AND %s != %d;",
                //select columns
                DBConstants.MESSAGE_BODY, DBConstants.USER_LAST_SEEN,
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.USER_USERNAME, DBConstants.MESSAGE_TABLE,
                DBConstants.MESSAGE_ID, DBConstants.MESSAGE_ID_ALIAS,
                //join tables
                DBConstants.MESSAGE_TABLE, DBConstants.USER_TABLE,
                //join column one
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID,
                ////join column two
                DBConstants.USER_TABLE, DBConstants.USER_ID,
                //Filters
                DBConstants.MESSAGE_TIME, DBConstants.USER_LAST_SEEN, DBConstants.MESSAGE_RECEIVER_ID, userID,
                DBConstants.IS_DELETED, DBConstants.IS_DELETED_TRUE);
        return getMessages(query, DBConstants.MESSAGE_SENDER_ID, userID, DBConstants.MESSAGE_BODY);
    }

    private String getUserLastSeen(long userID) {
        String query = String.format("SELECT %s from %s WHERE %s=%s",
                DBConstants.USER_LAST_SEEN, DBConstants.USER_TABLE,
                DBConstants.USER_ID, userID);
        String time = new Date().toString();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            if (rs.next()) {
                java.sql.Timestamp dbSqlTimestamp = rs.getTimestamp(DBConstants.USER_LAST_SEEN);
                Date date = new Date(dbSqlTimestamp.getTime());
                SimpleDateFormat format = new SimpleDateFormat(DBConstants.DATE_FORMAT);
                time = format.format(date);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
	    }

        return time;
    }

    /**
     * Gets the group messages since login.
     *
     * @param userID the user ID
     * @return the group messages since login
     */
    private List<Message> getGroupMessagesSinceLogin(long userID) {
        String lastSeen = getUserLastSeen(userID);
        String query = String.format(
                "SELECT %s, %s, %s , %s.%s, %s.%s as %s from %s "
                        + "inner join %s on %s.%s = %s.%s "
                        + "inner join %s on %s.%s = %s.%s "
                        + "WHERE (%s >= '%s') AND %s = %s.%s AND %s != %d;",
                //select columns
                DBConstants.MESSAGE_BODY,
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME,
                DBConstants.MESSAGE_TABLE,
                DBConstants.MESSAGE_ID, DBConstants.MESSAGE_ID_ALIAS,
                //join table one
                DBConstants.MESSAGE_TABLE, DBConstants.GROUP_TABLE,
                //join column one
                DBConstants.GROUP_TABLE, DBConstants.GROUP_ID,
                //join column two
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID,
                //join table two
                DBConstants.GROUP_INFO_TABLE,
                //Join column one
                DBConstants.GROUP_TABLE, DBConstants.GROUP_ID,
                //Join column two
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                //Filters
                //Date greater than last seen time
                DBConstants.MESSAGE_TIME, lastSeen,
                //Receiver id is a group that has this user as one of its member
                DBConstants.MESSAGE_RECEIVER_ID, DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                DBConstants.IS_DELETED, DBConstants.IS_DELETED_TRUE);
        return getMessages(query, DBConstants.MESSAGE_SENDER_ID, userID, DBConstants.MESSAGE_BODY);
    }

    /**
     * Gets the messages.
     *
     * @param query the query
     * @return the messages
     */
    private List<Message> getMessages(String query, String senderColumn, long receiverID, String textColumn) {
        List<Message> messages = new ArrayList<>();
        Set<Long> visitedMessages = new HashSet<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                long msgID = rs.getLong(DBConstants.MESSAGE_ID_ALIAS);

                if (!visitedMessages.contains(msgID)) {
                    Message m = Message.makeDirectMessage(getUserName(rs.getLong(senderColumn)), getUserName(receiverID), rs.getString(textColumn));
                    messages.add(m);
                    visitedMessages.add(msgID);
                }

            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
	    		try {
				closeDBResources(rs, statement);
			} catch (NullPointerException | SQLException e) {
				logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
			}
        }
        return messages;
    }

    /**
     * Gets the groups helper.
     *
     * @param query the query
     * @return the groups helper
     */
    private List<Group> getGroupsHelper(String query, String idColumn, String nameColumn) {
        List<Group> groups = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Group grp = new Group(rs.getLong(idColumn), rs.getString(nameColumn));
                groups.add(grp);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
        } finally {
        		try {
        				closeDBResources(rs, statement);
				} catch (NullPointerException | SQLException e) {
					logger.log(Level.INFO, SQL_EXCEPTION_MSG + ": " + e.getMessage());
				}
        }
        return groups;
    }
    
    private void closeDBResources(ResultSet rs, PreparedStatement statement) throws SQLException {
    		if (rs != null) {
			rs.close();
    		}
		if (statement != null) {
			statement.close();
		}
    }

}
