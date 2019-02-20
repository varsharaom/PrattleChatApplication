package edu.northeastern.ccs.im.server;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.client.IMConnection;
import org.junit.Test;

import static org.junit.Assert.*;

public class PrattleTest {

    @Test
    public void test(){
        Thread thread = new Thread(new MainTest());
        thread.start();
        //Prattle.broadcastMessage(Message.makeBroadcastMessage("test","test"));
        IMConnection connection1 = new IMConnection("localhost",4545, "test");
        connection1.connect();
        IMConnection connection2 = new IMConnection("localhost",4545, "test");
        connection2.connect();
        connection1.sendMessage("Message from C1");
        connection2.sendMessage("Message from C2");
        assertFalse(connection1.getMessageScanner().hasNext());
        Prattle.broadcastMessage(Message.makeBroadcastMessage("test","test"));
        //assertTrue(connection2.getMessageScanner().hasNext());
        Prattle.stopServer();
    }

    class MainTest implements Runnable{

        @Override
        public void run() {
            Prattle.main(new String[0]);
        }
    }



}