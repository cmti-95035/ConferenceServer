package com.conference.presentations.server.db.mysql;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBUtilities {
    private static Connection conn = null;
    private static int port = 3306;        // default port for mysql
    private static final String SEPARATOR = ":";
    private static final String COMMA_DELIMITER = ",";

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

    public static List<Integer> convertDelimitedStringToList(Logger logger, String delimitedString) {

        List<Integer> result = new ArrayList<Integer>();

        if (delimitedString != null && !delimitedString.isEmpty()) {
            String[] parts = delimitedString.split(COMMA_DELIMITER);
            for(int i = 0; i < parts.length; i++){
                try {
                    int field = Integer.parseInt(parts[i]);
                    result.add(field);
                } catch (Exception e){
                    logger.error("Number format exception: " + parts[i]);
                }

            }
        }
        return result;

    }

    public static String convertListToDelimitedString(List<Integer> list) {

        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for(int i = 0; i < (list.size() - 1); i++){
                sb.append(list.get(i));
                sb.append(COMMA_DELIMITER);
            }
            sb.append(list.get(list.size() - 1));
        }
        return sb.toString();
    }
}
