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
    		long userId = 0; 
    		try {
    			User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	        userId = res.getUserID();
	        assertEquals(QueryConstants.USERNAME, res.getUserName());
    		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }

    @Test
    public void testUpdateUserLastLoginSuccess() {
    		long userId = 0;
    		try {
	        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	        userId = res.getUserID();
	        int id = handler.updateUserLastLogin(res.getUserID());
	        assertNotNull(id);
    		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }

    @Test
    public void testUpdateUserLastLoginFailure() {
    		long userId = 0;
		try {
	        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	        userId = res.getUserID();
	        User invalidUser = new User(-1L, res.getUserName(), res.getNickName(), res.getLastSeen());
	        int id = handler.updateUserLastLogin(invalidUser.getUserID());
	        assertNotNull(id);
		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
		}
    }

    @Test
    public void testGetPasswordSuccess() {
    		long userId = 0;
		try {
			User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
			userId = res.getUserID();
	        assertEquals(handler.validateLogin(QueryConstants.USERNAME, QueryConstants.PASS), res.getUserID().longValue());
		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
		}
    }

    @Test
    public void testGetPasswordFailure() {
    		long userId = 0;
		try {
	        User res = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	        userId = res.getUserID();
	        assertEquals(handler.validateLogin(QueryConstants.USERNAME, ""), -1L);
		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
		}
    }

    @Test
    public void testStoreMessageSuccess() {
    		long res = 0;
		try {
	        res = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	        assertNotEquals(res, 0);
		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res);
	        handler.doUpdateQuery(query);
		}
    }

    @Test
    public void testGetMessagesSinceLastLoginSuccess() {
    		User sender = null;
		User receiver = null;
    		long msgId = 0;
    		long senderId = 0;
    		long receiverId = 0;
    		try {
	        sender = handler.createUser(QueryConstants.SENDER_USERNAME,QueryConstants.PASS,QueryConstants.SENDER_USERNAME);
	        receiver = handler.createUser(QueryConstants.RECEIVER_USERNAME,QueryConstants.PASS,QueryConstants.RECEIVER_USERNAME);
	        handler.updateUserLastLogin(receiver.getUserID());
	        msgId = handler.storeMessage(sender.getUserName(), QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	        
	        receiverId = receiver.getUserID();
	        senderId = sender.getUserID();
	        List<Message> messages = handler.getMessagesSinceLastLogin(receiver.getUserID());
	        assertEquals(QueryConstants.MESSAGE_TEXT, messages.get(0).getText());
    		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, receiverId);
	        handler.doUpdateQuery(query);
	
	        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, senderId);
	        handler.doUpdateQuery(query);
	
	        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgId);
	        handler.doUpdateQuery(query);
    		}
    }

    @Test
    public void checkUserNameExistsSuccess() {
    		long userId = 0;
    		try {
	        User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	        userId = user.getUserID();
	        boolean res = handler.checkUserNameExists(user.getUserName());
	        assertTrue(res);
    		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }

    @Test
    public void checkUserNameExistsFailure() {
    		long userId = 0;
    		try {
	        User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	        userId = user.getUserID();
	        boolean res = handler.checkUserNameExists(QueryConstants.INVALID_USERNAME);
	        assertFalse(res);
    		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        String query = String.format("SELECT Count(*) FROM %s;", DBConstants.USER_TABLE);
        int count = 0;
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
    		long id = 0;
    		try {
	    		id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	        Message res = handler.getMessage(id);
	        assertEquals(id, res.getId());
    		} finally {

	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, id);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testGetMessageFailure() {
    		long id = 0;
    		try {
			id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	        Message res = handler.getMessage(id+1);
	        assertNull(res);
    		} finally {
	        // Tear down
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, id);
	        handler.doUpdateQuery(query);
    		}
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
    		long userId = 0;
    		try {
	    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		userId = user.getUserID();
	    		handler.createGroup(QueryConstants.GROUP_NAME);
	    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);
	    		
	    		List<String> members = handler.getGroupMembers(QueryConstants.GROUP_NAME);
	        assertEquals(members.get(0), QueryConstants.USERNAME);
    		} finally {
	        // Tear down
	        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testGetGroupMembersEmptyGroup() {
    		long userId = 0;
    		try {
	    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		userId = user.getUserID();
	    		handler.createGroup(QueryConstants.GROUP_NAME);
	    		
	    		List<String> members = handler.getGroupMembers(QueryConstants.GROUP_NAME);
	        assertEquals(members.size(), 0);
    		} finally {
	        // Tear down
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testGetGroupModeratorsSuccess() {
    		long userId = 0;
    		try {
	    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		userId = user.getUserID();
	    		handler.createGroup(QueryConstants.GROUP_NAME);
	    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 2);
	    		
	    		List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
	        assertEquals(moderators.get(0), QueryConstants.USERNAME);
    		} finally {
	        // Tear down
	        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testGetGroupModeratorsNoModerators() {
    		long userId = 0;
    		try {
	    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		userId = user.getUserID();
	    		handler.createGroup(QueryConstants.GROUP_NAME);
	    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);
	    		
	    		List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
	    		assertEquals(moderators.size(), 0);
    		} finally {
	        // Tear down
	        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testChangeGroupMemberRoleSuccess() {
    		long userId = 0;
    		try {
	    		User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		userId = user.getUserID();
	    		long groupId = handler.createGroup(QueryConstants.GROUP_NAME);
	    		handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);
	    		
	    		handler.changeMemberRole(user.getUserID(), groupId, 2);
	    		
	    		List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
	        assertEquals(moderators.get(0), QueryConstants.USERNAME);
    		} finally {
	        // Tear down
	        handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
	        String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testGetGroupNameSuccess() {
    		try {
	    		long groupId = handler.createGroup(QueryConstants.GROUP_NAME);
	    		
	    		String groupName = handler.getGroupName(groupId);
	    		
	        assertEquals(QueryConstants.GROUP_NAME, groupName);
    		} finally {
	        // Tear down
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
    		}
    }
    
    @Test
    public void testGetGroupNameNonExistantGroup() {
    		try {
	    		long groupId = handler.createGroup(QueryConstants.GROUP_NAME);
	    		
	    		String groupName = handler.getGroupName(groupId+1);
	    		
	        assertEquals(groupName, "");
    		} finally {
	        // Tear down
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
    		}
    }
    
    @Test
    public void testGetGroupIdNonExistantGroup() {
    		try {
	    		handler.createGroup(QueryConstants.GROUP_NAME);
	    		
	    		long res = handler.getGroupID(QueryConstants.GROUP_NAME + QueryConstants.GROUP_NAME);
	    		
	        assertEquals(res, -1L);
    		} finally {
	        // Tear down
	        handler.deleteGroup(QueryConstants.GROUP_NAME);
    		}
    }
    
    @Test
    public void testGetMessagesSentByUser() {
    		long msgOneId = 0;
    		long msgTwoId = 0;
    		long userOneId = 0;
    		long userTwoId = 0;
    		try {
	    		User userOne = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		User userTwo = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	    		msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_SECOND_TEXT);    		
	    		userOneId = userOne.getUserID();
	    		userTwoId = userTwo.getUserID();
	    		
			List<Message> messageList = handler.getMessagesSentByUser(userOne.getUserID(), MessageType.DIRECT);
			assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(0).getText());
			assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(1).getText());
    		} finally {
			// Tear down
			String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgOneId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgTwoId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userOneId);
	        handler.doUpdateQuery(query);
	        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userTwoId);
	        handler.doUpdateQuery(query);
    		}
    }
    
    @Test
    public void testGetMessagesSentToUser() {
    		long msgOneId = 0;
		long msgTwoId = 0;
		long userOneId = 0;
		long userTwoId = 0;
		try {
	    		User userOne = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		User userTwo = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	    		msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_SECOND_TEXT);
	    		userOneId = userOne.getUserID();
	    		userTwoId = userTwo.getUserID();
	
			List<Message> messageList = handler.getMessagesSentToUser(userTwo.getUserID(), MessageType.DIRECT);
			assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(0).getText());
			assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(1).getText());
		} finally {
			// Tear down
			String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgOneId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgTwoId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userOneId);
	        handler.doUpdateQuery(query);
	        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userTwoId);
	        handler.doUpdateQuery(query);
		}
    }
    
    @Test
    public void testGetMessagesFromUserChat() {
    		long msgOneId = 0;
		long msgTwoId = 0;
		long msgThreeId = 0;
		long userOneId = 0;
		long userTwoId = 0;
		long userThreeId = 0;
    		try {
	    		User userOne = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		User userTwo = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		User userThree = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
	    		msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_TEXT);
	    		msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_SECOND_TEXT);
	    		msgThreeId = handler.storeMessage(userOne.getUserName(), userThree.getUserName(), MessageType.DIRECT, QueryConstants.MESSAGE_SECOND_TEXT);
	    		userOneId = userOne.getUserID();
	    		userTwoId = userTwo.getUserID();
	    		userThreeId = userThree.getUserID();
	
			List<Message> messageList = handler.getMessagesFromUserChat(userOne.getUserID(), userTwo.getUserID());
			assertEquals(messageList.size(), 2);
			assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(0).getText());
			assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(1).getText());
    		} finally {
			// Tear down
			String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgOneId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgTwoId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, msgThreeId);
			handler.doUpdateQuery(query);
			query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userOneId);
	        handler.doUpdateQuery(query);
	        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userTwoId);
	        handler.doUpdateQuery(query);
	        query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userThreeId);
	        handler.doUpdateQuery(query);
	    }
    }
}
