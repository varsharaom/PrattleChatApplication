package edu.northeastern.ccs.im.server;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class ClientTimerTest {

    @Test
    public void test() {
        ClientTimer clientTimer = new ClientTimer();
        assertFalse(clientTimer.isBehind());
    }

    @Test
    public void testUpdateAfterActivity() {
        ClientTimer clientTimer = new ClientTimer();
        clientTimer.updateAfterActivity();
        assertFalse(clientTimer.isBehind());
    }

    @Test
    public void testUpdateAfterInitialization() {
        ClientTimer clientTimer = new ClientTimer();
        clientTimer.updateAfterInitialization();
        assertFalse(clientTimer.isBehind());
    }
}
