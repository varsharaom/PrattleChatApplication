package edu.northeastern.ccs.im.persistence;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.MessageType;
import edu.northeastern.ccs.im.User;

import java.util.List;

public interface IQueryHandler {

    //User Queries
    public User createUser(String userName, String pass, String nickName);

    public void updateUserLastLogin(User user);

    public List<Long> getCircles(User user);

    public String getPassword(String name);

    //Message Queries
    public void storeMessage(long senderID, long receiverID, MessageType type, String msgText);

    public List<Message> getMessagesSinceLastLogin(User user);

    public boolean checkUserNameExists(String name);

}
