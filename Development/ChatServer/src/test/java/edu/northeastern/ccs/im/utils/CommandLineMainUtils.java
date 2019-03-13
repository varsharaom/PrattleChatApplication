package edu.northeastern.ccs.im.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import edu.northeastern.ccs.im.Message;
import edu.northeastern.ccs.im.constants.ClientStringConstants;
import edu.northeastern.ccs.im.constants.MessageConstants;

public class CommandLineMainUtils {
	private CommandLineMainUtils() {
		
	}
	
	public static Iterator<Message> getMessageIteratorWithDifferentUser() {

		return new Iterator<Message>() {
			Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
					MessageConstants.BROADCAST_TEXT_MESSAGE);
			
			List<Message> messageList = new ArrayList<>(Arrays.asList(message1));
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < messageList.size();
			}

			@Override
			public Message next() {
				if (hasNext()) {
					return messageList.get(index++);
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}

	public static Iterator<Message> getMessageIteratorWithSameUser() {

		return new Iterator<Message>() {
			
			Message message2 = Message.makeBroadcastMessage(ClientStringConstants.TEST_USER, MessageConstants.BROADCAST_TEXT_MESSAGE);
			
			List<Message> messageList = new ArrayList<>(Arrays.asList(message2));
			int index = 0;

			@Override
			public boolean hasNext() {
				return index < messageList.size();
			}

			@Override
			public Message next() {
				if (hasNext()) {
					return messageList.get(index++);
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}
}
