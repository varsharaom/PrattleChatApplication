package edu.northeastern.ccs.serverim;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents private conversations and holds information regarding the receiver.
 */
public class PrivateMessage implements IMessage {
    private User receiver;

    public PrivateMessage(User receiver){
        this.receiver = receiver;
    }

    @Override
    public Set<User> getReceiver() {
        return new HashSet<User>() {{
            add(receiver);
        }};
    }
}
