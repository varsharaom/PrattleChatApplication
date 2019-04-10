package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.serverim.Group;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Interface IQueryHandler.
 */
public interface IQueryHandler {

    /**
     * Creates the user.
     *
     * @param userName the user name
     * @param pass the pass
     * @param nickName the nick name
     * @return the user
     */
    //User Queries
    public User createUser(String userName, String pass, String nickName);

    /**
     * Update user last login.
     *
     * @param userID the user ID
     * @return the int
     */
    public int updateUserLastLogin(long userID);

    /**
     * Validate login.
     *
     * @param username the username
     * @param password the password
     * @return the long
     */
    public long validateLogin(String username, String password);


    /**
     * Checks if the user is invisible for general searches
     *
     * @param userName name of the user
     * @return visibility status
     */
    public boolean isUserInVisible(String userName);

    /**
     * Store message.
     *
     * @param senderName the sender name
     * @param receiverName the receiver name
     * @param type the type
     * @param msgText the msg text
     * @return the long
     */
    //Message Queries
    public long storeMessage(String senderName, String receiverName, MessageType type, String msgText);

    /**
     * Store forwarded message.
     *
     * @param senderName   the sender name
     * @param receiverName the receiver name
     * @param type         the type
     * @param msgText      the msg text
     * @param parentMsgID  id of the parent message
     * @return the long
     */
    public long storeMessage(String senderName, String receiverName, MessageType type, String msgText, Long parentMsgID);


    /**
     * returns all the users and groups who have been forwarded with the given message ID.
     *
     * @param messageID source message id
     * @return Map containing list of group and individual names.
     */
    public Map<String, List<String>> trackMessage(Long messageID);

    /**
     * Update user visibility.
     *
     * @param userName the user name
     * @param makeInVisible the make in visible
     */
    public void updateUserVisibility(String userName, Boolean makeInVisible);
    
    /**
     * Gets the message.
     *
     * @param messageID the message ID
     * @return the message
     */
    public Message getMessage(long messageID);

    /**
     * Delete message.
     *
     * @param messageID the message ID
     */
    public void deleteMessage(long messageID);

    /**
     * Gets the messages since last login.
     *
     * @param userID the user ID
     * @return the messages since last login
     */
    public List<Message> getMessagesSinceLastLogin(long userID);

    /**
     * Check user name exists.
     *
     * @param name the name
     * @return true, if successful
     */
    public boolean checkUserNameExists(String name);

    /**
     * Gets the all users.
     *
     * @return the all users
     */
    public List<User> getAllUsers();

    /**
     * Gets the my users.
     *
     * @param senderName the sender name
     * @return the my users
     */
    public List<User> getMyUsers(String senderName);

    /**
     * Gets the user name.
     *
     * @param userID the user ID
     * @return the user name
     */
    public String getUserName(long userID);

    /**
     * Gets the user ID.
     *
     * @param userName the user name
     * @return the user ID
     */
    public long getUserID(String userName);

    /**
     * Is the given group public or private
     *
     * @param groupName name of the group
     * @return visibility status
     */
    public boolean isGroupInVisible(String groupName);

    /**
     * Check group name exists.
     *
     * @param groupName the group name
     * @return true, if successful
     */
    //Group Queries
    public boolean checkGroupNameExists(String groupName);

    /**
     * Gets the all groups.
     *
     * @return the all groups
     */
    public List<Group> getAllGroups();

    /**
     * Gets the my groups.
     *
     * @param senderName the sender name
     * @return the my groups
     */
    public List<Group> getMyGroups(String senderName);

    /**
     * Gets the group members.
     *
     * @param name the name
     * @return the group members
     */
    public List<String> getGroupMembers(String name);
    
    /**
     * Gets the group moderators.
     *
     * @param name the name
     * @return the group moderators
     */
    public List<String> getGroupModerators(String name);
        
    /**
     * Adds the group member.
     *
     * @param userName the user name
     * @param groupName the group name
     * @param role the role
     * @return the long
     */
    public long addGroupMember(String userName, String groupName, int role);
    
    /**
     * Removes the group member.
     *
     * @param userName the user name
     * @param groupName the group name
     * @return the long
     */
    public long removeGroupMember(String userName, String groupName);
    
    /**
     * Change member role.
     *
     * @param userId the user id
     * @param groupId the group id
     * @param role the role
     */
    public void changeMemberRole(long userId, long groupId, int role);
    
    /**
     * Gets the group ID.
     *
     * @param groupName the group name
     * @return the group ID
     */
    public long getGroupID(String groupName);
    
    /**
     * Gets the group name.
     *
     * @param groupID the group ID
     * @return the group name
     */
    public String getGroupName(long groupID);
    
    /**
     * Gets the messages sent by user.
     *
     * @param id the id
     * @param type the type
     * @return the messages sent by user
     */
    //Message Log Queries
    public List<Message> getMessagesSentByUser(long id, MessageType type, int start, int limit);
    
    /**
     * Gets the messages sent to user.
     *
     * @param id the id
     * @param type the type
     * @return the messages sent to user
     */
    public List<Message> getMessagesSentToUser(long id, MessageType type, int start, int limit);
    
    /**
     * Gets the messages from user chat.
     *
     * @param senderId the sender id
     * @param receiverId the receiver id
     * @return the messages from user chat
     */
    public List<Message> getMessagesFromUserChat(String sender, String receiver, int start, int limit);

    /**
     * Creates the group.
     *
     * @param sender the sender
     * @param groupName the group name
     * @return the long
     */
    long createGroup(String sender, String groupName);

    /**
     * Delete group.
     *
     * @param sender the sender
     * @param groupName the group name
     */
    void deleteGroup(String sender, String groupName);

    /**
     * Checks if is moderator.
     *
     * @param sender the sender
     * @param groupName the group name
     * @return true, if is moderator
     */
    boolean isModerator(String sender, String groupName);

    /**
     * Checks if is group member.
     *
     * @param groupName the group name
     * @param sender the sender
     * @return true, if is group member
     */
    boolean isGroupMember(String groupName, String sender);

    /**
     * Make moderator.
     *
     * @param groupName the group name
     * @param toBeModerator the to be moderator
     */
    void makeModerator(String groupName, String toBeModerator);

    /**
     * Removes the member.
     *
     * @param groupName the group name
     * @param member the member
     */
    void removeMember(String groupName, String member);

    /**
     * Gets the all group members.
     *
     * @param groupName the group name
     * @return the all group members
     */
    Set<String> getAllGroupMembers(String groupName);

    
    /**
     * Update group visibility.
     *
     * @param groupName the group name
     * @param makeInVisible the make in visible
     */
    public void updateGroupVisibility(String groupName, Boolean makeInVisible);
    
    /**
     * Adds the user to circle.
     *
     * @param senderName the sender name
     * @param receiverName the receiver name
     * @return the long
     */
    // Circles
    public long addUserToCircle(String senderName, String receiverName);
}
