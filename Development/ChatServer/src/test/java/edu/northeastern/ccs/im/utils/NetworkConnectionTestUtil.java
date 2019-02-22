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
				if (hasNext()) {
					return messageList.get(index++);
				} else {
					throw new NoSuchElementException();
				}
			}
		};
	}

	public static Iterator<Message> getMessageIteratorWithNoUsers() {
		return new Iterator<Message>() {
			Message message1 = Message.makeBroadcastMessage(null, MessageConstants.BROADCAST_TEXT_MESSAGE);
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
				if (hasNext()) {
					return messageList.get(index++);
				} else {
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
				if (hasNext()) {
					return messageList.get(index++);
				} else {
					throw new NoSuchElementException();
				}

			}
		};
	}

	public static Iterator<Message> getMessageIteratorWithManyMessages() {
		return new Iterator<Message>() {
			Message message5 = Message.makeSimpleLoginMessage(MessageConstants.FIRST_USER);
			Message message4 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
					MessageConstants.BROADCAST_TEXT_MESSAGE);
			Message message3 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
					MessageConstants.BROADCAST_TEXT_MESSAGE);
			Message message2 = Message.makeBroadcastMessage(null, MessageConstants.BROADCAST_TEXT_MESSAGE);
			Message message1 = Message.makeQuitMessage(MessageConstants.SIMPLE_USER);
			Message message0 = Message.makeSimpleLoginMessage(MessageConstants.FIRST_USER);

			List<Message> messageList = new ArrayList<>(
					Arrays.asList(message5, message4, message3, message2, message1, message0));
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

	public static Iterator<Message> getMessageIteratorWithMultipleQuitMessages() {
		return new Iterator<Message>() {

			Message message1 = Message.makeQuitMessage(MessageConstants.SIMPLE_USER);

			Message message2 = Message.makeQuitMessage(MessageConstants.SIMPLE_USER);

			Message message3 = Message.makeQuitMessage(MessageConstants.SIMPLE_USER);
			List<Message> messageList = new ArrayList<>(Arrays.asList(message1, message2, message3));
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

	public static Iterator<Message> getMessageIteratorWithNoMessages() {
		return new Iterator<Message>() {

			List<Message> messageList = new ArrayList<>();
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
	
	public static Iterator<Message> getMessageIteratorWithNullAndNonNullMessages() {

		return new Iterator<Message>() {
			Message message1 = Message.makeBroadcastMessage(MessageConstants.SIMPLE_USER,
					MessageConstants.BROADCAST_TEXT_MESSAGE);
			List<Message> messageList = new ArrayList<>(Arrays.asList(message1, null));
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
