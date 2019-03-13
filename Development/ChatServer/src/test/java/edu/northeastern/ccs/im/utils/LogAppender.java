package edu.northeastern.ccs.im.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * Appender class to capture the log messages
 *
 */
public class LogAppender extends AppenderSkeleton {

	List<LoggingEvent> logs = new ArrayList<>();

	@Override
	public void close() {
		logs.clear();

	}

	@Override
	public boolean requiresLayout() {

		return false;
	}

	@Override
	protected void append(LoggingEvent l) {
		logs.add(l);

	}

	public List<LoggingEvent> getLogs() {
		return logs;
	}

}