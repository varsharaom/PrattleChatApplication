package edu.northeastern.ccs.serverim;

import java.util.HashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class Group.
 */
public class Group {
    
    /** The group id. */
    private final Long id;
    
    /** The group name. */
    private final String name;

    /**
     * Instantiates a new group.
     *
     * @param groupId the group id
     * @param name the name
     */
    public Group(long groupId, String name) {
        this.id = groupId;
        this.name = name;
    }

    /**
     * Gets the group id.
     *
     * @return the group id
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the group name.
     *
     * @return the group name
     */
    public String getName() {
        return name;
    }
}
