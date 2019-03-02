package edu.northeastern.ccs.im.datahandler;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class handles the user, group and message data from the persistence.
 */
public class EntityHandler {



    public static Set<User> getAllUsers(){
        return new HashSet<>();
    }

    public static boolean createUser(User user){
        //check if the user details already exist
        //if yes don't create and send a false/message
        //else persist information
        return false;
    }

    public static boolean updateUserActivityTimeStamp(User user){
        //stores the users last active time stamp when a user is removed for inactivity.
        return false;
    }

    public static List<Message> getMessageSinceLastActivity(User user){
        //returns all the messages(private, group and broadcasts) for this user since last their last activity.
        return new ArrayList<>();
    }

    //private static Set<Group> getAllGroups(){}

    public static boolean storeMessage(Message message){
        //store message (msg id, sender, receiver(privateid//groupid/broadcastid), messge)
        return false;
    }

}
