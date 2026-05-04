package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Connection details
        String url = "jdbc:mysql://localhost:3306/hospital_db";
        String user = "root";
        String password = ""; // XAMPP default is no password

        Scanner myObj = new Scanner(System.in);

        try {
            System.out.println("Connecting to database...");
            Connection connection = DriverManager.getConnection(url, user, password);

            // 1. Define the SQL command
            String sql = "INSERT INTO patients (name, age, ailment) VALUES (?, ?, ?)";
            // 2. Prepare the statement (this creates the 'pstmt' object)
            var pstmt = connection.prepareStatment(sql);
            // 3. Get User Input and Fill the Blanks
            System.out.println("Enter Patient name:")
            String userName = myObj.nextLine(); // Read user input
            pstmt.setString(1, userName);

            System.out.println("Enter Patient Age:");
            int userAge = myObj.nextLine(); // Read user Age
            pstmt.setInt(2, userAge); 

            myObj.nextLine(); // "Buffer Clear": Do this after nextInt() so it doesn't skip the next line

            System.out.println("Enter Ailment:")
            String userAilment = myObj.nextLine();
            pstmt.setString(3, userAilment);

            System.out.println("--- Success! Connection Established ---");

            // This is where you will eventually call your menu or logic

        } catch (SQLException e) {
            System.out.println("--- Connection Failed ---");
            e.printStackTrace();
        }
    }
}