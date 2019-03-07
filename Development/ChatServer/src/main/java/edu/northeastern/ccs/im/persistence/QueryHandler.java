package edu.northeastern.ccs.im.persistence;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class QueryHandler {

	final Logger logger = Logger.getGlobal();
	public static final String SQL_EXCEPTION_MSG = "SQL Exception";
	private Connection connection;
	
	public QueryHandler() {
		connection = DBHandler.getConnection();
	}
	
	protected ResultSet doSelectQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
        } catch (SQLException e) {
        		logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        ResultSet rs = null;
        if (statement != null) {
	        try {
	            rs = statement.executeQuery();
	            return rs;
	        } catch (SQLException e) {
	        		logger.log(Level.INFO, SQL_EXCEPTION_MSG);
	        }
        }
        return null;
    }

	protected long doInsertQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
        		logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return -1;
    }

	protected int doUpdateQuery(String query) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
            return statement.executeUpdate(query);
        } catch (SQLException e) {
        		logger.log(Level.INFO, SQL_EXCEPTION_MSG);
        }
        return 0;
    }

}
