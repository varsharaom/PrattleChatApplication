package edu.northeastern.ccs.im;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    LOGGER.setLevel(Level.ALL);
    LOGGER.setUseParentHandlers(false);
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
      LOGGER.log(lvl, msg);
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
      Formatter simpleFormatter = new SimpleFormatter();
      Handler fileHandler = new FileHandler(PATH);
      fileHandler.setFormatter(simpleFormatter);
      fileHandler.setLevel(Level.ALL);
      LOGGER.addHandler(fileHandler);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  /**
   * Creates console Handler for the logger to use.
   */
  private static void switchToConsole() {
    Formatter simpleFormatter = new SimpleFormatter();
    Handler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(simpleFormatter);
    consoleHandler.setLevel(Level.ALL);
    LOGGER.addHandler(consoleHandler);
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
