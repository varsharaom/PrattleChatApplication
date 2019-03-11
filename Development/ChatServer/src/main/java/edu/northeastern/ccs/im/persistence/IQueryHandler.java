package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;
import edu.northeastern.ccs.serverim.User;

import java.util.List;

public interface IQueryHandler {

    //User Queries
    public User createUser(String userName, String pass, String nickName);

    public int updateUserLastLogin(User user);

    public List<Long> getCircles(User user);

    public String getPassword(String name);

    //Message Queries
    public long storeMessage(long senderID, long receiverID, MessageType type, String msgText);

    public List<Message> getMessagesSinceLastLogin(User user);

    public boolean checkUserNameExists(String name);

}
