package edu.northeastern.ccs.im.client;

import edu.northeastern.ccs.im.constants.BuddyConstants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BuddyTest {

    @Test
    public void testCreateTestBuddy() {
        Buddy buddy = Buddy.makeTestBuddy(BuddyConstants.TEST_BUDDY_NAME);
        assertEquals(BuddyConstants.TEST_BUDDY_NAME, buddy.getUserName());
    }

    @Test
    public void testGetBuddy() {
        Buddy buddy = Buddy.getBuddy(BuddyConstants.TEST_BUDDY_NAME);
        assertEquals(BuddyConstants.TEST_BUDDY_NAME, buddy.getUserName());
    }

    @Test
    public void testGetEmptyBuddy() {
        Buddy buddy = Buddy.getEmptyBuddy(BuddyConstants.TEST_EMPTY_BUDDY_NAME);
        assertEquals(BuddyConstants.TEST_EMPTY_BUDDY_NAME, buddy.getUserName());
        Buddy.removeBuddy(BuddyConstants.TEST_EMPTY_BUDDY_NAME);
    }

}
