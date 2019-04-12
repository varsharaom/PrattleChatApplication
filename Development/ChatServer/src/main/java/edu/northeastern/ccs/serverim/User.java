package edu.northeastern.ccs.serverim;

import java.util.HashSet;
import java.util.Set;


/**
 * The Class User.
 */
public class User {

    /**
     * The user ID.
     */
    private final Long userID;

    /**
     * The user name.
     */
    private final String userName;

    /**
     * The nick name.
     */
    private final String nickName;

    /**
     * The connections.
     */
    private Set<User> connections;

    /**
     * The last seen time stamp.
     */
    private long lastSeen;

    /**
     * The invisible.
     */
    private int invisible;

    /**
     * Instantiates a new user.
     *
     * @param userID    the user ID
     * @param userName  the user name
     * @param nickName  the nick name
     * @param time      the time
     * @param invisible the invisible
     */
    public User(Long userID, String userName, String nickName, long time, int invisible) {
        this.userID = userID;
        this.userName = userName;
        this.nickName = nickName;
        this.connections = new HashSet<>();
        this.lastSeen = time;
        this.invisible = invisible;
    }

    /**
     * Gets the user ID.
     *
     * @return the user ID
     */
    public Long getUserID() {
        return userID;
    }

    /**
     * Gets the user name.
     *
     * @return the user name
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the nick name.
     *
     * @return the nick name
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * Gets the connections.
     *
     * @return the connections
     */
    public Set<User> getConnections() {
        return connections;
    }

    /**
     * Gets the last seen.
     *
     * @return the last seen
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * Returns the user visibility status
     *
     * @return visibility status
     */
    public int getInvisible() {
        return invisible;
    }
}
