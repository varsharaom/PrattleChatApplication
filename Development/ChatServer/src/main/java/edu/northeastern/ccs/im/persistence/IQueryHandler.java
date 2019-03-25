package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.serverim.Group;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

import java.util.List;

public interface IQueryHandler {

    //User Queries
    public User createUser(String userName, String pass, String nickName);

    public int updateUserLastLogin(long userID);

    public long validateLogin(String username, String password);

    //Message Queries
    public long storeMessage(String senderName, String receiverName, MessageType type, String msgText);

    public Message getMessage(long messageID);

    public void deleteMessage(long messageID);

    public List<Message> getMessagesSinceLastLogin(long userID);

    public boolean checkUserNameExists(String name);

    public List<User> getAllUsers();

    public List<User> getMyUsers(String senderName);

    public String getUserName(long userID);

    public long getUserID(String userName);
    
    //Group Queries
    public boolean checkGroupNameExists(String groupName);

    public List<Group> getAllGroups();

    public  List<Group> getMyGroups(String senderName);
    
    public List<String> getGroupMembers(String name);
    
    public List<String> getGroupModerators(String name);
    
    public long createGroup(String name);
    
    public long deleteGroup(String name);
    
    public long addGroupMember(String userName, String groupName, int role);
    
    public long removeGroupMember(String userName, String groupName);
    
    public void changeMemberRole(long userId, long groupId, int role);
    
    public long getGroupID(String groupName);
    
    public String getGroupName(long groupID);
    
    //Message Log Queries
    public List<Message> getMessagesSentByUser(long id, MessageType type);
    
    public List<Message> getMessagesSentToUser(long id, MessageType type);
    
    public List<Message> getMessagesFromUserChat(long senderId, long receiverId);

}
