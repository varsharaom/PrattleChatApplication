package edu.northeastern.ccs.im.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBHandler {
	
	private DBHandler() {
		
	}

	static final Logger logger = Logger.getGlobal();
	
	public static final String CLASS_EXCEPTION_MSG = "Class not found rxception";
	
    private static Connection conn = null;

    static Connection getConnection() {
        if (conn == null) {
            createConnection();
        }
        return conn;
    }

    private static void createConnection() {
        try {
            conn = DriverManager.getConnection(DBConstants.CONNECTION_STRING, DBConstants.DB_USER, DBConstants.DB_CRED);
        } catch (SQLException e) {
        		logger.log(Level.INFO, CLASS_EXCEPTION_MSG);
        }
    }
}
