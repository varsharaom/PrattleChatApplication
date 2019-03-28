package edu.northeastern.ccs.im.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The Class DBHandler.
 */
public class DBHandler {
	
	/**
	 * Private constructor to disallow creation of objects.
	 */
	private DBHandler() {
		
	}

	/** The logger instance used in query methods. */
	static final Logger logger = Logger.getGlobal();
	
	/** The error message for class not found exception. */
	public static final String CLASS_EXCEPTION_MSG = "Class not found exception";
	
    /** The JDBC connection instance. */
    private static Connection conn = null;

    /**
     * Gets the JDBC connection.
     *
     * @return the connection
     */
    static Connection getConnection() {
        if (conn == null) {
            createConnection();
        }
        return conn;
    }

    /**
     * Creates the JDBC connection.
     */
    private static void createConnection() {
        try {
            conn = DriverManager.getConnection(DBConstants.CONNECTION_STRING, DBConstants.DB_USER, DBConstants.DB_CRED);
        } catch (SQLException e) {
        		logger.log(Level.INFO, ""+e.getMessage());
        }
    }
}
