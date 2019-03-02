package edu.northeastern.ccs.im;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user and stores their information.
 */
public class User {
    private final Long userID;
    private final String userName;
    private final String nickName;
    private Set<User> connections;

    public User(Long userID, String userName, String nickName) {
        this.userID = userID;
        this.userName = userName;
        this.nickName = nickName;
        connections = new HashSet<>();
    }

    public Long getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickName() {
        return nickName;
    }

    public Set<User> getConnections() {
        return connections;
    }
}
