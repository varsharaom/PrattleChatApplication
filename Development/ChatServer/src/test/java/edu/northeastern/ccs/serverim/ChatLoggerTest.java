package edu.northeastern.ccs.serverim;

import org.junit.Test;

import edu.northeastern.ccs.serverim.ChatLogger;

import static org.junit.Assert.*;

public class ChatLoggerTest {

	private static final String LOG_MESSAGE="TEST";

	@Test
	public void testErrorLog() {
		ChatLogger.error(LOG_MESSAGE);
	}

	@Test
	public void testWarningLog() {
		ChatLogger.warning(LOG_MESSAGE);
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