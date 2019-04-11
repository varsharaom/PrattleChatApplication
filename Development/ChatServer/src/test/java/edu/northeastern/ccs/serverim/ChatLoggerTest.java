package edu.northeastern.ccs.serverim;

import org.junit.Test;

import edu.northeastern.ccs.im.utils.LogAppender;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;

public class ChatLoggerTest {
	
	
	/**
	 * log appender instance to get the log messages from the client console
	 */

	private static LogAppender la;
	
	/**
	 * logger instance to get a reference to the logger in the ChatLogger class
	 */
	private static Logger logger ;

	
	/**
	 * This method initializes the instance of custom appender which is used to get
	 * the log messages
	 */
	@BeforeClass
	public static void initAppender() {

		la = new LogAppender();

	}
	
	@Before
	public  void init() {
		logger = ChatLogger.getLogger();
		logger.addAppender(la);
	}
	@After
	public  void tearDown() {
		logger.removeAppender(la);
	}

	private static final String LOG_MESSAGE="TEST";

	@Test
	public void testErrorLog() {
		ChatLogger.error(LOG_MESSAGE);
		List<LoggingEvent> log = la.getLogs();		
		assertEquals(LOG_MESSAGE, (String)log.get(0).getMessage());
	}

	@Test
	public void testWarningLog() {
		ChatLogger.warning(LOG_MESSAGE);
		List<LoggingEvent> log = la.getLogs();
		assertEquals(LOG_MESSAGE, (String)log.get(0).getMessage());
	}
	
	@Test
	public void testDebugLog() {
		ChatLogger.debug(LOG_MESSAGE);
		List<LoggingEvent> log = la.getLogs();
		assertEquals(LOG_MESSAGE, (String)log.get(0).getMessage());
	}

	@Test
	public void testSetModeFile() {
		ChatLogger.setMode(ChatLogger.getHandlerType(1));
	}

	@Test
	public void testSetModeConsole() {
		ChatLogger.setMode(ChatLogger.getHandlerType(2));
	}

	@Test
	public void testSetModeBoth() {
		ChatLogger.setMode(ChatLogger.getHandlerType(3));
	}

	@Test
	public void testGetInvalidHandler() {
		assertNotNull(ChatLogger.getHandlerType(4));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetInvalidHandler() {
		ChatLogger.setMode(ChatLogger.getHandlerType(5));
	}
	
}