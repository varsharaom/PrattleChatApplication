package edu.northeastern.ccs.serverim;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a user and stores their information.
 */
public class Group {
    private final Long id;
    private final String name;

    public Group(long groupId, String name) {
        this.id = groupId;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
