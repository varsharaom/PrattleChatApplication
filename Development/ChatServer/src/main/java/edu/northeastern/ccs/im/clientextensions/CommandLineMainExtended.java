package edu.northeastern.ccs.im.clientextensions;

import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.KeyboardScanner;
import edu.northeastern.ccs.im.MessageScanner;
import edu.northeastern.ccs.im.*;

import java.util.logging.Logger;

public class CommandLineMainExtended {

	private static final Logger LOGGER = Logger.getLogger(CommandLineMainExtended.class.getName());

	public static void main(String[] args) {
		IMConnection connect;

		boolean fail = true;
		String[] strs;
		String msgOpt = "";
		
		String msgReceiver = "";
		String username = "";

		boolean selected = false;

		do {
			// Establish a connection soon after the window is opened
			// Create a Connection to the IM server.
			connect = new IMConnection(args[0], Integer.parseInt(args[1]), Keywords.SAY_HELLO);
		} while (!connect.connect());

		KeyboardScanner scan = connect.getKeyboardScanner();
		MessageScanner mess = connect.getMessageScanner();

		// Show login/register prompt
		LOGGER.info(Keywords.LOGIN_MSG);
		do {
			// Check if the user has typed in a line of text to broadcast to the IM server.
			// If there is a line of text to be broadcast:
			if (scan.hasNext()) {

				// Read in the text they typed
				String userinput = scan.nextLine();

				strs = userinput.split(" ");
				if (strs.length <= 2 || strs.length > 3) {
					LOGGER.info(Keywords.ERROR_MSG);
				} else {
					switch (strs[0].toUpperCase()) {
					case Keywords.LOGIN:
					case Keywords.REGISTER:
						fail = false;
						username = strs[1];
						// this is a broadcast message
						connect.sendMessage(userinput);
						break;
					default:
						LOGGER.info(Keywords.ERROR_MSG);
						break;
					}
				}
			}
		} while (fail);

		// now wait for confirmation from server whether the password is correct

		// once the confirmation is obtained, server should return user names

		// prompt user to type GRP or DRCT based on the type of message he wants to send
		LOGGER.info(Keywords.MSG_FORMAT);

		while (connect.connectionActive()) {
			if (!selected && scan.hasNext()) {
				String txt = scan.nextLine();
				String[] parts = txt.split(" ", 3);
				if (parts.length < 3) {
					LOGGER.info(Keywords.ERROR_MSG);
				} else {
					msgOpt = parts[0];
					msgReceiver = parts[1];
					String msgBody = parts[2];
					// check the starting of the message to keep track of whether it is private
					// message or group message
					if (msgOpt.equalsIgnoreCase(Keywords.DRCT_MESSAGE)
							|| msgOpt.equalsIgnoreCase(Keywords.GRP_MESSAGE)) {
						selected = true;
						connect.sendMessage(msgOpt + " " + username + " " + msgReceiver + " " + msgBody);
					}
					// user established whether he wants to send group or private message and the intended receiver
					// now onwards accept only message
				}
			}
			if (selected) {
				// Create the objects needed to read & write IM messages.
				// Check if the user has typed in a line of text to broadcast to the IM server.
				// If there is a line of text to be
				// broadcast:
				if (scan.hasNext()) {
					// Read in the text they typed
					String line = scan.nextLine();
					
					// If the line equals "/quit", close the connection to the IM server.
					if (line.equals("/quit")) {
						connect.disconnect();
						break;
					}
					// user wants to change the kind of messaging
					else if (line.equalsIgnoreCase(Keywords.CHANGE_OPTION)) {
						selected = false;
						LOGGER.info(Keywords.MSG_FORMAT);
					} else {
						// Else, send the text so that it is broadcast to all users logged in to the IM server.
						connect.sendMessage(msgOpt + " " + username + " " + msgReceiver + " " + line);
					}
				}
			}
			
			// Get any recent messages received from the IM server.
			if (mess.hasNext()) {
				Message message = mess.next();
				if (!message.getSender().equals(connect.getUserName())) {
					// get the text part of the message
					String messageText = message.getText();

					System.out.println(message.getSender() + ": " + MessageParser.getMsgType(messageText));
					System.out.println(MessageParser.getMsgBody(messageText));
				}
			}
		}
		System.out.println("Program complete.");
		System.exit(0);
	}
}
