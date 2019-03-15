package edu.northeastern.ccs.im.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.junit.Before;
import org.junit.Test;
import edu.northeastern.ccs.im.constants.QueryConstants;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

public class QueryHandlerMySQLImplTest {

    QueryHandlerMySQLImpl handler;

    @Before
    public void setUp() {
        handler = new QueryHandlerMySQLImpl();
    }

    @Test
    public void testCreateUserSuccess() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        assertEquals(res.getUserName(), QueryConstants.USERNAME);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testUpdateUserLastLoginSuccess() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        int id = handler.updateUserLastLogin(res.getUserID());
        assertNotNull(id);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testUpdateUserLastLoginFailure() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        User invalidUser = new User(-1L, res.getUserName(), res.getNickName(), res.getLastSeen());
        int id = handler.updateUserLastLogin(invalidUser.getUserID());
        assertNotNull(id);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testGetPasswordSuccess() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        assertTrue(handler.validateLogin(QueryConstants.USERNAME, QueryConstants.PASS));

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testGetPasswordFailure() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        assertFalse(handler.validateLogin(QueryConstants.USERNAME, ""));

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testStoreMessageSuccess() {
        long res = handler.storeMessage(QueryConstants.SENDER_ID, QueryConstants.RECEIVER_ID, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
        assertNotEquals(res, 0);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res);
        handler.doUpdateQuery(query);
    }

    @Test
    public void testGetMessagesSinceLastLoginSuccess() {
        User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        handler.updateUserLastLogin(user.getUserID());
        long msgId = handler.storeMessage(QueryConstants.SENDER_ID, user.getUserID(), MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);

        List<Message> messages = handler.getMessagesSinceLastLogin(user.getUserID());
        assertEquals(QueryConstants.MESSAGE_TEXT, messages.get(0).getText());

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgId);
        handler.doUpdateQuery(query);
    }

    @Test
    public void checkUserNameExistsSuccess() {
        User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        boolean res = handler.checkUserNameExists(user.getUserName());
        assertTrue(res);
        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void checkUserNameExistsFailure() {
        User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        boolean res = handler.checkUserNameExists(QueryConstants.INVALID_USERNAME);
        assertFalse(res);
        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        String query = String.format("SELECT Count(*) FROM %s;", DBConstants.USER_TABLE);
        int count = 0;
        List<User> userList = new ArrayList<>();
        try (PreparedStatement statement = DBHandler.getConnection().prepareStatement(query);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                count = rs.getInt(1);
            }
        } finally {
            assertEquals(count, handler.getAllUsers().size());
        }

    }
}
