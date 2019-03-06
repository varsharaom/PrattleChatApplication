package edu.northeastern.ccs.im.clientextensions;

public class MessageParser {    
    
	public static String getMsgType(String msg) {
		return msg.split("$$",2)[0];
	}
	public static String getMsgBody(String msg) {
		return msg.split("$$",2)[1];
	}
}
