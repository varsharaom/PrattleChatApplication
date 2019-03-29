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
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import edu.northeastern.ccs.im.constants.QueryConstants;
import edu.northeastern.ccs.serverim.Group;
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
            User invalidUser = new User(-1L, res.getUserName(), res.getNickName(), res.getLastSeen(), 0);
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
            sender = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.SENDER_USERNAME);
            receiver = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.RECEIVER_USERNAME);
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
    public void testMessageSinceLastLoginForGroups() {

        User sender = null;
        User receiver = null;
        long msgId = 0;
        long senderId = 0;
        long receiverId = 0;
        try {
            sender = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.SENDER_USERNAME);
            receiver = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.RECEIVER_USERNAME);
            handler.updateUserLastLogin(receiver.getUserID());
            long msgGrpID = handler.createGroup(sender.getUserName(), QueryConstants.GROUP_2_NAME);
            handler.addGroupMember(receiver.getUserName(), QueryConstants.GROUP_2_NAME, DBConstants.GROUP_INFO_USER_ROLE_MEMBER);
            assertEquals(2, handler.getAllGroupMembers(QueryConstants.GROUP_2_NAME).size());
            msgId = handler.storeMessage(sender.getUserName(), QueryConstants.GROUP_2_NAME, MessageType.GROUP, QueryConstants.MESSAGE_TEXT);

            receiverId = receiver.getUserID();
            senderId = sender.getUserID();
            List<Message> messages = handler.getMessagesSinceLastLogin(receiver.getUserID());
            assertEquals(QueryConstants.MESSAGE_TEXT, messages.get(0).getText());
        } finally {
            // Tear down

            handler.deleteGroup(sender.getUserName(), QueryConstants.GROUP_2_NAME);

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
            Message res = handler.getMessage(id + 1);
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
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);

            List<String> members = handler.getGroupMembers(QueryConstants.GROUP_NAME);
            assertEquals(members.get(0), QueryConstants.USERNAME);
        } finally {
            // Tear down
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
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
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);

            List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
            assertEquals(moderators.get(0), QueryConstants.USERNAME);
        } finally {
            // Tear down
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
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
            long groupId = handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.addGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME, 1);

            handler.changeMemberRole(user.getUserID(), groupId, 2);

            List<String> moderators = handler.getGroupModerators(QueryConstants.GROUP_NAME);
            assertEquals(moderators.get(0), QueryConstants.USERNAME);
        } finally {
            // Tear down
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testGetGroupNameSuccess() {
        try {
            long groupId = handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);

            String groupName = handler.getGroupName(groupId);

            assertEquals(QueryConstants.GROUP_NAME, groupName);
        } finally {
            // Tear down
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetGroupNameNonExistantGroup() {
        try {
            long groupId = handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);

            String groupName = handler.getGroupName(groupId + 1);

            assertEquals(groupName, "");
        } finally {
            // Tear down
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetGroupIdNonExistantGroup() {
        try {
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);

            long res = handler.getGroupID(QueryConstants.GROUP_NAME + QueryConstants.GROUP_NAME);

            assertEquals(res, -1L);
        } finally {
            // Tear down
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testCheckGroupNameExistance() {
        try {
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            assertTrue(handler.checkGroupNameExists(QueryConstants.GROUP_NAME));
        } finally {
            //teardown
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetGroups() {
        try {
            int size = handler.getAllGroups().size();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            assertEquals(handler.getAllGroups().size(), size + 1);
        } finally {
            //teardown
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }

    }

    @Test
    public void testGetGroupsForSpecificUser() {
        User user1 = null;
        try {
            user1 = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.SENDER_USERNAME);
            long msgGrpID = handler.createGroup(user1.getUserName(), QueryConstants.GROUP_2_NAME);
            assertEquals(user1.getUserName(), handler.getGroupModerators(QueryConstants.GROUP_2_NAME).get(0));
            assertEquals(1, handler.getAllGroupMembers(QueryConstants.GROUP_2_NAME).size());
            assertEquals(1, handler.getMyGroups(user1.getUserName()).size());
        } finally {
            //teardown
            handler.deleteGroup(user1.getUserName(), QueryConstants.GROUP_2_NAME);
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user1.getUserID());
            handler.doUpdateQuery(query);
            assertEquals("", handler.getUserName(user1.getUserID()));
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

    @Test
    public void testCheckGroupNameExists() {
        try {
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);

            boolean exists = handler.checkGroupNameExists(QueryConstants.GROUP_NAME);

            assertTrue(exists);
        } finally {
            // Tear down
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetAllGroups() {
        try {
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);

            List<Group> groups = handler.getAllGroups();

            for (Group group : groups) {
                if (group.getName().equals(QueryConstants.GROUP_NAME)) {
                    assertTrue(group.getName().equals(QueryConstants.GROUP_NAME));
                } else {
                    assertFalse(group.getName().equals(QueryConstants.GROUP_NAME));
                }
            }
        } finally {
            // Tear down
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetMyGroups() {
        long userId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.createGroup(QueryConstants.INVALID_USERNAME, QueryConstants.GROUP_2_NAME);

            List<Group> groups = handler.getMyGroups(QueryConstants.USERNAME);

            for (Group group : groups) {
                if (group.getName().equals(QueryConstants.GROUP_NAME)) {
                    assertTrue(group.getName().equals(QueryConstants.GROUP_NAME));
                } else {
                    assertFalse(true);
                }
            }
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            handler.removeGroupMember(QueryConstants.INVALID_USERNAME, QueryConstants.GROUP_2_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_2_NAME);
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testIsModeratorSuccess() {
        long userId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            boolean isModerator = handler.isModerator(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            assertTrue(isModerator);
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testIsGroupMemberSuccess() {
        long userId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            boolean isMember = handler.isGroupMember(QueryConstants.GROUP_NAME, QueryConstants.USERNAME);
            assertTrue(isMember);
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testMakeModeratorSuccess() {
        long userId = -1L;
        long userTwoId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            User userTwo = handler.createUser(QueryConstants.INVALID_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            userTwoId = userTwo.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.addGroupMember(QueryConstants.INVALID_USERNAME, QueryConstants.GROUP_NAME, 1);
            handler.makeModerator(QueryConstants.GROUP_NAME, QueryConstants.INVALID_USERNAME);
            boolean isMember = handler.isModerator(QueryConstants.INVALID_USERNAME, QueryConstants.GROUP_NAME);
            assertTrue(isMember);
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userTwoId);
            handler.doUpdateQuery(query);
            handler.removeGroupMember(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.removeGroupMember(QueryConstants.INVALID_USERNAME, QueryConstants.GROUP_NAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testRemoveGroupMemberSuccess() {
        long userId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.removeMember(QueryConstants.GROUP_NAME, QueryConstants.USERNAME);
            boolean isMember = handler.isGroupMember(QueryConstants.GROUP_NAME, QueryConstants.USERNAME);
            assertFalse(isMember);
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetAllGroupMembersSuccess() {
        long userId = -1L;
        long userTwoId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            User userTwo = handler.createUser(QueryConstants.INVALID_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            userTwoId = userTwo.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            handler.addGroupMember(QueryConstants.INVALID_USERNAME, QueryConstants.GROUP_NAME, QueryConstants.MEMBER_ROLE_ID);
            Set<String> memberList = handler.getAllGroupMembers(QueryConstants.GROUP_NAME);
            assertEquals(memberList.size(), 2);
            assertTrue(memberList.contains(user.getUserName()));
            assertTrue(memberList.contains(userTwo.getUserName()));
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userTwoId);
            handler.doUpdateQuery(query);
            handler.removeMember(QueryConstants.GROUP_NAME, QueryConstants.USERNAME);
            handler.removeMember(QueryConstants.GROUP_NAME, QueryConstants.INVALID_USERNAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testGetMyUsersSuccess() {
        long userId = -1L;
        long userTwoId = -1L;
        long circleId = -1L;
        try {
            User user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            User userTwo = handler.createUser(QueryConstants.INVALID_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            userId = user.getUserID();
            userTwoId = userTwo.getUserID();

            circleId = handler.addUserToCircle(user.getUserName(), userTwo.getUserName());

            List<User> usersList = handler.getMyUsers(user.getUserName());
            assertEquals(1, usersList.size());
            assertEquals(usersList.get(0).getUserName(), userTwo.getUserName());
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userTwoId);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.CIRCLES_TABLE, DBConstants.CIRCLE_ID, circleId);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testDeleteGroup() {
        User user1 = null;
        User user2 = null;
        long user1Id = -1L;
        long user2Id = -1L;
        try {
            user1 = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.SENDER_USERNAME);
            user2 = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.RECEIVER_USERNAME);
            user1Id = user1.getUserID();
            user2Id = user2.getUserID();
            handler.createGroup(user1.getUserName(), QueryConstants.GROUP_2_NAME);
            handler.addGroupMember(user2.getUserName(), QueryConstants.GROUP_2_NAME, DBConstants.GROUP_INFO_USER_ROLE_MEMBER);
            assertEquals(user1.getUserName(), handler.getGroupModerators(QueryConstants.GROUP_2_NAME).get(0));
            assertEquals(2, handler.getAllGroupMembers(QueryConstants.GROUP_2_NAME).size());
            handler.deleteGroup(user1.getUserName(), QueryConstants.GROUP_2_NAME);

            assertEquals(0, handler.getAllGroupMembers(QueryConstants.GROUP_2_NAME).size());
            assertEquals((long) user1.getUserID(), handler.getUserID(user1.getUserName()));
            assertEquals((long) user2.getUserID(), handler.getUserID(user2.getUserName()));
        } finally {
            //teardown
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user1Id);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user2Id);
            handler.doUpdateQuery(query);
            assertEquals("", handler.getUserName(user1Id));
            assertEquals("", handler.getUserName(user2Id));
        }
    }
    
    @Test
    public void testUpdateUserVisibility() {
    		User user = null;
    		long userId = -1L;
        try {
            user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.USERNAME);
            userId = user.getUserID();

            List<User> userList = handler.getAllUsers();
            int sizeBefore = userList.size();
            handler.updateUserVisibility(QueryConstants.USERNAME, true);
            userList = handler.getAllUsers();
            assertEquals(sizeBefore - 1L, userList.size());
            handler.updateUserVisibility(QueryConstants.USERNAME, false);
        } finally {
        		//teardown
        		String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testUpdateGroupVisibility() {
    		User user = null;
    		long userId = -1L;
        try {
            user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.USERNAME);
            userId = user.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            List<Group> groupList = handler.getAllGroups();
            int sizeBefore = groupList.size();
            handler.updateGroupVisibility(QueryConstants.GROUP_NAME, true);
            groupList = handler.getAllGroups();
            assertEquals(sizeBefore - 1L, groupList.size());
            handler.updateGroupVisibility(QueryConstants.GROUP_NAME, false);
        } finally {
        		//teardown
        		String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            handler.removeMember(QueryConstants.GROUP_NAME, QueryConstants.USERNAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

}
