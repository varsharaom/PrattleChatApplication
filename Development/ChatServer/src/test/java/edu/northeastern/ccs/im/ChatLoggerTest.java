package edu.northeastern.ccs.im;

 import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
 	
 }