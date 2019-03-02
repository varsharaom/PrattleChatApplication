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
	public static final String CONNECTION_STRING = "jdbc:mysql://prattledb.c22lvtrn2mli.us-east-2.rds.amazonaws.com/prattledb";
	public static final String DB_USER = "root";
	public static final String DB_CRED = "prattledb";
	
    private static Connection conn = null;

    static Connection getConnection() {
        if (conn == null) {
            createConnection();
        }
        return conn;
    }

    private static void createConnection() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING, DB_USER, DB_CRED);
        } catch (SQLException e) {
        		logger.log(Level.INFO, CLASS_EXCEPTION_MSG);
        }

    }
}
