package com.conference.presentations.server.db.mysql;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;

public class DBUtilities {
    private static Connection conn = null;
    private static int port = 3306;        // default port for mysql
    private static final String SEPARATOR = ":";

    /**
     * create a DB connection to mysql
     *
     * @param dbUrl
     * @param dbName
     * @param userName
     * @param password
     * @return
     */
    public static Connection getConnection(String dbUrl, String dbName,
                                           String userName, String password) {

        if (conn == null) {
            try {
                String connString = "jdbc:mysql://" + dbUrl + ":" + port + "/" + dbName + "?autoReconnect=true";
                conn = DriverManager.getConnection(connString, userName, password);
            } catch (SQLException ex) {
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
        }

        return conn;
    }

    public static void printStackTrace(Logger logger, StackTraceElement[] elements){
        if(logger != null && elements != null) {
            for(StackTraceElement element : elements)
                logger.error(String.valueOf(element));
        }
    }
}
