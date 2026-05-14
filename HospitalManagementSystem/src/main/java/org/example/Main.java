package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;


public class Main {
    // ui color constants
    public static final String RED = "\u001B[31m";
    public static final String ORANGE = "\u001b[33m";
    public static final String RESET = "\u001B[0m";
    public static final String BLACK_BG = "\u001b[40m";

    public static void main(String[] args) {
        // Connection details
        String url = "jdbc:mysql://localhost:3306/hospital_db";
        String user = "root";
        String password = ""; // XAMPP default is no password

        Scanner myObj = new Scanner(System.in);

        try {
            System.out.println("Connecting to database...");
            Connection connection = DriverManager.getConnection(url, user, password);

            boolean running = true;

            while (running) {
                // Show ui menu system
                System.out.println(ORANGE + "===============================================" + RESET);
                System.out.println(ORANGE + "|| " + RED + "WARNING : MAGI SYSTEM - PATIENT DARABASE" + ORANGE +" ||" + RESET);
                System.out.println("===============================================" + RESET);
                System.out.println(ORANGE + "|| [1] VIEW SUBJECT DATA            ||" + RESET);
                System.out.println(ORANGE + "|| [2] REGISTER NEW SUBJECT         ||" + RESET);
                System.out.println(ORANGE + "|| [3] TERMINATE CONNECTION         ||" + RESET);
                System.out.println(ORANGE + "======================================" + RESET);
                //Status Alerts
                System.outprintln(RED + ">>> DATA UPLOAD: COMPLETE" + RESET);
                System.out,println(ORANGE + ">>> STATUS: PATIENT REGISTERED IN CENTRAL DOGMA" + RESET);

                int choice = myObj.nextInt();
                myObj.nextLine(); // Buffer Clear

                switch (choice) {
                    case 1:
                        //  SQL query to fetch all data
                        String viewSql = "SELECT * FROM patients";
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery(viewSql);

                        System.out.println("\n--- Current Patient List ---");
                        System.out.printf("%-5s | %-20s | %-5s | %-20s%n", "ID", "Name", "Age", "Ailment");
                        System.out.println("------------------------------------------------------------");

                        while (resultSet.next()) {

                            int id = resultSet.getInt("id");
                            String name = resultSet.getString("name");
                            int age = resultSet.getInt("age");
                            String ailment = resultSet.getString("ailment");
                            // Displaying the data in a clean format
                            System.out.printf("%-5d | %-20s | %-5d | %20s%n", id, name, age, ailment);
                        }
                        break;

                    case 2:
                        String sql = "INSERT INTO patients (name, age, ailment) VALUES (?, ?, ?)";  //  Define the SQL command
                        var pstmt = connection.prepareStatement(sql); //  Prepare the statement (this creates the 'pstmt' object)

                        System.out.println("Enter Patient name:");  //  Get User Input and Fill the Blanks
                        String userName = myObj.nextLine(); // Read user input
                        pstmt.setString(1, userName);

                        System.out.println("Enter Patient Age:");
                        int userAge = myObj.nextInt(); // Read user Age
                        pstmt.setInt(2, userAge);

                        myObj.nextLine(); // Buffer Clear

                        System.out.println("Enter Ailment:");
                        String userAilment = myObj.nextLine();
                        pstmt.setString(3, userAilment);

                        // CRITICAL: This line actually sends the data to MySQL
                        int rowsInserted = pstmt.executeUpdate();
                        if (rowsInserted > 0) {
                            //  STATUS ALERTS HERE
                            System.out.println(RED + "\n>>> DATA UPLOAD: COMPLETE" + RESET);
                            System.out.println(ORANGE + ">>> STATUS: PATIENT REGISTERED IN CENTRAL DOGMA" + RESET);
                        }
                        break;

                    case 3:
                        running = false;
                        System.out.println(RED + "CONNECTION TERMINATED. LOGGING OUT..." + RESET);
                        break;

                    default:
                        System.out.println(RED + "INVALID SELECTION. RETRY." + RESET);
                } // End of switch
            } // End of while loop

        } catch (SQLException e) {
            System.out.println("--- Connection Failed ---");
            e.printStackTrace();
        }
    } // End of main method
} // End of Main class