package edu.northeastern.ccs.im.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import edu.northeastern.ccs.im.constants.QueryConstants;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

public class QueryHandlerMySQLImplTest {

    private QueryHandlerMySQLImpl handler;

    @Before
    public void setUp() {
        handler = new QueryHandlerMySQLImpl();
    }

    @Test
    public void testCreateUserSuccess() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        assertEquals(QueryConstants.USERNAME, res.getUserName());

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
        assertEquals(handler.validateLogin(QueryConstants.USERNAME, QueryConstants.PASS), res.getUserID().longValue());

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testGetPasswordFailure() {
        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
        assertEquals(handler.validateLogin(QueryConstants.USERNAME, ""), -1L);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, res.getUserID());
        handler.doUpdateQuery(query);
    }

    @Test
    public void testStoreMessageSuccess() {
        long res = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
        assertNotEquals(res, 0);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res);
        handler.doUpdateQuery(query);
    }

    @Test
    public void testGetMessagesSinceLastLoginSuccess() {
        User sender = handler.createUser(QueryConstants.SENDER_USERNAME,QueryConstants.PASS,QueryConstants.SENDER_USERNAME);
        User receiver = handler.createUser(QueryConstants.RECEIVER_USERNAME,QueryConstants.PASS,QueryConstants.RECEIVER_USERNAME);
        handler.updateUserLastLogin(receiver.getUserID());
        long msgId = handler.storeMessage(sender.getUserName(), QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);

        List<Message> messages = handler.getMessagesSinceLastLogin(receiver.getUserID());
        assertEquals(QueryConstants.MESSAGE_TEXT, messages.get(0).getText());

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, receiver.getUserID());
        handler.doUpdateQuery(query);

        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, sender.getUserID());
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
    
    @Test
    public void testGetMessageSuccess() {
    		long id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
        Message res = handler.getMessage(id);
        assertEquals(id, res.getId());

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, id);
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testGetMessageFailure() {
    		long id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
        Message res = handler.getMessage(id+1);
        assertNull(res);

        // Tear down
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, id);
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testDeleteMessageSuccess() {
    		long id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
    		handler.deleteMessage(id);
        Message res = handler.getMessage(id);
        assertEquals(res.getIsDeleted(), 1);
    }
    
    @Test
    public void testGetGroupMembersSuccess() {
    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
    		handler.createGroup(QueryConstants.GROUP_NAME);
    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);
    		
    		List<String> members = handler.getGroupMembers(QueryConstants.GROUP_NAME);
        assertEquals(members.get(0), QueryConstants.USERNAME);

        // Tear down
        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        handler.deleteGroup(QueryConstants.GROUP_NAME);
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testGetGroupMembersEmptyGroup() {
    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
    		handler.createGroup(QueryConstants.GROUP_NAME);
    		
    		List<String> members = handler.getGroupMembers(QueryConstants.GROUP_NAME);
        assertEquals(members.size(), 0);

        // Tear down
        handler.deleteGroup(QueryConstants.GROUP_NAME);
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testGetGroupModeratorsSuccess() {
    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
    		handler.createGroup(QueryConstants.GROUP_NAME);
    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 2);
    		
    		List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
        assertEquals(moderators.get(0), QueryConstants.USERNAME);

        // Tear down
        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        handler.deleteGroup(QueryConstants.GROUP_NAME);
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testGetGroupModeratorsNoModerators() {
    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
    		handler.createGroup(QueryConstants.GROUP_NAME);
    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);
    		
    		List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
    		assertEquals(moderators.size(), 0);

        // Tear down
        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        handler.deleteGroup(QueryConstants.GROUP_NAME);
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testChangeGroupMemberRoleSuccess() {
    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
    		long groupId = handler.createGroup(QueryConstants.GROUP_NAME);
    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);
    		
    		handler.changeMemberRole(user.getUserID(), groupId, 2);
    		
    		List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
        assertEquals(moderators.get(0), QueryConstants.USERNAME);

        // Tear down
        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        handler.deleteGroup(QueryConstants.GROUP_NAME);
        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user.getUserID());
        handler.doUpdateQuery(query);
    }
    
    @Test
    public void testGetGroupNameSuccess() {
    		long groupId = handler.createGroup(QueryConstants.GROUP_NAME);
    		
    		String groupName = handler.getGroupName(groupId);
    		
        assertEquals(QueryConstants.GROUP_NAME, groupName);

        // Tear down
        handler.deleteGroup(QueryConstants.GROUP_NAME);
    }
    
    @Test
    public void testGetGroupNameNonExistantGroup() {
    		long groupId = handler.createGroup(QueryConstants.GROUP_NAME);
    		
    		String groupName = handler.getGroupName(groupId+1);
    		
        assertEquals(groupName, "");

        // Tear down
        handler.deleteGroup(QueryConstants.GROUP_NAME);
    }
    
    @Test
    public void testGetGroupIdNonExistantGroup() {
    		handler.createGroup(QueryConstants.GROUP_NAME);
    		
    		long res = handler.getGroupID(QueryConstants.GROUP_NAME + QueryConstants.GROUP_NAME);
    		
        assertEquals(res, -1L);

        // Tear down
        handler.deleteGroup(QueryConstants.GROUP_NAME);
    }
    
}
