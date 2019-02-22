package edu.northeastern.ccs.im;

 import org.junit.Test;

 public class ChatLoggerTest {

 	private static final String LOG_MESSAGE="TEST";
 	
 	private enum HandlerType {
 	    /** The file handler. */
 	    FILE,
 	    /** The console handler. */
 	    CONSOLE,
 	    /** Both handlers. */
 	    BOTH;
 	  }
 	
 	@Test
 	public void testErrorLog() {
 		ChatLogger.error(LOG_MESSAGE);
 	}
 	
 	@Test
 	public void testWarningLog() {
 		ChatLogger.warning(LOG_MESSAGE);
 	}
 	

 }