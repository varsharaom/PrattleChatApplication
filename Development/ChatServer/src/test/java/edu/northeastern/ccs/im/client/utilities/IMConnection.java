package edu.northeastern.ccs.im.client.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingWorker;

/**
 * This class manages the connection between an the IM client and the IM server.
 * Instances of this class can be relied upon to manage all the details of this
 * connection and sends alerts when appropriate. Instances of this class must be
 * constructed and connected before it can be used to transmit messages.
 * 
 * This work is licensed under the Creative Commons Attribution-ShareAlike 4.0
 * International License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-sa/4.0/. It is based on work
 * originally written by Matthew Hertz and has been adapted for use in a class
 * assignment at Northeastern University.
 * 
 * @version 1.3
 */
public class IMConnection {

	/**
	 * Real Connection instance which this class wraps and makes presentable to the
	 * user
	 */
	private SocketNB socketConnection;

	/**
	 * List of instances that have registered as a listener for connection events.
	 */
	private ArrayList<LinkListener> linkListeners;

	/**
	 * List of instances that have registered as a listener for received message
	 * events.
	 */
	private ArrayList<MessageListener> messageListeners;

	/** Server to which this connection will be made. */
	private String hostName;

	/** Port to which this connection will be made. */
	private int portNum;

	/** Name of the user for which this connection was formed. */
	private String userName;


	/** Instance used to read the messages. */
	private MessageScanner messageScanner;

	/**
	 * Creates an instance that will manage a connection with an IM server, but does
	 * not begin the process of making a connection to the IM server.
	 * 
	 * @param host     The name of the host that this connection is using
	 * @param port     The port number to use.
	 * @param username Name of the user for which this connection is being made.
	 */
	public IMConnection(String host, int port, String username) {
		if ((username == null) || username.trim().equals("")) {
			username = "TooDumbToEnterRealUsername";
		}
		linkListeners = new ArrayList<>();
		messageListeners = new ArrayList<>();
		userName = username;
		hostName = host;
		portNum = port;
	}

	/**
	 * Add the given listener to be notified whenever 1 or more Messages are
	 * received from IM server via this connection.
	 * 
	 * @param listener Instance which will begin to receive notifications of any
	 *                 messages received by this IMConnection.
	 * @throws InvalidListenerException Exception thrown when this is called with a
	 *                                  value of null for {@code listener}
	 */
	public void addMessageListener(MessageListener listener) {
		if (listener == null) {
			throw new InvalidListenerException("Cannot add (null) as a listener!");
		}
		messageListeners.add(listener);
	}

	/**
	 * Send a message to log in to the IM server using the given username. For the
	 * moment, you will automatically be logged in to the server, even if there is
	 * already someone with that username.<br/>
	 * Precondition: connectionActive() == false
	 * 
	 * @throws IllegalNameException Exception thrown if we try to connect with an
	 *                              illegal username. Legal usernames can only
	 *                              contain letters and numbers.
	 * @return True if the connection was successfully made; false otherwise.
	 */
	public boolean connect() {
		String name = getUserName();
		for (int i = 0; i < name.length(); i++) {
			char ch = name.charAt(i);
			if (!Character.isLetter(ch) && !Character.isDigit(ch)) {
				throw new IllegalNameException("Cannot log in to the server using the name " + name);
			}
		}
		boolean retVal = login();
		MessageScanner rms = MessageScanner.getInstance();
		addMessageListener(rms);
		messageScanner = rms;
		return retVal;
	}

	/**
	 * Returns whether the instance is managing an active, logged-in connection
	 * between the client and an IM server.
	 * 
	 * @return True if the client is logged in to the server using this connection;
	 *         false otherwise.
	 */
	public boolean connectionActive() {
		if (socketConnection == null) {
			return false;
		} else {
			return socketConnection.isConnected();
		}
	}

	/**
	 * Break this connection with the IM server. Once this method is called, this
	 * instance will need to be logged back in to the IM server to be usable.<br/>
	 * Precondition: connectionActive() == true
	 */
	public void disconnect() {
		Message quitMessage = Message.makeQuitMessage(getUserName());
		socketConnection.print(quitMessage);
		KeyboardScanner.close();
	}

	/**
	 * Gets an object which can be used to read what the user types in on the
	 * keyboard without waiting. The object returned by this method should be used
	 * rather than {@link Scanner} since {@code Scanner} will cause a program to
	 * halt if there is no input.
	 * 
	 * @return Instance of {@link KeyboardScanner} that can be used to read keyboard
	 *         input for this connection of the server.
	 */
	public KeyboardScanner getKeyboardScanner() {
		return KeyboardScanner.getInstance();
	}

	/**
	 * Gets an object which can be used to get the message sent by the server over
	 * this connection. This is the only object that can be used to retrieve all
	 * these messages.
	 * 
	 * @return Instance of {@link MessageScanner} that can be used to read message
	 *         sent over this connection for this user.
	 */
	public MessageScanner getMessageScanner() {
		if (messageScanner == null) {
			throw new IllegalOperationException("Cannot get a MessageScanner if you have not connected to the server!");
		}
		return messageScanner;
	}

	/**
	 * Get the name of the user for which we have created this connection.
	 * 
	 * @return Current value of the user name and/or the username with which we
	 *         logged in to this IM server.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Unless this is a &quot;special&quot; server message, this sends the given
	 * message to all of the users logged in to the IM server. <br/>
	 * Precondition: connectionActive() == true
	 * 
	 * @param message Text of the message which will be broadcast to all users.
	 */
	public void sendMessage(String message) {
		if (!connectionActive()) {
			throw new IllegalOperationException("Cannot send a message if you are not connected to a server!\n");
		}
		Message bctMessage = Message.makeBroadcastMessage(userName, message);
		socketConnection.print(bctMessage);
	}

	/**
	 * Send a message to log in to the IM server using the given username. For the
	 * moment, you will automatically be logged in to the server, even if there is
	 * already someone with that username.<br/>
	 * Precondition: connectionActive() == false
	 * 
	 * @return True if the connection was successfully made; false otherwise.
	 */
	private boolean login() {
		// Now log in using this name.
		SwingWorker<Void, Message> workerBee;
		
		Message loginMessage = Message.makeLoginMessage(userName);
		try {
			socketConnection = new SocketNB(hostName, portNum);
			socketConnection.startIMConnection();
		} catch (IOException e) {
			// Report the error
			Logger logger = Logger.getGlobal();
			logger.log(Level.INFO, "ERROR:  Could not make a connection to: " + hostName + " at port " + portNum);
			logger.log(Level.INFO, "If the settings look correct and your machine is connected to the Internet, report this error to Dr. Jump");
			
			// Return that the connection could not be made.
			return false;
		}
		// Send the message to log us into the system.
		socketConnection.print(loginMessage);
		// Create the background thread that handles our incoming messages.
		workerBee = new ScanForMessagesWorker(this, socketConnection);
		// Start the worker bee scanning for messages.
		workerBee.execute();
		// Return that we were successful
		return true;
	}

	@SuppressWarnings({ "unchecked" })
	protected void fireSendMessages(List<Message> mess) {
		ArrayList<MessageListener> targets;
		synchronized (this) {
			targets = (ArrayList<MessageListener>) messageListeners.clone();
		}
		for (MessageListener iml : targets) {
			iml.messagesReceived(mess.iterator());
		}
	}

	@SuppressWarnings("unchecked")
	protected void fireStatusChange(String userName) {
		ArrayList<LinkListener> targets;
		synchronized (this) {
			targets = (ArrayList<LinkListener>) linkListeners.clone();
		}
		for (LinkListener iml : targets) {
			iml.linkStatusUpdate(userName, this);
		}
	}

	protected void loggedOut() {
		socketConnection = null;
	}
}