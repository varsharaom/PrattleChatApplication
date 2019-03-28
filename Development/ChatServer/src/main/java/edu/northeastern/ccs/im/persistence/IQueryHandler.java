package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.serverim.Group;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

import java.util.List;
import java.util.Set;

/**
 * The Interface IQueryHandler.
 */
public interface IQueryHandler {

    /**
     * Creates the user.
     *
     * @param userName the user name
     * @param pass the password
     * @param nickName the nick name
     * @return the user
     */
    //User Queries
    public User createUser(String userName, String pass, String nickName);

    /**
     * Update user last login.
     *
     * @param userID the user ID
     * @return the user id for the record in the users table
     */
    public int updateUserLastLogin(long userID);

    /**
     * Validate login with username and password.
     *
     * @param username the username
     * @param password the password
     * @return the user id if the given username and password match, else -1
     */
    public long validateLogin(String username, String password);

    /**
     * Store message.
     *
     * @param senderName the sender name
     * @param receiverName the receiver name
     * @param type the message type
     * @param msgText the message text
     * @return the message id
     */
    //Message Queries
    public long storeMessage(String senderName, String receiverName, MessageType type, String msgText);

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
     * Check if user name exists.
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
     * Gets the users in the circle of the requesting user.
     *
     * @param senderName the sender name
     * @return the my users
     */
    public List<User> getMyUsers(String senderName);

    /**
     * Gets the user name from the user ID.
     *
     * @param userID the user ID
     * @return the user name
     */
    public String getUserName(long userID);

    /**
     * Gets the user ID from the user name.
     *
     * @param userName the user name
     * @return the user ID
     */
    public long getUserID(String userName);
    
    /**
     * Check if group name exists.
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
     * Gets all the groups a user is a member of.
     *
     * @param senderName the sender name
     * @return the my groups
     */
    public  List<Group> getMyGroups(String senderName);
    
    /**
     * Gets all the members of a group.
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
     * Creates the group.
     *
     * @param name the group name
     * @return the long
     */
    public long createGroup(String name);
    
    /**
     * Delete group.
     *
     * @param name the group name
     * @return the long
     */
    public long deleteGroup(String name);
    
    /**
     * Add a group member.
     *
     * @param userName the user name
     * @param groupName the group name
     * @param role the role
     * @return the long
     */
    public long addGroupMember(String userName, String groupName, int role);
    
    /**
     * Remove a group member.
     *
     * @param userName the user name
     * @param groupName the group name
     * @return the long
     */
    public long removeGroupMember(String userName, String groupName);
    
    /**
     * Change a group member's role.
     *
     * @param userId the user id
     * @param groupId the group id
     * @param role the role
     */
    public void changeMemberRole(long userId, long groupId, int role);
    
    /**
     * Gets the group ID from group name.
     *
     * @param groupName the group name
     * @return the group ID
     */
    public long getGroupID(String groupName);
    
    /**
     * Gets the group name from group ID.
     *
     * @param groupID the group ID
     * @return the group name
     */
    public String getGroupName(long groupID);
    
    /**
     * Gets the messages sent by a user.
     *
     * @param id the user id
     * @param type the type
     * @return the messages sent by user
     */
    //Message Log Queries
    public List<Message> getMessagesSentByUser(long id, MessageType type);
    
    /**
     * Gets the messages sent to a user.
     *
     * @param id the id
     * @param type the type
     * @return the messages sent to user
     */
    public List<Message> getMessagesSentToUser(long id, MessageType type);
    
    /**
     * Gets the messages from user chat between two users.
     *
     * @param senderId the sender id
     * @param receiverId the receiver id
     * @return the messages from user chat
     */
    public List<Message> getMessagesFromUserChat(long senderId, long receiverId);

    /**
     * Create a group.
     *
     * @param sender the sender
     * @param groupName the group name
     * @return the group id
     */
    long createGroup(String sender, String groupName);

    /**
     * Delete a group.
     *
     * @param sender the sender
     * @param groupName the group name
     */
    void deleteGroup(String sender, String groupName);

    /**
     * Checks if a user is moderator for a group.
     *
     * @param sender the sender
     * @param groupName the group name
     * @return true, if is moderator
     */
    boolean isModerator(String sender, String groupName);

    /**
     * Checks if a user is a member of a group.
     *
     * @param groupName the group name
     * @param sender the sender
     * @return true, if is group member
     */
    boolean isGroupMember(String groupName, String sender);

    /**
     * Make a user the moderator of a group.
     *
     * @param groupName the group name
     * @param toBeModerator the to be moderator
     */
    void makeModerator(String groupName, String toBeModerator);

    /**
     * Removes a user from a group.
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
    
    // Circles
    public long addUserToCircle(String senderName, String receiverName);
}
