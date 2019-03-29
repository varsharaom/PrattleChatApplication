package edu.northeastern.ccs.im.server;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.ccs.im.constants.QueryConstants;
import edu.northeastern.ccs.im.persistence.IQueryHandler;
import edu.northeastern.ccs.im.persistence.QueryFactory;
import edu.northeastern.ccs.serverim.Message;
import edu.northeastern.ccs.serverim.MessageType;

public class MessageFactoryTests {
	
	IQueryHandler queryHandler;

	@Before
	public void before() {
		queryHandler = QueryFactory.getQueryHandler();
	}
	
//	@Test
	public void testMessageFactoryCustomRegisterMessage() {
		String reg = "$$RGSTR#";
		Message clientMessage = new Message(MessageType.DIRECT, QueryConstants.SENDER_USERNAME, QueryConstants.RECEIVER_USERNAME, reg + " " + QueryConstants.USERNAME + " " + QueryConstants.PASS);
		Message msg = MessageFactory.createMessage(clientMessage, queryHandler);
		assertEquals(MessageType.BROADCAST, msg.getMessageType());
	}

}
