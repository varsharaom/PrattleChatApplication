package edu.northeastern.ccs.im.client;

import java.util.Scanner;

/**
 * Class which can be used as a command-line IM client.
 *
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 *
 * @version 1.3
 */
public class CommandLineMain {

	/**
	 * This main method will perform all of the necessary actions for this phase of
	 * the course project.
	 *
	 * @param args Command-line arguments which we ignore
	 */
	public static void main(String[] args) {
		IMConnection connect;
		@SuppressWarnings("resource")
		Scanner in = new Scanner(System.in);

		do {
			// Prompt the user to type in a username.
			System.out.println("What username would you like?");

			String username = in.nextLine();

			// Create a Connection to the IM server.
			connect = new IMConnection(args[0], Integer.parseInt(args[1]), username);
		} while (!connect.connect());

		// Create the objects needed to read & write IM messages.
		KeyboardScanner scan = connect.getKeyboardScanner();
		MessageScanner mess = connect.getMessageScanner();

		// Repeat the following loop
		while (connect.connectionActive()) {
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
				} else {
					// Else, send the text so that it is broadcast to all users logged in to the IM
					// server.
					connect.sendMessage(line);
				}
			}
			// Get any recent messages received from the IM server.
			if (mess.hasNext()) {
				Message message = mess.next();
				if (!message.getSender().equals(connect.getUserName())) {
					System.out.println(message.getSender() + ": " + message.getText());
				}
			}
		}
		System.out.println("Program complete.");
		System.exit(0);
	}
}
