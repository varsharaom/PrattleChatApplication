package edu.northeastern.ccs.serverim;

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
    private long lastSeen;
    private int invisible;

    public User(Long userID, String userName, String nickName, long time, int invisible) {
        this.userID = userID;
        this.userName = userName;
        this.nickName = nickName;
        this.connections = new HashSet<>();
        this.lastSeen = time;
        this.invisible = invisible;
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
    
    public long getLastSeen() {
        return lastSeen;
    }
}
