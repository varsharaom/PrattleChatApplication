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

    public boolean checkGroupNameExists(String groupName);

    public List<Group> getAllGroups();

    public  List<Group> getMyGroups(String senderName);

}
