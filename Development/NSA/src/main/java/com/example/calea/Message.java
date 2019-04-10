package com.example.calea;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Message {

	public Message(long id, String sender, String receiver, String msgText, Date messageSent) {
		this.id = id;
		this.msgSender = sender;
		this.msgReceiver = receiver;
		this.msgText = msgText;
		this.messageSent = messageSent;
	}
	
    /** The message id. */
    private long id;

    /** The msg sender. */
    private String msgSender;

    /** The message receiver. */
    private String msgReceiver;
    
    /** The msg text. */
    private String msgText;
    
    /** The message sent. */
    private Date messageSent;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
    @JsonGetter("id")
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Gets the msg sender.
	 *
	 * @return the msg sender
	 */
	@JsonGetter("sender")
	public String getMsgSender() {
		return msgSender;
	}

	/**
	 * Sets the msg sender.
	 *
	 * @param msgSender the new msg sender
	 */
	public void setMsgSender(String msgSender) {
		this.msgSender = msgSender;
	}

	/**
	 * Gets the msg receiver.
	 *
	 * @return the msg receiver
	 */
	@JsonGetter("receiver")
	public String getMsgReceiver() {
		return msgReceiver;
	}

	/**
	 * Sets the msg receiver.
	 *
	 * @param msgReceiver the new msg receiver
	 */
	public void setMsgReceiver(String msgReceiver) {
		this.msgReceiver = msgReceiver;
	}

	/**
	 * Gets the msg text.
	 *
	 * @return the msg text
	 */
	@JsonGetter("body")
	public String getMsgText() {
		return msgText;
	}

	/**
	 * Sets the msg text.
	 *
	 * @param msgText the new msg text
	 */
	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}

	/**
	 * Gets the message sent.
	 *
	 * @return the message sent
	 */
	@JsonGetter("timestamp")
	public Date getMessageSent() {
		return messageSent;
	}

	/**
	 * Sets the message sent.
	 *
	 * @param messageSent the new message sent
	 */
	public void setMessageSent(Date messageSent) {
		this.messageSent = messageSent;
	}
	
}
