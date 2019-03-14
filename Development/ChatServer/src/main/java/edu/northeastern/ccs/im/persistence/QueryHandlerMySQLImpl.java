package edu.northeastern.ccs.im.persistence;

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
    public static final String SQL_EXCEPTION_MSG = "SQL Exception";
    private Connection connection;

    public QueryHandlerMySQLImpl() {
        connection = DBHandler.getConnection();
    }

    //-----------------User Queries-------------------
    public User createUser(String userName, String pass, String nickName) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = String.format("UPDATE %s set %s = '%s' WHERE %s = %d;",
                DBConstants.USER_TABLE, DBConstants.USER_LAST_SEEN, format.format(date),
                DBConstants.USER_ID, userID);
        return doUpdateQuery(query);
    }

    @Override
    public Boolean validateLogin(String username, String password) {
        String query = String.format("SELECT * from %s WHERE %s =\"%s\" and %s = \"%s\"",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, username, DBConstants.USER_PASS, password);
        ResultSet rs = doSelectQuery(query);
        try {
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, DBConstants.EXCEPTION_MESSAGE);
        }
        return false;
    }

    //-----------------Message Queries-------------------
    public long storeMessage(long senderID, long receiverID, MessageType type, String msgText) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = String.format("INSERT INTO %s (%s,%s,%s,%s,%s) VALUES(%d,%d,'%s','%s','%s');",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_SENDER_ID, DBConstants.MESSAGE_RECEIVER_ID,
                DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY, DBConstants.MESSAGE_TIME,
                senderID, receiverID, type, msgText, format.format(date));
        return doInsertQuery(query);
    }

    public List<Message> getMessagesSinceLastLogin(long userID) {
        //ToDo : join group id to get all messages where the user is a part of.
        String query = String.format(
                "SELECT %s, %s from %s inner join %s on %s.%s = %s.%s WHERE %s >= %s AND %s = %s;",
                //select columns
                DBConstants.MESSAGE_BODY, DBConstants.USER_LAST_SEEN,
                //join tables
                DBConstants.MESSAGE_TABLE, DBConstants.USER_TABLE,
                //join column one
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_RECEIVER_ID,
                ////join column two
                DBConstants.USER_TABLE, DBConstants.USER_ID,
                //Filters
                DBConstants.MESSAGE_TIME, DBConstants.USER_LAST_SEEN, DBConstants.MESSAGE_RECEIVER_ID, userID);
        ResultSet rs = doSelectQuery(query);
        List<Message> messages = new ArrayList<>();
        try {
            while (rs.next()) {
                Message m = Message.makeBroadcastMessage("placeholder", rs.getString(1));
                messages.add(m);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, DBConstants.EXCEPTION_MESSAGE);
        }
        return messages;
    }


    public boolean checkUserNameExists(String name) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
        ResultSet rs = doSelectQuery(query);
        boolean isNameFound = false;
        try {
            isNameFound = rs.next();
        } catch (SQLException e) {
            logger.log(Level.INFO, DBConstants.EXCEPTION_MESSAGE);
        }
        return isNameFound;
    }

    @Override
    public List<User> getAllUsers() {
        String query = String.format("SELECT %s, %s, %s FROM %s;",
                DBConstants.USER_ID, DBConstants.USER_USERNAME,
                DBConstants.USER_NICKNAME, DBConstants.USER_TABLE);
        ResultSet rs = doSelectQuery(query);
        List<User> userList = new ArrayList<>();

        Date date = new Date(System.currentTimeMillis());
        try {
            while (rs.next()) {
                User user = new User(rs.getLong(1),
                        rs.getString(2), rs.getString(3), date.getTime());
                userList.add(user);
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }


        return userList;
    }

    public ResultSet doSelectQuery(String query) {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.prepareStatement(query);
            if (statement != null) {
                rs = statement.executeQuery();
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return rs;
    }

    public long doInsertQuery(String query) {
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
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return key;
    }

    public int doUpdateQuery(String query) {
        PreparedStatement statement = null;
        int updateCode = 0;
        try {
            statement = connection.prepareStatement(query);
            updateCode = statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return updateCode;
    }

}
