package edu.northeastern.ccs.im.utils;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.constants.MessageConstants;

import java.util.*;

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
                if(hasNext()) {
                    return messageList.get(index++);
                }
                else {
                    throw new NoSuchElementException();
                }
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
                if(hasNext()) {
                    return messageList.get(index++);
                }
                else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    public static Iterator<Message> getMessageIteratorWithDifferentUsers() {

        return new Iterator<Message>() {
            Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE);
            Message message2 = Message.makeBroadcastMessage(MessageConstants.SECOND_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE);
            List<Message> messageList = new ArrayList<>(Arrays.asList(message1, message2));
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < messageList.size();
            }

            @Override
            public Message next() {
                if(hasNext()) {
                    return messageList.get(index++);
                }
                else {
                    throw new NoSuchElementException();
                }

            }
        };
    }

    public static Iterator<Message> getMessageIteratorWithQuitMessages() {

        return new Iterator<Message>() {
            Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
                    MessageConstants.BROADCAST_TEXT_MESSAGE);
            Message message2 = Message.makeQuitMessage(MessageConstants.SECOND_USER);
            List<Message> messageList = new ArrayList<>(Arrays.asList(message1, message2));
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < messageList.size();
            }

            @Override
            public Message next() {
                if(hasNext()) {
                    return messageList.get(index++);
                }
                else {
                    throw new NoSuchElementException();
                }

            }
        };
    }


}
