package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Todo: All stubs and signatures, need to update the querying and updating part.
 */
public class QueryHandler {

    public static User createUser(String userName, String nickName) {
        String query = "Insert into " + QueryConstants.USER_TABLE
                + " values(" + userName + "," + nickName
                + "," + System.currentTimeMillis() + ")";
        return new User(doInsertQuery(query), userName, nickName);
    }

    public static void updateUserLastLogin(User user) {
        String query = "Update " + QueryConstants.USER_TABLE + " set "
                + QueryConstants.USER_LAST_LOGIN + " = " + System.currentTimeMillis()
                + " where " + QueryConstants.USER_ID + "=";

    }

    public static List<Long> getCircles(User user) {
        return new ArrayList<>();
    }

    //Todo : add msg type field (probably and enum).
    public static void storeMessage(long senderID, long receiverID, String msgText) {
    }

    public static List<Message> getMessagesSinceLastLogin(User user) {
        return new ArrayList<>();
    }

    private ResultSet doSelectQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = DBHandler.getConnection().prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet rs = null;
        try {
            rs = statement.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("This is not expected to happen");
    }

    private static long doInsertQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = DBHandler.getConnection().prepareStatement(query);
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Failed to insert");
    }

    private static int doUpdateQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = DBHandler.getConnection().prepareStatement(query);
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
