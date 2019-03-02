package edu.northeastern.ccs.im.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBHandler {
    private static Connection conn = null;
    private static PreparedStatement statement = null;

    static Connection getConnection() {
        if (conn == null) {
            createConnection();
        }
        return conn;
    }

    private static void createConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection("jdbc:mysql://prattledb.c22lvtrn2mli.us-east-2.rds.amazonaws.com/prattledb",
                    "root", "prattledb");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
