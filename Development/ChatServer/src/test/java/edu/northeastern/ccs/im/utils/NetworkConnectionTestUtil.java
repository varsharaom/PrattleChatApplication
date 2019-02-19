package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.constants.MessageConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NetworkConnectionTestUtil {

    private NetworkConnectionTestUtil() {

    }

    public static Iterator<Message> getMessageIterator() {

        return new Iterator<Message>() {
            Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE);
            Message message2 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE);
            List<Message> messageList = new ArrayList<>(Arrays.asList(message1, message2));
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < messageList.size();
            }

            @Override
            public Message next() {
                return messageList.get(index++);
            }
        };
    }

    public static Iterator<Message> getMessageIteratorWithNoUsers() {
        return new Iterator<Message>() {
            Message message1 = Message.makeBroadcastMessage(null,
                    MessageConstants.BROADCAST_TEXT_MESSAGE);
            List<Message> messageList = new ArrayList<>(Arrays.asList(message1));
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < messageList.size();
            }

            @Override
            public Message next() {
                return messageList.get(index++);
            }
        };
    }
}
