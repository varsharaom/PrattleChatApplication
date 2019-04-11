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
import java.util.concurrent.TimeUnit;

import edu.northeastern.ccs.im.constants.MessageConstants;
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
            res = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
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
            msgId = handler.storeMessage(sender.getUserName(), QueryConstants.RECEIVER_USERNAME, MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);

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
            msgId = handler.storeMessage(sender.getUserName(), QueryConstants.GROUP_2_NAME, MessageType.GROUP,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);

            receiverId = receiver.getUserID();
            senderId = sender.getUserID();
            List<Message> messages = handler.getMessagesSinceLastLogin(receiver.getUserID());
            assertEquals(QueryConstants.MESSAGE_TEXT, messages.get(0).getText());
        } finally {
            // Tear down

            handler.deleteGroup(QueryConstants.SENDER_USERNAME, QueryConstants.GROUP_2_NAME);

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
            id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
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
            id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
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
        long id = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME,
                MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
        handler.deleteMessage(id);
        Message res = handler.getMessage(id);
        assertEquals(1, res.getIsDeleted());
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
            assertEquals(QueryConstants.USERNAME, members.get(0));
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
            assertEquals(QueryConstants.USERNAME, moderators.get(0));
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
            assertEquals(QueryConstants.USERNAME, moderators.get(0));
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

            assertEquals("", groupName);
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

            assertEquals(-1L, res);
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
            assertEquals(handler.getAllGroups().size(), size + 1L);
        } finally {
            //teardown
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }

    }

    @Test
    public void testGetGroupsForSpecificUser() {
        User user1 = null;
        long userId = 0;
        try {
            user1 = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.SENDER_USERNAME);
            userId = user1.getUserID();
            handler.createGroup(user1.getUserName(), QueryConstants.GROUP_2_NAME);
            assertEquals(user1.getUserName(), handler.getGroupModerators(QueryConstants.GROUP_2_NAME).get(0));
            assertEquals(1, handler.getAllGroupMembers(QueryConstants.GROUP_2_NAME).size());
            assertEquals(1, handler.getMyGroups(user1.getUserName()).size());
        } finally {
            //teardown
            handler.deleteGroup(QueryConstants.SENDER_USERNAME, QueryConstants.GROUP_2_NAME);
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
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
            msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();

            List<Message> messageList = handler.getMessagesSentByUser(userOne.getUserID(), MessageType.DIRECT, 0, 1);
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());
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
    public void testGetAllMessagesSentByUser() {
        long msgOneId = 0;
        long msgTwoId = 0;
        long userOneId = 0;
        long userTwoId = 0;
        try {
            User userOne = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            User userTwo = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();

            List<Message> messageList = handler.getMessagesSentByUser(userOne.getUserID(), MessageType.DIRECT, 0, -1);
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());
            assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(1).getText());
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
            msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();

            List<Message> messageList = handler.getMessagesSentToUser(userTwo.getUserID(), MessageType.DIRECT, 0, 1);
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());
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
    public void testGetAllMessagesSentToUser() {
        long msgOneId = 0;
        long msgTwoId = 0;
        long userOneId = 0;
        long userTwoId = 0;
        try {
            User userOne = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            User userTwo = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();

            List<Message> messageList = handler.getMessagesSentToUser(userTwo.getUserID(), MessageType.DIRECT, 0, -1);
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());
            assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(1).getText());
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
            msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgThreeId = handler.storeMessage(userOne.getUserName(), userThree.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();
            userThreeId = userThree.getUserID();

            List<Message> messageList = handler.getMessagesFromUserChat(userOne.getUserName(), userTwo.getUserName(), 0, 1);
            assertEquals(1, messageList.size());
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());
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
    public void testGetMessagesFromGroupChat() {
        long msgOneId = 0;
        long msgTwoId = 0;
        long msgThreeId = 0;
        long userOneId = 0;
        long userTwoId = 0;
        long groupId = 0;
        try {
            User userOne = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            User userTwo = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.NICKNAME);
            groupId = handler.createGroup(userOne.getUserName(), QueryConstants.GROUP_NAME);
            handler.addGroupMember(QueryConstants.RECEIVER_USERNAME, QueryConstants.GROUP_NAME, QueryConstants.MEMBER_ROLE_ID);

            msgOneId = handler.storeMessage(userOne.getUserName(), QueryConstants.GROUP_NAME, MessageType.GROUP,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), QueryConstants.GROUP_NAME, MessageType.GROUP,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();

            List<Message> messageList = handler.getMessagesFromGroupChat(QueryConstants.GROUP_NAME, 0, 1);
            assertEquals(1, messageList.size());
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());

            messageList = handler.getMessagesFromGroupChat(QueryConstants.GROUP_NAME, 1, 1);
            assertEquals(1, messageList.size());
            assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(0).getText());

            messageList = handler.getMessagesFromGroupChat(QueryConstants.GROUP_NAME, 2, 1);
            assertEquals(0, messageList.size());
        } finally {
            // Tear down
            handler.removeGroupMember(QueryConstants.RECEIVER_USERNAME, QueryConstants.GROUP_NAME);
            handler.removeGroupMember(QueryConstants.SENDER_USERNAME, QueryConstants.GROUP_NAME);
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
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.GROUP_TABLE, DBConstants.GROUP_ID, groupId);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testGetAllMessagesFromUserChat() {
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
            msgOneId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgTwoId = handler.storeMessage(userOne.getUserName(), userTwo.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            msgThreeId = handler.storeMessage(userOne.getUserName(), userThree.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_SECOND_TEXT, System.currentTimeMillis(), 0);
            userOneId = userOne.getUserID();
            userTwoId = userTwo.getUserID();
            userThreeId = userThree.getUserID();

            List<Message> messageList = handler.getMessagesFromUserChat(userOne.getUserName(), userTwo.getUserName(), 0, -1);
            assertEquals(2, messageList.size());
            assertEquals(QueryConstants.MESSAGE_SECOND_TEXT, messageList.get(0).getText());
            assertEquals(QueryConstants.MESSAGE_TEXT, messageList.get(1).getText());
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
            assertEquals(2, memberList.size());
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

    @Test
    public void testParentMessageID() {
        long res1 = 0;
        long res2 = 0;
        try {
            res1 = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            assertEquals(res1, (long) handler.getParentMessageID(res1));
            res2 = handler.storeMessage(QueryConstants.RECEIVER_USERNAME, QueryConstants.SENDER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, res1, System.currentTimeMillis(), 0);
            assertEquals(res1, (long) handler.getParentMessageID(res2));
            assertNotEquals(res1, 0);
            assertNotEquals(res2, 0);
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res1);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res2);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testStoreForwardedMessage() {
        long res1 = 0;
        long res2 = 0;
        try {
            res1 = handler.storeMessage(QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            res2 = handler.storeMessage(QueryConstants.RECEIVER_USERNAME, QueryConstants.SENDER_USERNAME,
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, res1, System.currentTimeMillis(), 0);
            assertNotEquals(res1, 0);
            assertNotEquals(res2, 0);
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res1);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res2);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testTrackMessage() {
        long res1 = 0;
        long res2 = 0;
        User sender = null;
        User receiver = null;
        long senderId = 0;
        long receiverId = 0;

        try {
            sender = handler.createUser(QueryConstants.SENDER_USERNAME, QueryConstants.PASS, QueryConstants.SENDER_USERNAME);
            receiver = handler.createUser(QueryConstants.RECEIVER_USERNAME, QueryConstants.PASS, QueryConstants.RECEIVER_USERNAME);

            senderId = sender.getUserID();
            receiverId = receiver.getUserID();
            assertNotEquals(0, senderId);
            assertNotEquals(0, receiverId);

            res1 = handler.storeMessage(sender.getUserName(), receiver.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 0);
            assertNotEquals(0, res1);
            assertEquals(0, handler.trackMessage(res1).get(MessageConstants.FORWARDED_USERS).size());
            res2 = handler.storeMessage(receiver.getUserName(), sender.getUserName(), MessageType.DIRECT,
                    QueryConstants.MESSAGE_TEXT, res1, System.currentTimeMillis(), 0);
            assertNotEquals(0, res2);

            assertEquals(1, handler.trackMessage(res1).get(MessageConstants.FORWARDED_USERS).size());
        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res1);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res2);
            handler.doUpdateQuery(query);

            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, receiverId);
            handler.doUpdateQuery(query);

            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, senderId);
            handler.doUpdateQuery(query);
        }
    }

    @Test
    public void testUserVisibilityStatus() {
        User user = null;
        long userId = -1L;
        try {
            user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.USERNAME);
            userId = user.getUserID();

            List<User> userList = handler.getAllUsers();
            int sizeBefore = userList.size();
            assertFalse(handler.isUserInVisible(user.getUserName()));
            handler.updateUserVisibility(QueryConstants.USERNAME, true);
            assertTrue(handler.isUserInVisible(user.getUserName()));
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
    public void testGroupVisibilityStatus() {
        User user = null;
        long userId = -1L;
        try {
            user = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.USERNAME);
            userId = user.getUserID();
            handler.createGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
            List<Group> groupList = handler.getAllGroups();
            int sizeBefore = groupList.size();
            assertFalse(handler.isGroupInVisible(QueryConstants.GROUP_NAME));
            handler.updateGroupVisibility(QueryConstants.GROUP_NAME, true);
            assertTrue(handler.isGroupInVisible(QueryConstants.GROUP_NAME));
            groupList = handler.getAllGroups();
            assertEquals(sizeBefore - 1L, groupList.size());
            handler.updateGroupVisibility(QueryConstants.GROUP_NAME, false);
            assertFalse(handler.isGroupInVisible(QueryConstants.GROUP_NAME));
        } finally {
            //teardown
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, userId);
            handler.doUpdateQuery(query);
            handler.removeMember(QueryConstants.GROUP_NAME, QueryConstants.USERNAME);
            handler.deleteGroup(QueryConstants.USERNAME, QueryConstants.GROUP_NAME);
        }
    }

    @Test
    public void testTimeOutMessagesFeature() {
        long res1 = 0;
        long res2 = 0;
        User user1 = null;
        long user1Id = -1L;
        User user2 = null;
        long user2Id = -1L;
        try {
            user1 = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.USERNAME);
            user1Id = user1.getUserID();
            user2 = handler.createUser(QueryConstants.USERNAME, QueryConstants.PASS, QueryConstants.USERNAME);
            user2Id = user2.getUserID();
            res1 = handler.storeMessage(user1.getUserName(), user2.getUserName(),
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis() - 1200000, 1);

            assertEquals(0, handler.getMessagesFromUserChat(user1.getUserName(),
                    user2.getUserName(), 0, 1).size());

            res2 = handler.storeMessage(user1.getUserName(), user2.getUserName(),
                    MessageType.DIRECT, QueryConstants.MESSAGE_TEXT, System.currentTimeMillis(), 1);

            assertEquals(1, handler.getMessagesFromUserChat(user1.getUserName(),
                    user2.getUserName(), 0, 1).size());

        } finally {
            // Tear down
            String query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res1);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.MESSAGE_TABLE, DBConstants.MESSAGE_ID, res2);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user1Id);
            handler.doUpdateQuery(query);
            query = String.format(QueryConstants.TEARDOWN_DELETE, DBConstants.USER_TABLE, DBConstants.USER_ID, user2Id);
            handler.doUpdateQuery(query);
        }
    }

}
