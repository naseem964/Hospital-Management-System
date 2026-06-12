package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    /// URL format : jdbc:mysql://[host]:[part]/[database_name]
    private static final String URL = "jdbc:mysql://localhost:3306/hospital_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; //

    /**
     * * starts a new handshake  with the local mysql database instance.
     * @return an active Connection object if successful, or null if it fails.
     */
    public static Connection getConnecction() {
        Connection connection = null;
        try {
            // force the jvm to load the mysql driver class we imported via maven
            Class.forName("com.mysql.cj.jdbc.Driver");

            // attempt to establish the physical connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(" ✅ MySQL Database Connected Successfully!");

        } catch (ClassNotFoundException e) {
            System.err.println("❌ Database Connection Failed: MySQL Driver missing. ");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database Connection Failed: MySQL Driver missing.");
            e.printStackTrace();
        }
        return connection;
    }
}
