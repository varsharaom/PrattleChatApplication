package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.constants.MessageConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.utils.MessageUtil;
import edu.northeastern.ccs.serverim.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionMessageHandlersTest {

    @InjectMocks
    ClientRunnableHelper clientRunnableHelper;

    @Mock
    IQueryHandler queryHandler;

    @Test
    public void testCreateGroup() {
        Message message = MessageUtil.getValidGroupCreateMessage();
        Message groupCreateMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.createGroup(anyString(), anyString())).thenReturn(1l);

        clientRunnableHelper.handleMessages(groupCreateMessage);
    }

    @Test
    public void testDeleteGroupByModerator() {
        Message message = MessageUtil.getValidGroupDeleteMessage();
        Message groupDeleteMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).deleteGroup(anyString(), anyString());

        clientRunnableHelper.handleMessages(groupDeleteMessage);
    }

    @Test
    public void testDeleteGroupByNonModerator() {
        Message message = MessageUtil.getValidGroupDeleteMessage();
        Message groupDeleteMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(false);
        doNothing().when(queryHandler).deleteGroup(anyString(), anyString());

        clientRunnableHelper.handleMessages(groupDeleteMessage);
    }

    @Test
    public void testCreateModeratorHappyPath() {
        Message message = MessageUtil.getValidAddModeratorMessage();
        Message addModeratorMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).makeModerator(anyString(), anyString());

        clientRunnableHelper.handleMessages(addModeratorMessage);
    }

    @Test
    public void testCreateInvalidGroupMemberAsModeratorByModerator() {
        Message message = MessageUtil.getValidAddModeratorMessage();
        Message addModeratorMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(false);

        clientRunnableHelper.handleMessages(addModeratorMessage);
    }

    @Test
    public void testCreateModeratorByNonModerator() {
        Message message = MessageUtil.getValidAddModeratorMessage();
        Message addModeratorMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(false);
        clientRunnableHelper.handleMessages(addModeratorMessage);
    }

    @Test
    public void testLeaveGroupHappyPath() {
        Message message = MessageUtil.getValidLeaveGroupMessage();
        Message leaveGroupMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).removeMember(anyString(), anyString());

        clientRunnableHelper.handleMessages(leaveGroupMessage);

    }

    @Test
    public void testLeaveGroupInvalidGroupMember() {
        Message message = MessageUtil.getValidLeaveGroupMessage();
        Message leaveGroupMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(false);
        doNothing().when(queryHandler).removeMember(anyString(), anyString());

        clientRunnableHelper.handleMessages(leaveGroupMessage);

    }

    @Test
    public void testAddMemberToGroupHappyPath() {
        Message message = MessageUtil.getValidAddMemberMessage();
        Message addMemberMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        when(queryHandler.checkUserNameExists(anyString())).thenReturn(true);
        when(queryHandler.addGroupMember(anyString(), anyString(), anyByte())).thenReturn(1l);

        clientRunnableHelper.handleMessages(addMemberMessage);
    }

    @Test
    public void testAddMemberByNonModerator() {
        Message message = MessageUtil.getValidAddMemberMessage();
        Message addMemberMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(false);
        clientRunnableHelper.handleMessages(addMemberMessage);
    }

    @Test
    public void testAddInvalidMemberToGroup() {
        Message message = MessageUtil.getValidAddMemberMessage();
        Message addMemberMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        when(queryHandler.checkUserNameExists(anyString())).thenReturn(false);

        clientRunnableHelper.handleMessages(addMemberMessage);
    }

    @Test
    public void testRemoveMemberToGroupHappyPath() {
        Message message = MessageUtil.getValidRemoveMemberMessage();
        Message addMemberMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).removeMember(anyString(), anyString());

        clientRunnableHelper.handleMessages(addMemberMessage);
    }

    @Test
    public void testRemoveMemberByNonModerator() {
        Message message = MessageUtil.getValidRemoveMemberMessage();
        Message addMemberMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(false);

        clientRunnableHelper.handleMessages(addMemberMessage);
    }

    @Test
    public void testRemoveInvalidGroupMember() {
        Message message = MessageUtil.getValidRemoveMemberMessage();
        Message addMemberMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(false);

        clientRunnableHelper.handleMessages(addMemberMessage);
    }


    @Test
    public void testGroupAddRequestHappyPath() {
        Message message = MessageUtil.getValidRequestGroupAddMessage();
        Message groupAddRequestMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(true);
        when(queryHandler.checkUserNameExists(anyString())).thenReturn(true);
        clientRunnableHelper.handleMessages(groupAddRequestMessage);
    }

    @Test
    public void testGroupAddRequestForInvalidMember() {
        Message message = MessageUtil.getValidRequestGroupAddMessage();
        Message groupAddRequestMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(true);
        when(queryHandler.checkUserNameExists(anyString())).thenReturn(false);
        clientRunnableHelper.handleMessages(groupAddRequestMessage);
    }

    @Test
    public void testGroupAddRequestByInvalidGroupMember() {
        Message message = MessageUtil.getValidRequestGroupAddMessage();
        Message groupAddRequestMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(false);
        clientRunnableHelper.handleMessages(groupAddRequestMessage);
    }

    @Test
    public void testInvalidActionType() {
        Message message = MessageUtil.getInvalidActionTypeMessage();
        Message invalidActionMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        clientRunnableHelper.handleMessages(invalidActionMessage);
    }

    @Test
    public void testMessageGroupSubsetHappyPath() {
        Message message = MessageUtil.getValidGroupSubsetMessage();
        Message validGroupSubsetMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        List<String> users = validGroupSubsetMessage.getReceivers();
        when(queryHandler.getGroupMembers(anyString())).thenReturn(users);
        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(true);

        clientRunnableHelper.handleMessages(validGroupSubsetMessage);
    }

    @Test
    public void testMessagesGroupSubsetInvalidReceivers() {
        Message message = MessageUtil.getValidGroupSubsetMessage();
        Message validGroupSubsetMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        List<String> users = Arrays.asList("user1", "user2");
        when(queryHandler.getGroupMembers(anyString())).thenReturn(users);
        when(queryHandler.isGroupMember(anyString(), anyString())).thenReturn(false);

        clientRunnableHelper.handleMessages(validGroupSubsetMessage);
    }

    @Test
    public void testTrackMessageHappyPath() {
        Message message = MessageUtil.getValidTrackMessage();
        Message validGroupSubsetMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        Message trackMsg = Message.makeBroadcastMessage("senderName", "");
        Map<String, List<String>> map = new HashMap<>();
        map.put(MessageConstants.FORWARDED_GROUPS, Arrays.asList("group1", "group2"));
        map.put(MessageConstants.FORWARDED_USERS, Arrays.asList("user1", "user2"));

        when(queryHandler.trackMessage(anyLong())).thenReturn(map);
        when(queryHandler.getMessage(anyLong())).thenReturn(trackMsg);
        clientRunnableHelper.handleMessages(validGroupSubsetMessage);
    }

    @Test
    public void testTrackMessageByNonOriginator() {
        Message message = MessageUtil.getValidTrackMessage();
        Message validGroupSubsetMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        Message trackMsg = Message.makeBroadcastMessage("nonOriginator", "");
        Map<String, List<String>> map = new HashMap<>();
        map.put(MessageConstants.FORWARDED_GROUPS, Arrays.asList("group1", "group2"));
        map.put(MessageConstants.FORWARDED_USERS, Arrays.asList("user1", "user2"));

        when(queryHandler.trackMessage(anyLong())).thenReturn(map);
        when(queryHandler.getMessage(anyLong())).thenReturn(trackMsg);
        clientRunnableHelper.handleMessages(validGroupSubsetMessage);
    }

    @Test
    public void testChangeGroupVisibilityToPrivate() {
        Message message = MessageUtil.getValidGroupVisibilityToPrivateMessage();

        Message validGroupVisibilityChangeMessage = clientRunnableHelper
                .getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).updateGroupVisibility(anyString(), anyBoolean());

        clientRunnableHelper.handleMessages(validGroupVisibilityChangeMessage);
    }

    @Test
    public void testChangeGroupVisibilityByNonModerator() {
        Message message = MessageUtil.getValidGroupVisibilityToPrivateMessage();

        Message validGroupVisibilityChangeMessage = clientRunnableHelper
                .getCustomConstructedMessage(message);

        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(false);

        clientRunnableHelper.handleMessages(validGroupVisibilityChangeMessage);
    }

    @Test
    public void testChangeGroupVisibilityToPublic() {
        Message message = MessageUtil.getValidGroupVisibilityToPublicMessage();
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupInVisible(anyString())).thenReturn(true);
        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).updateGroupVisibility(anyString(), anyBoolean());;

        clientRunnableHelper.handleMessages(constructedMessage);
    }

    @Test
    public void testInvalidGroupVisibilityChangeBothPrivate() {
        Message message = MessageUtil.getValidGroupVisibilityToPrivateMessage();
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);

        when(queryHandler.isGroupInVisible(anyString())).thenReturn(true);
        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(true);
        doNothing().when(queryHandler).updateGroupVisibility(anyString(), anyBoolean());;

        clientRunnableHelper.handleMessages(constructedMessage);
    }


    @Test
    public void testChangeGroupVisibilityByInvalidModerator() {
        Message message = MessageUtil.getValidGroupVisibilityToPublicMessage();

        when(queryHandler.isGroupInVisible(anyString())).thenReturn(true);
        when(queryHandler.isModerator(anyString(), anyString())).thenReturn(false);
        Message constructedMessage = clientRunnableHelper.getCustomConstructedMessage(message);
        assertTrue(constructedMessage.isActionMessage());
    }

    @Test
    public void testValidChangeUserVisibilityToPrivate() {
        Message message = MessageUtil.getValidUserVisibilityChangeMessage();

        Message validGroupVisibilityChangeMessage = clientRunnableHelper
                .getCustomConstructedMessage(message);
        when(queryHandler.isUserInVisible(anyString())).thenReturn(false);
        doNothing().when(queryHandler).updateUserVisibility(anyString(), anyBoolean());

        clientRunnableHelper.handleMessages(validGroupVisibilityChangeMessage);
    }

    @Test
    public void testInvalidChangeUserVisibilityBothPrivate() {
        Message message = MessageUtil.getValidUserVisibilityChangeMessage();

        Message validGroupVisibilityChangeMessage = clientRunnableHelper
                .getCustomConstructedMessage(message);
        when(queryHandler.isUserInVisible(anyString())).thenReturn(true);
        doNothing().when(queryHandler).updateUserVisibility(anyString(), anyBoolean());

        clientRunnableHelper.handleMessages(validGroupVisibilityChangeMessage);
    }
}

