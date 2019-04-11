package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.utils.ClientRunnableHelperUtil;
import edu.northeastern.ccs.im.utils.MessageUtil;
import edu.northeastern.ccs.im.utils.QueryHandlerUtil;
import edu.northeastern.ccs.serverim.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageParserTest {

	public static String MESSAGE_CONTENT_EMPTY = "Constructed message content is empty";
	
    @InjectMocks
    private ClientRunnableHelper clientRunnableHelper;

    @Mock
    private IQueryHandler queryHandler;

    @Test
    public void testParseValidRegisterMessage() {
        Message message = MessageUtil.getValidRegisterBroadcastMessage();
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isRegisterMessage());
        assertNotNull(MESSAGE_CONTENT_EMPTY, message.getText());

        String[] content = message.getText().split(" ");
        assertEquals(3, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidRegisterMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);

        assertEquals(constructedMessage.getText(), content[2]);
    }

    @Test
    public void testParseValidLoginMessage() {
        Message message = MessageUtil.getValidLoginBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isLoginMessage());
        assertNotNull(MESSAGE_CONTENT_EMPTY, message.getText());

        String[] content = message.getText().split(" ");
        assertEquals(3, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidLoginMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);

        assertEquals(constructedMessage.getText(), content[2]);
    }

    @Test
    public void testParseValidDirectMessage() {
        Message message = MessageUtil.getValidDirectBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isDirectMessage());
        assertNotNull(MESSAGE_CONTENT_EMPTY, message.getText());

        String[] content = message.getText().split(" ", 5);
        assertEquals(5, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidDirectMessageIdentifer(content[0]));
        assertEquals(content[1], constructedMessage.getName());
        assertEquals(content[2], constructedMessage.getMsgReceiver());
        assertEquals(Integer.parseInt(content[3]), constructedMessage.getTimeOutMinutes());
        assertTrue(constructedMessage.getText().contains(content[4]));
    }

    @Test
    public void testParseValidGroupMessage() {
        Message message = MessageUtil.getValidGroupBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isGroupMessage());
        assertNotNull(MESSAGE_CONTENT_EMPTY, message.getText());

        String[] content = message.getText().split(" ", 5);
        assertEquals(5, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidGroupMessageIdentifer(content[0]));
        assertEquals(content[1], constructedMessage.getName());
        assertEquals(content[2], constructedMessage.getMsgReceiver());
        assertEquals(Integer.parseInt(content[3]), constructedMessage.getTimeOutMinutes());
        assertTrue(constructedMessage.getText().contains(content[4]));
    }

    @Test
    public void testParseValidDeleteMessage() {
        Message message = MessageUtil.getValidDeleteBroadcastMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isDeleteMessage());

        String[] content = message.getText().split(" ", 4);
        assertEquals(4, content.length);

        assertTrue(ClientRunnableHelperUtil.isValidDeleteMessageIdentifer(content[0]));
        assertEquals(constructedMessage.getName(), content[1]);
        assertEquals(constructedMessage.getMsgReceiver(), content[2]);
        assertEquals(constructedMessage.getId(), Long.parseLong(content[3]));
    }

    @Test
    public void testValidGetAllUsersMessage() {
        Message message = MessageUtil.getValidGetUsersMessage();

        when(queryHandler.getAllUsers()).thenReturn(QueryHandlerUtil.getUsers());
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isGetInfoMessage());
        assertTrue(constructedMessage.getText().startsWith(MessageConstants.GET_USERS_CONSOLE_INFO));
    }

    @Test
    public void testValidGetAllGroupsMessage() {
        Message message = MessageUtil.getValidGetGroupsMessage();

        when(queryHandler.getAllGroups()).thenReturn(QueryHandlerUtil.getGroups());
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isGetInfoMessage());
        assertTrue(constructedMessage.getText().startsWith(MessageConstants.GET_GROUPS_CONSOLE_INFO));
    }

    @Test
    public void testValidGetMyMessages() {
        Message message = MessageUtil.getValidGetMyUsersMessage();

        when(queryHandler.getMyUsers(message.getName())).thenReturn(QueryHandlerUtil.getUsers());
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isGetInfoMessage());
        assertTrue(constructedMessage.getText().startsWith(MessageConstants.GET_MY_USERS_CONSOLE_INFO));
    }

    @Test
    public void testValidGetMyGroupsMessage() {
        Message message = MessageUtil.getValidGetMyGroupsMessage();

        when(queryHandler.getMyGroups(message.getName())).thenReturn(QueryHandlerUtil.getGroups());
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isGetInfoMessage());
        assertTrue(constructedMessage.getText().startsWith(MessageConstants.GET_MY_GROUPS_CONSOLE_INFO));
    }

    @Test
    public void testValidForwardDirectMessage() {
        Message message = MessageUtil.getValidForwardDirectMessage();

        when(queryHandler.getMessage(anyLong())).thenReturn(QueryHandlerUtil.getValidDirectMessage());
        when(queryHandler.getParentMessageID(anyLong())).thenReturn(1L);
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isDirectMessage());
        assertEquals("Forwarded messages must have a parent Id set",
                1L, constructedMessage.getId());
    }

    @Test
    public void testValidForwardGroupMessage() {
        Message message = MessageUtil.getValidForwardDirectMessage();

        when(queryHandler.getMessage(anyLong())).thenReturn(QueryHandlerUtil.getValidGroupMessage());
        when(queryHandler.getParentMessageID(anyLong())).thenReturn(1L);
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isGroupMessage());
        assertEquals("Forwarded messages must have a parent Id set",
                1L, constructedMessage.getId());
    }

    @Test
    public void testGroupCreateMessage() {
        Message message = MessageUtil.getValidGroupCreateMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.GROUP_CREATE_IDENTIFIER, contents[0]);
    }

    @Test
    public void testGroupDeleteMessage() {
        Message message = MessageUtil.getValidGroupDeleteMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.GROUP_DELETE_IDENTIFIER, contents[0]);
    }

    @Test
    public void testAddModeratorMessage() {
        Message message = MessageUtil.getValidAddModeratorMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.GROUP_ADD_MODERATOR, contents[0]);
    }

    @Test
    public void testRemoveGroupMemberMessage() {
        Message message = MessageUtil.getValidRemoveMemberMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.GROUP_REMOVE_MEMBER_IDENTIFIER, contents[0]);
    }

    @Test
    public void testAddGroupMemberMessage() {
        Message message = MessageUtil.getValidAddMemberMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.GROUP_ADD_MEMBER_IDENTIFIER, contents[0]);
    }

    @Test
    public void testLeaveGroupMessage() {
        Message message = MessageUtil.getValidLeaveGroupMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.LEAVE_GROUP_IDENTIFIER, contents[0]);
    }

    @Test
    public void testRequestGroupAddMessage() {
        Message message = MessageUtil.getValidRequestGroupAddMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        String[] contents = constructedMessage.getText().split(" ");

        assertTrue(constructedMessage.isActionMessage());
        assertEquals(MessageConstants.REQUEST_GROUP_ADD_IDENTIFIER, contents[0]);
    }

    @Test
    public void testInvalidGetInfoMessage() {
        Message message = MessageUtil.getInvalidGetInfoMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.getText().endsWith(MessageConstants.INVALID_GROUP_INFO_ERR));
    }

    @Test
    public void testValidGroupSubsetMessage() {
        Message message = MessageUtil.getValidGroupSubsetMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        assertTrue(constructedMessage.isGroupSubsetMessage());
        assertEquals("senderName", constructedMessage.getName());
        assertEquals("groupName", constructedMessage.getMsgReceiver());
        assertEquals(3, constructedMessage.getReceivers().size());
        assertEquals(14, constructedMessage.getTimeOutMinutes());
        assertNotNull(constructedMessage.getText());
    }

    @Test
    public void testTrackMessage() {
        Message message = MessageUtil.getValidTrackMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isActionMessage());;
    }

    @Test
    public void testChangeGroupVisibilityToPrivate() {
        Message message = MessageUtil.getValidGroupVisibilityToPrivateMessage();

        when(queryHandler.isGroupInVisible(anyString())).thenReturn(false);
        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isActionMessage());
    }


    @Test
    public void testUserVisibilityChangeMessage() {
        Message message = MessageUtil.getValidUserVisibilityChangeMessage();

        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isActionMessage());;
    }
}
