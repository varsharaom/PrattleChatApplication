package edu.northeastern.ccs.serverim;

import java.io.IOException;
import java.util.logging.Level;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 * Logger class that handles logging of all levels of messages.
 *
 * @author Maria Jump and Riya Nadkarni
 * @version 12-20-2018
 */
public class ChatLogger {
  /** Name of the logger file. */
  private static final String LOGNAME = ChatLogger.class.getName();
  /** The logger itself. */
  private static final Logger LOGGER = Logger.getLogger(LOGNAME);
  /** The directory holding the log file. */
  private static final String DIR = System.getProperty("user.dir");
  /** The path for the directory. */
  private static final String PATH = String.format("%s/%s.log", DIR, LOGNAME);
  
  private static final  PatternLayout layout = new PatternLayout("%d{ISO8601} [%t] %-5p %c %x - %m%n");

  
  /**
	 * This method returns the logger instance created for the client
	 * 
	 * @return the logger instance that belongs to the client.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}
  
  /**
   * Static initializations for this class.
   */
  static {
    setMode(HandlerType.BOTH);
  }

  /**
   * Private constructor. This class cannot be instantiated.
   */
  private ChatLogger() {
    throw new IllegalStateException("ChatLogger not instantiable");
  }

  /**
   * Logs the error messages.
   *
   * @param msg error message to be logged
   */
  public static final void error(String msg) {
    write(Level.SEVERE, msg);
  }

  /**
   * Logs the debug messages.
   *
   * @param msg warning to be logged
   */
  public static final void debug(String msg) {
    write(Level.FINE, msg);
  }

  
  /**
   * Logs the warnings.
   *
   * @param msg warning to be logged
   */
  public static final void warning(String msg) {
    write(Level.WARNING, msg);
  }

  /**
   * Logs the general messages.
   *
   * @param msg message to be logged
   */
  public static final void info(String msg) {
    write(Level.INFO, msg);
  }

  /**
   * Toggles between the handler types.
   *
   * @param type the type of handler to be used by the logger
   */
  public static void setMode(HandlerType type) {

    switch (type) {
      case FILE:
        switchToFile();
        break;
      case CONSOLE:
        switchToConsole();
        break;
      case BOTH:
        switchToBoth();
        break;
      default:
        throw new IllegalArgumentException("Invalid handler type.");
    }

  }

  /**
   * Writes to the logger.
   *
   * @param lvl the level of severity of the message being logged
   * @param msg the message being logged.
   * @return true if the message was logged, false otherwise
   */
  private static final boolean write(Level lvl, String msg) {
    boolean done = true;
    try {
      if(lvl==Level.SEVERE) {
    	  LOGGER.fatal(msg);
      }
      else if(lvl==Level.INFO) {
    	  LOGGER.info(msg);
    	  
      }
      else if(lvl==Level.WARNING) {
    	  LOGGER.warn(msg);
      }
      else {
    	  LOGGER.debug(msg);
      }
     
    } catch (SecurityException ex) {
      done = false;
    }
    return done;
  }

  /**
   * Creates file Handler for the logger to use.
   */
  private static void switchToFile() {
    try {
	  RollingFileAppender fileAppender = new RollingFileAppender(layout, PATH);	
	  LOGGER.addAppender(fileAppender);
    } 
    catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  /**
   * Creates console Handler for the logger to use.
   */
  private static void switchToConsole() {
	  LOGGER.addAppender(new ConsoleAppender(layout));
  }

  /**
   * Creates file and console handlers for the logger to use.
   */
  private static void switchToBoth() {
    switchToFile();
    switchToConsole();
  }

  /**
   * Private Enum class for Handler Types.
   */
  private enum HandlerType {
    /** The file handler. */
    FILE,
    /** The console handler. */
    CONSOLE,
    /** Both handlers. */
    BOTH,
    /** Invalid handlers */
    INVALID;
  }

  protected static HandlerType getHandlerType(int mode) {
    switch(mode) {
      case 1:
        return HandlerType.FILE;
      case 2:
        return HandlerType.CONSOLE;
      case 3:
        return HandlerType.BOTH;
      default:
        return HandlerType.INVALID;
    }
  }

}