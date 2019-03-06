package edu.northeastern.ccs.im.clientextensions;

import java.util.Scanner;

import com.mysql.jdbc.Connection;

import edu.northeastern.ccs.im.IMConnection;
import edu.northeastern.ccs.im.KeyboardScanner;
import edu.northeastern.ccs.im.MessageScanner;
import edu.northeastern.ccs.im.*;

public class CommandLineMainExtended {

	public static void main(String[] args) {
		IMConnection connect;
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);
		boolean fail = true;
		String[] strs;
		String msgOpt = "";
		String selection = "";
		String msgReceiver = "";
		String username = "";

		boolean selected = false;

		do {
			// establish a connection soon after the window is opened

			// Create a Connection to the IM server.
			connect = new IMConnection(args[0], Integer.parseInt(args[1]), Keywords.SAY_HELLO);
		} while (!connect.connect());

		// use broadcast message to login or register or send any messages

		do {
			in.reset();
			System.out.println("Enter $$LGN# username password, if you want to login\n"
					+ "Enter $$RGSTR# username password, if you want to register. Username and password"
					+ " should not contain spaces");
			String userinput = in.nextLine();

			strs = userinput.split(" ");
			if (strs.length <= 2 || strs.length > 3) {
				System.out.println("Enter the command in the specified format");
			} else if (strs.length == 3) {
				selection = strs[0].toUpperCase();
				if (selection.equals(Keywords.LOGIN) || selection.equals(Keywords.REGISTER)) {
					fail = false;
					username = strs[1];
					// this is a broadcast message
					connect.sendMessage(userinput);
				}
			}
		} while (fail);

		// now I have to wait for confirmation from server whether the password is
		// correct

		// once the confirmation is obtained, server should return user names

		
		KeyboardScanner scan = connect.getKeyboardScanner();
		MessageScanner mess = connect.getMessageScanner();
		// ask user to type GRP or DRCT based on the type of message he wants to send
		while (connect.connectionActive()) {

			if (selected == false) {
				
				System.out
						.println("Send direct message or group message? \nEnter $$GRP# receiver_grp_name message_string"
								+ "  to send group message "
								+ "or $$DRCT# receiver_name message_string to send direct message.");

				if (scan.hasNext()) {
					// Read in the text they typed
					String line = scan.nextLine();
				}
				String txt = in.nextLine();
				//in.reset();
				String[] parts = txt.split(" ", 3);
				System.out.println(parts.length);
				if (parts.length < 3) {
					System.out.println("Enter the command in the specified format");
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
					// user established whether he wants to send group or private message and the intended
					// receiver
					// now onwards accept only message

				}
			}
			if (selected == true) {
				// Create the objects needed to read & write IM messages.
				

				// Check if the user has typed in a line of text to broadcast to the IM server.
				// If there is a line of text to be
				// broadcast:
				if (scan.hasNext()) {
					// Read in the text they typed
					String line = scan.nextLine();
					// System.out.println("read line " + line);
					// If the line equals "/quit", close the connection to the IM server.
					if (line.equals("/quit")) {
						connect.disconnect();
						break;
					}
					// user wants to change the kind of messaging
					else if (line.equalsIgnoreCase(Keywords.CHANGE_OPTION)) {
						selected = false;
					} else {
						// Else, send the text so that it is broadcast to all users logged in to the IM
						// server.
						// System.out.println("sending message to the intended client");
						connect.sendMessage(msgOpt + " " + username + " " + msgReceiver + " " + line);
					}
				}

				// Get any recent messages received from the IM server.
				if (mess.hasNext()) {

					Message message = mess.next();
					if (!message.getSender().equals(connect.getUserName())) {
						// get the text part of the message
						String messageText = message.getText();
						
						// System.out.println(message.getSender() + ": " + message.getText());
						System.out.println(message.getSender() + ": " + MessageParser.getMsgType(messageText));
						System.out.println(MessageParser.getMsgBody(messageText));

					}
				}

			}
		}
		

		System.out.println("Program complete.");
		System.exit(0);
	}

}
