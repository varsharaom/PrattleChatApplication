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

public class QueryHandlerMySQLImpl implements IQueryHandler{

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
        if (id != -1) {
            return new User(id, userName, nickName, date.getTime());
        }
        return null;
    }

    public int updateUserLastLogin(User user) {
    		Date date = new Date(System.currentTimeMillis());
    		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String query = String.format("UPDATE %s set %s = '%s' WHERE %s = %d;",
                DBConstants.USER_TABLE, DBConstants.USER_LAST_SEEN, format.format(date),
                DBConstants.USER_ID, user.getUserID());
        return doUpdateQuery(query);
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

    @Override
    public Boolean validateLogin(String username, String password) {
        String query = String.format("SELECT * from %s WHERE %s = %s and %s = %s",
                DBConstants.USER_TABLE, DBConstants.USER_USERNAME, username, DBConstants.USER_PASS, password);
        ResultSet rs = doSelectQuery(query);
        try {
            while(rs.next()) {
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
                DBConstants.MESSAGE_TYPE, DBConstants.MESSAGE_BODY,  DBConstants.MESSAGE_TIME,
                senderID, receiverID, type, msgText, format.format(date));
        return doInsertQuery(query);
    }

    public List<Message> getMessagesSinceLastLogin(User user) {
        String query = String.format("SELECT * from %s WHERE %s > %d;",
                DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_TIME, user.getLastSeen());
        ResultSet rs = doSelectQuery(query);
        List<Message> messages = new ArrayList<>();
        try {
            while(rs.next()) {
                Message m = Message.makeBroadcastMessage("placeholder", rs.getString("body"));
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
        try {
			return rs.next();
		} catch (SQLException e) {
			logger.log(Level.INFO, DBConstants.EXCEPTION_MESSAGE);
		}
        return false; 
    }

    public ResultSet doSelectQuery(String query) {
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

    public long doInsertQuery(String query) {
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

    public int doUpdateQuery(String query) {
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
