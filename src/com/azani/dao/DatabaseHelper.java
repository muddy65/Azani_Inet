package com.azani.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
// import java.io.FileInputStream;
// import java.util.Properties;

public class DatabaseHelper {

    //Class.forName("com.mysql.jdbc.Driver");

    private static final String URL = "jdbc:mysql://mysql-1a936139-muirurir556-f1ad.j.aivencloud.com:24216/defaultdb?ssl-mode=REQUIRED";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS_lGnkUsg84LceXgrVzDB";

    // static{
    //     Properties props = new Properties();
    //     try(FileInputStream fis = new FileInputStream(".env")){
    //         props.load(fis);
    //         URL = props.getProperty("DB_URL");
    //         USER = props.getProperty("DB_USER");
    //         PASSWORD = props.getProperty("DB_PASSWORD");
    //     }catch (Exception e){
    //         URL = System.getenv("DB_URL");
    //         USER = System.getenv("DB_USER");
    //         PASSWORD = System.getenv("DB_PASSWORD");
    //     }
    // }

    /**
     * This method establishes and returns a live connection to the MySQL database.
     * Whenever your other Java classes need to save or read data, they will call this method.
     */
    // public static Connection getConnection() {
    //     Connection connection = null;
    //     try {
    //         // DriverManager attempts to connect to the database using the provided credentials
    //         //Class.forName("com.mysql.cj.jdbc.Driver");
    //         connection = DriverManager.getConnection(URL, USER, PASSWORD);
    //     } catch (SQLException e) {
    //         // If the connection fails (e.g., MySQL is not running, or wrong password),
    //         // this block catches the error and prints exactly what went wrong.
    //         System.err.println("Database Connection Failed!");
    //         e.printStackTrace(); 
    //     }
    //     return connection;
    // }
    public static Connection getConnection() throws SQLException {
        try {
            // Asegura que el driver de MySQL esté cargado en memoria
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver no encontrado en el classpath: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Database Connection Failed!: " + e.getMessage());
            throw e;
        }
    }

    /**
     * A temporary main method specifically for testing your connection.
     * Run this file directly to see if Java can talk to your MySQL server.
     */
    // public static void main(String[] args) {
    //     System.out.println("Attempting to connect to Azani ISP Database...");
        
    //     // Call the method above to try and get a connection
    //     //Connection testConn = DatabaseHelper.getConnection();
        
    //     if (testConn != null) {
    //         System.out.println("SUCCESS! Connected to the database.");
    //         try {
    //             // Always close the connection when done to free up computer memory
    //             testConn.close();
    //             System.out.println("Connection safely closed.");
    //         } catch (SQLException e) {
    //             e.printStackTrace();
    //         }
    //     } else {
    //         System.out.println("FAILED. Check your password and ensure MySQL server is running.");
    //     }
    // }
}