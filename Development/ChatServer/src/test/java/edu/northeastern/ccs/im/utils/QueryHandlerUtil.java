package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.serverim.Group;
import edu.northeastern.ccs.serverim.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class QueryHandlerUtil {

    public static List<User> getUsers() {
        User user1 = new User(1L, "user1", "nick1", Calendar.getInstance().getTimeInMillis());
        User user2 = new User(2L, "user2", "nick2", Calendar.getInstance().getTimeInMillis());

        return Arrays.asList(user1, user2);
    }

    public static List<Group> getGroups() {
        Group group1 = new Group(1L, "group1");
        Group group2 = new Group(2L, "group2");

        return Arrays.asList(group1, group2);
    }
}
