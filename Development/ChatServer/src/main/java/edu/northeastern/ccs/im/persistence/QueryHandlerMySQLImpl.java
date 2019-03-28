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
            user = new User(id, userName, nickName, date.getTime(), 0);
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
        String query = String.format("SELECT %s from %s WHERE %s ='%s' and %s = '%s'",
                DBConstants.USER_ID, DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, username, DBConstants.USER_PASS, password);
        return idHelper(query);
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
        String query = String.format("Select %s,%s,%s,%s,%s,%s from %s where %s=%s;",
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
        String query = String.format("Update %s set %s=%s where %s=%s;",
                DBConstants.MESSAGE_TABLE, DBConstants.IS_DELETED, DBConstants.IS_DELETED_TRUE,
                DBConstants.MESSAGE_ID, messageID
        );
        doUpdateQuery(query);
    }

    public List<Message> getMessagesSinceLastLogin(long userID) {
        List<Message> messages = new ArrayList<>();
        messages.addAll(getPrivateMessagesSinceLogin(userID));
        messages.addAll(getGroupMessagesSinceLogin(userID));
        return messages;
    }


    public long addUserToCircle(String senderName, String receiverName) {
        long senderID = getUserID(senderName);
        long receiverID = getUserID(receiverName);

        String query = String.format("INSERT INTO %s (%s, %s) VALUES(%d,%d);",
                DBConstants.CIRCLES_TABLE, DBConstants.CIRCLE_USER_1_ID,
                DBConstants.CIRCLE_USER_2_ID, senderID, receiverID);
        return doInsertQuery(query);
    }

    public boolean checkUserNameExists(String name) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
        return nameAvailabilityHelper(query);
    }

    public boolean checkGroupNameExists(String groupName) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName);
        return nameAvailabilityHelper(query);
    }

    @Override
    public List<Group> getAllGroups() {
        String query = String.format("Select %s, %s from %s;",
                //Select columns
                DBConstants.GROUP_ID, DBConstants.GROUP_NAME,
                //table
                DBConstants.GROUP_TABLE);

        return getGroupsHelper(query);
    }

    @Override
    public List<Group> getMyGroups(String senderName) {
        String query = String.format("Select %s.%s, %s.%s from %s "
                        + "inner join %s on %s.%s = %s.%s "
                        + "inner join %s on %s.%s = %s.%s "
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

        return getGroupsHelper(query);
    }

    @Override
    public List<User> getAllUsers() {
        String query = String.format("SELECT %s, %s, %s FROM %s where %s = %d;",
                DBConstants.USER_ID, DBConstants.USER_USERNAME,
                DBConstants.USER_NICKNAME, DBConstants.USER_TABLE,
                DBConstants.USER_INVISIBLE, 0);

        List<User> userList = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            Date date = new Date(System.currentTimeMillis());
            while (rs.next()) {
                User user = new User(rs.getLong(1),
                        rs.getString(2), rs.getString(3), date.getTime(), 0);
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
        long senderID = getUserID(senderName);
        String query = String.format("Select %s, %s from %s where %s = %s or %s = %s",
                DBConstants.CIRCLE_USER_1_ID, DBConstants.CIRCLE_USER_2_ID,
                DBConstants.CIRCLES_TABLE,
                DBConstants.CIRCLE_USER_1_ID, senderID,
                DBConstants.CIRCLE_USER_2_ID, senderID);
        Set<Long> circleIDs = new HashSet<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                long idToAdd = rs.getLong(1) == senderID ?
                        rs.getLong(2) : rs.getLong(1);
                circleIDs.add(idToAdd);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        List<User> userList = getAllUsers();
        List<User> circleList = new ArrayList<>();
        for (User user : userList) {
            if (circleIDs.contains(user.getUserID())) {
                circleList.add(user);
            }
        }
        return circleList;

    }


    public long getUserID(String userName) {
        String query = String.format("SELECT %s FROM %s where %s=\"%s\";",
                DBConstants.USER_ID, DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, userName
        );
        return idHelper(query);
    }

    public String getUserName(long userID) {
        String query = String.format("SELECT %s FROM %s where %s=%s;",
                DBConstants.USER_USERNAME, DBConstants.USER_TABLE,
                DBConstants.USER_ID, userID
        );
        return nameHelper(query);
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
        return getPeopleHelper(query);

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
        System.out.println(query);
        return getPeopleHelper(query);
    }

    @Override
    public long createGroup(String sender, String groupName) {
        String query = String.format("INSERT INTO %s (%s) values ('%s');",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName);
        long groupId = doInsertQuery(query);
        addGroupMember(sender, groupName, DBConstants.GROUP_INFO_ADMIN_ROLE_ID);
        return groupId;
    }

    @Override
    public void deleteGroup(String sender, String groupName) {
        String query = String.format("DELETE FROM %s" +
                        "WHERE %s = '%s' AND %s > 0;",
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME, groupName,
                DBConstants.GROUP_ID);
        doUpdateQuery(query);
    }

    @Override
    public boolean isModerator(String sender, String groupName) {
        String query = String.format("SELECT gi.%s FROM %s as gi\n" +
                        "where gi.%s = %d AND gi.%s = %d;",
                DBConstants.GROUP_INFO_USER_ROLE, DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                getGroupID(groupName), DBConstants.GROUP_INFO_USER_ID, getUserID(sender));
        int role = -1;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                role = rs.getInt(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return role == DBConstants.GROUP_INFO_ADMIN_ROLE_ID;
    }

    @Override
    public boolean isGroupMember(String groupName, String sender) {
        String query = String.format("SELECT * FROM %s as gi\n" +
                        "where gi.%s = %d AND gi.%s = %d;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                getGroupID(groupName), DBConstants.GROUP_INFO_USER_ID, getUserID(sender));
        boolean isMember = false;
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            isMember = rs.next();
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return isMember;
    }

    @Override
    public void makeModerator(String groupName, String toBeModerator) {
        String query = String.format("UPDATE %s as gi SET %s = %d\n" +
                        "where gi.%s = %d AND gi.%s = %s AND gi.%s > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_USER_ROLE, DBConstants.GROUP_INFO_ADMIN_ROLE_ID,
                DBConstants.GROUP_INFO_GROUP_ID, getGroupID(groupName),
                DBConstants.GROUP_INFO_USER_ID, getUserID(toBeModerator), DBConstants.GROUP_INFO_ID);
        doUpdateQuery(query);
    }

    @Override
    public void removeMember(String groupName, String member) {
        String query = String.format("DELETE FROM %s " +
                        "WHERE %s = %d AND %s = %d AND %s > 0;",
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID, getGroupID(groupName),
                DBConstants.GROUP_INFO_USER_ID, getUserID(member), DBConstants.GROUP_INFO_ID);
        doUpdateQuery(query);
    }

    @Override
    public Set<String> getAllGroupMembers(String groupName) {
        String query = String.format("SELECT gi.%s FROM %s as gi\n" +
                        "WHERE gi.%s = %d;",
                DBConstants.GROUP_INFO_USER_ID, DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID, getGroupID(groupName));
        Set<String> groupMembers = new HashSet<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                groupMembers.add(getUserName(rs.getLong(1)));
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return groupMembers;
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

        return idHelper(query);
    }

    public String getGroupName(long groupID) {
        String query = String.format("SELECT %s FROM %s where %s=%d;",
                DBConstants.GROUP_NAME, DBConstants.GROUP_TABLE,
                DBConstants.GROUP_ID, groupID
        );
        return nameHelper(query);
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

    //---------------------helper methods------------------------------

    //helper method for queries that return ID
    private long idHelper(String query) {

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

    //helper method for group members or moderator list
    private List<String> getPeopleHelper(String query) {
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

    //helper method that returns name of some entity
    private String nameHelper(String query) {
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

    // helper method that checks if the given person or group name is present
    private boolean nameAvailabilityHelper(String query) {
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

    private List<Message> getPrivateMessagesSinceLogin(long userID) {
        String query = String.format(
                "SELECT %s, %s, %s, %s, %s from %s inner join %s on %s.%s = %s.%s WHERE %s >= %s AND %s = %s;",
                //select columns
                DBConstants.MESSAGE_BODY, DBConstants.USER_LAST_SEEN,
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.USER_USERNAME,
                //join tables
                DBConstants.MESSAGE_TABLE, DBConstants.USER_TABLE,
                //join column one
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID,
                ////join column two
                DBConstants.USER_TABLE, DBConstants.USER_ID,
                //Filters
                DBConstants.MESSAGE_TIME, DBConstants.USER_LAST_SEEN, DBConstants.MESSAGE_RECEIVER_ID, userID);
        return getMessages(query);
    }

    private List<Message> getGroupMessagesSinceLogin(long userID) {
        String query = String.format(
                "SELECT %s, %s, %s, %s , %s.%s from %s "
                        + "inner join %s on %s.%s = %s.%s"
                        + "inner join %s on %s.%s = %s.%s"
                        + " WHERE (%s >= %s) AND %s = %s;",
                //select columns
                DBConstants.MESSAGE_BODY, DBConstants.USER_LAST_SEEN,
                DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.GROUP_TABLE, DBConstants.GROUP_NAME,
                //join table one
                DBConstants.MESSAGE_TABLE, DBConstants.GROUP_INFO_TABLE,
                //join column one
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_USER_ID,
                //join column two
                DBConstants.USER_TABLE, DBConstants.USER_ID,
                //join table two
                DBConstants.GROUP_TABLE,
                //Join column one
                DBConstants.GROUP_TABLE, DBConstants.GROUP_ID,
                //Join column two
                DBConstants.GROUP_INFO_TABLE, DBConstants.GROUP_INFO_GROUP_ID,
                //Filters
                //Date greater than last seen time
                DBConstants.MESSAGE_TIME, DBConstants.USER_LAST_SEEN,
                //Receiver id is a group that has this user as one of its member
                DBConstants.MESSAGE_RECEIVER_ID, userID);
        return getMessages(query);
    }

    private List<Message> getMessages(String query) {
        List<Message> messages = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Message m = Message.makeDirectMessage(getUserName(rs.getLong(3)), rs.getString(5), rs.getString(1));
                messages.add(m);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return messages;
    }

    private List<Group> getGroupsHelper(String query) {
        List<Group> groups = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Group grp = new Group(rs.getLong(1), rs.getString(2));
                groups.add(grp);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return groups;
    }

}
