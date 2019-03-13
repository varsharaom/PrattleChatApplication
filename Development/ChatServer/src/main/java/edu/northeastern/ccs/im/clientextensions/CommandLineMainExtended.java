package edu.northeastern.ccs.im.clientextensions;

import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.KeyboardScanner;
import edu.northeastern.ccs.im.MessageScanner;
import edu.northeastern.ccs.im.*;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class CommandLineMainExtended {

	/**
	 * Logger instance to display the messages and commands in the console
	 */
	private static Logger logger = Logger.getLogger(CommandLineMainExtended.class.getName());

	private static boolean selected = false;
	private static String msgOpt = "";
	private static String username = "";
	private static String msgReceiver = "";

	/**
	 * This method returns the logger instance created for the client
	 * 
	 * @return the logger instance that belongs to the client.
	 */
	public static Logger getLogger() {
		return logger;
	}

	/**
	 * This method sends the first message that establishes whether the user wants
	 * to send a group message or a private message
	 * 
	 * @param scan
	 *            The keyboard scanner instance that belongs to this client
	 * @param connect
	 *            The IMConnection instance for this client
	 */
	public static void sendFirstMessage(KeyboardScanner scan, IMConnection connect) {

		String txt = scan.nextLine();
        
		String[] parts = txt.split(" ", 3);

		if (parts.length < 3) {
			logger.info(Keywords.ERROR_MSG);

		} else {
			msgOpt = parts[0];
			msgReceiver = parts[1];
			String msgBody = parts[2];
			// check the starting of the message to keep track of whether it is private
			// message or group message

			if (msgOpt.equalsIgnoreCase(Keywords.DRCT_MESSAGE) || msgOpt.equalsIgnoreCase(Keywords.GRP_MESSAGE)) {
				selected = true;

				connect.sendMessage(msgOpt + " " + username + " " + msgReceiver + " " + msgBody);

			} else {
				logger.info(Keywords.ERROR_MSG);
			}

		}

	}

	/**
	 * This method reads a single message from the message queue that is populated
	 * when server sends a message to the client
	 * 
	 * @param mess
	 *            The MessageScanner instance for this client
	 * @param connect
	 *            The IMConnection instance for this client
	 */
	public static void readNewMessages(MessageScanner mess, IMConnection connect) {

		// Get any recent messages received from the IM server.
		if (mess.hasNext()) {

			Message message = mess.next();
			if (!message.getSender().equals(connect.getUserName())) {

				// get the text part of the message
				String messageText = message.getText();

				logger.info(message.getSender() + ": " + MessageParser.getMsgType(messageText));
				logger.info(MessageParser.getMsgBody(messageText));

			}
		}
	}

	/**
	 * This method logs in an existing user or registers a new user and returns the
	 * username
	 * 
	 * @param connect
	 *            IMConnection instance for this client
	 * @param scan
	 *            keyboard scanner instance for this client
	 * @return a string which is the username of the user who just logged
	 *         in/registered
	 */
	public static String logInUser(IMConnection connect, KeyboardScanner scan) {

		// use broadcast message to login or register or send any messages
		boolean fail = true;
		String uname = "";
		do {

			// Check if the user has typed in a line of text to broadcast to the IM server.
			// If there is a line of text to be
			// broadcast:
			if (scan.hasNext()) {

				
				// Read in the text they typed
				String userinput = scan.nextLine();
 
				
				String[] strs = userinput.split(" ");
				if (strs.length != 3) {

					logger.info(Keywords.ERROR_MSG);
				} else {
					switch (strs[0].toUpperCase()) {
					case Keywords.LOGIN:
					case Keywords.REGISTER:
						fail = false;
						uname = strs[1];
						// this is a broadcast message

						connect.sendMessage(userinput);
						break;
					default:
						logger.info(Keywords.ERROR_MSG);
						break;
					}

				}

			}
		} while (fail);

		return uname;
	}

	public static void main(String[] args) {
		BasicConfigurator.configure();
		IMConnection connect;

		do {
			// establish a connection soon after the window is opened

			// Create a Connection to the IM server.
			connect = new IMConnection(args[0], Integer.parseInt(args[1]), Keywords.SAY_HELLO);
		} while (!connect.connect());

		// Create the objects needed to read & write IM messages.
		KeyboardScanner scan = connect.getKeyboardScanner();
		MessageScanner mess = connect.getMessageScanner();

		logger.info(Keywords.LOGIN_MSG);

		username = logInUser(connect, scan);

		logger.info(Keywords.MSG_FORMAT);

		while (connect.connectionActive()) {

			if (!selected && scan.hasNext()) {

				sendFirstMessage(scan, connect);

			}

			if (selected && scan.hasNext()) {

				// Check if the user has typed in a line of text to broadcast to the IM server.
				// If there is a line of text to be
				// broadcast:

				// Read in the text they typed
				String line = scan.nextLine();

				// If the line equals "/quit", close the connection to the IM server.
				if (line.equals("/quit")) {
					logger.info(Keywords.DISCONNECT);
					connect.disconnect();
					break;
				}
				// user wants to change the kind of messaging
				else if (line.equalsIgnoreCase(Keywords.CHANGE_OPTION)) {
					selected = false;
					logger.info(Keywords.MSG_FORMAT);

				} else {
					// Else, send the text so that it is broadcast to all users logged in to the IM
					// server.

					connect.sendMessage(msgOpt + " " + username + " " + msgReceiver + " " + line);
				}
			}

			readNewMessages(mess, connect);

		}

	}
}
