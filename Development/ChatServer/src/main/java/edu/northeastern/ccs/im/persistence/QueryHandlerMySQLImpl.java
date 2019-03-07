package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueryHandler {

    final Logger logger = Logger.getGlobal();
    public static final String SQL_EXCEPTION_MSG = "SQL Exception";
    private Connection connection;

    public QueryHandler() {
        connection = DBHandler.getConnection();
    }

    //-----------------User Queries-------------------
    public User createUser(String userName, String pass, String nickName) {
        Date date = new Date(System.currentTimeMillis());
        String query = String.format("INSERT into %s (%s,%s,%s,%s) VALUES('%s','%s','%s',%tc);", DBConstants.USER_TABLE,
                DBConstants.USER_USERNAME, DBConstants.USER_PASS, DBConstants.USER_NICKNAME, DBConstants.USER_LAST_SEEN,
                userName, pass, nickName, date);
        long id = doInsertQuery(query);
        if (id != -1) {
            return new User(id, userName, nickName, date.getTime());
        }
        return null;
    }

    public void updateUserLastLogin(User user) {
        String query = String.format("UPDATE %s set %s = %d WHERE %s = %d;",
                DBConstants.USER_TABLE, DBConstants.USER_LAST_SEEN, System.currentTimeMillis(),
                DBConstants.USER_ID, user.getUserID());
        doUpdateQuery(query);
    }

    public List<Long> getCircles(User user) {
        long userId = user.getUserID();
        String query = String.format("SELECT * from %s WHERE %s = %d OR %s = %d;",
                DBConstants.CIRCLES_TABLE, DBConstants.CIRCLE_USER_1_ID, user.getUserID(),
                DBConstants.CIRCLE_USER_2_ID, user.getUserID());
        List<Long> circle = new ArrayList<>();
        ResultSet rs = doSelectQuery(query);
        if (rs != null) {
            try {
                while (rs.next()) {
                    if (rs.getLong(2) != userId) {
                        circle.add(rs.getLong(2));
                    }
                    if (rs.getLong(3) != userId) {
                        circle.add(rs.getLong(3));
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.INFO, SQL_EXCEPTION_MSG);
            }
        }
        return circle;
    }


    //-----------------Message Queries-------------------
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


    public boolean checkUserNameExists(String name) {
        String query = String.format("SELECT * FROM %s WHERE %s = '%s';",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
        ResultSet rs = doSelectQuery(query);
        if (rs != null) {
            try {
                return rs.next();
            } catch (SQLException e) {
                logger.log(Level.INFO, SQL_EXCEPTION_MSG);
            }
        }
        return false;
    }

    public String getPassword(String name) {
        String query = String.format("SELECT %s FROM %s WHERE %s = '%s'",
                DBConstants.USER_PASS, DBConstants.USER_TABLE, DBConstants.USER_USERNAME, name);
        ResultSet rs = doSelectQuery(query);
        if (rs != null) {
            try {
                rs.next();
                return rs.getString(1);
            } catch (SQLException e) {
                logger.log(Level.INFO, SQL_EXCEPTION_MSG);
            }
        }
        return "";
    }

    private ResultSet doSelectQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        ResultSet rs = null;
        if (statement != null) {
            try {
                rs = statement.executeQuery();
                return rs;
            } catch (SQLException e) {
                logger.log(Level.INFO, SQL_EXCEPTION_MSG);
            }
        }
        return null;
    }

    private long doInsertQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return -1;
    }

    private int doUpdateQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return 0;
    }

}
