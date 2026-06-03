package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement; // <-- FIX 2: Added this import
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
                System.out.println(
                        ORANGE + "|| " + RED + "WARNING : MAGI SYSTEM - PATIENT DATABASE" + ORANGE + " ||" + RESET);
                System.out.println(ORANGE + "===============================================" + RESET);
                System.out.println(ORANGE + "|| [1] VIEW SUBJECT DATA            ||" + RESET);
                System.out.println(ORANGE + "|| [2] REGISTER NEW SUBJECT         ||" + RESET);
                System.out.println(ORANGE + "|| [3] DISCHARGE SUBJECT            ||" + RESET);
                System.out.println(ORANGE + "|| [4] UPDATE PATIENT RECORD        ||" + RESET);// <-- FIX  : Added to menu
                System.out.println(ORANGE + "|| [5] VIEW MEDICAL STAFF           ||" + RESET); // <-- FIX: menu in correct order
                System.out.println(ORANGE + "|| [6] TERMINATE CONNECTION         ||" + RESET);
                System.out.println(ORANGE + "======================================" + RESET);

                int choice = myObj.nextInt();
                myObj.nextLine(); // Buffer Clear

                switch (choice) {
                    case 1:
                        // SQL query to fetch all data
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
                        String sql = "INSERT INTO patients (name, age, ailment) VALUES (?, ?, ?)";
                        var pstmt = connection.prepareStatement(sql);

                        System.out.println("Enter Patient name:");
                        String userName = myObj.nextLine();
                        pstmt.setString(1, userName);

                        System.out.println("Enter Patient Age:");
                        int userAge = myObj.nextInt();
                        pstmt.setInt(2, userAge);

                        myObj.nextLine(); // Buffer Clear

                        System.out.println("Enter Ailment:");
                        String userAilment = myObj.nextLine();
                        pstmt.setString(3, userAilment);

                        int rowsInserted = pstmt.executeUpdate();
                        if (rowsInserted > 0) {
                            System.out.println(RED + "\n>>> DATA UPLOAD: COMPLETE" + RESET);
                            System.out.println(ORANGE + ">>> STATUS: PATIENT REGISTERED IN CENTRAL DOGMA" + RESET);
                        }
                        break;

                    case 3:
                        System.out.println(ORANGE + "\n>>> INITIATING SUBJECT DISCHARGE PROTOCOL..." + RESET);
                        System.out.print(ORANGE + "ENTER PATIENT ID TO DE-REGISTER: " + RESET);

                        // <-- FIX 1: Changed 'scanner' to 'myObj'
                        int targetId = myObj.nextInt();
                        myObj.nextLine(); // Clear the scanner buffer

                        String deleteSql = "DELETE FROM patients WHERE id = ?";

                        try {
                            // <-- FIX 3: Renamed 'pstmt' to 'deleteStmt' to avoid clashes
                            PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                            deleteStmt.setInt(1, targetId);

                            int rowsDeleted = deleteStmt.executeUpdate();

                            if (rowsDeleted > 0) {
                                System.out.println(RED + "\n>>> DATA DELETION: COMPLETE" + RESET);
                                System.out.println(ORANGE + ">>> STATUS: SUBJECT " + targetId + " PURGED FROM CENTRAL DOGMA" + RESET);
                            } else {
                                System.out.println(RED + "\n>>> DATA DELETION: FAILED" + RESET);
                                System.out.println(ORANGE + ">>> STATUS: NO SUBJECT FOUND WITH ID " + targetId + RESET);
                            }
                        } catch (SQLException e) {
                            System.out.println(RED + ">>> ERROR: SYSTEM FAILURE DURING DISCHARGE" + RESET);
                            e.printStackTrace();
                        }
                        break;
                    case 4:
                        String Sql = "UPDATE patients SET ailment = ?, room_number = ? WHERE id = ?"; // Update patients info option.
                        var update = connection.prepareStatement(Sql);

                        System.out.println("Enter Patient Diagnosis : ");
                        String userailment = myObj.nextLine();
                        update.setString(1,userailment);

                        System.out.println("Enter Room_Number : ");
                        String userRoom_Number = myObj.nextLine();
                        update.setString(2,userRoom_Number);

                        System.out.println("Enter Patient ID # :");
                        String userid = myObj.nextLine();
                        update.setString(3,userid);

                        update.executeUpdate();
                        update.close();

                        System.out.println("Patient record updated successfully!");

                        break;
                    case 5:
                        String CLASSIFIED_REGISTRATION_PROTOCOL = "INSERT INTO doctors (name, specialty, status) VALUES (?, ?, ?)";
                        var magiCoreExecutor = connection.prepareStatement(CLASSIFIED_REGISTRATION_PROTOCOL);

                        System.out.println("IDENTIFY MEDICAL PERSONNEL NAME:");
                        String personnelName = myObj.nextLine();
                        magiCoreExecutor.setString(1, personnelName);

                        System.out.println("ENTER ASSIGNED CLINICAL SPECIALTY:");
                        String clinicalSpecialty = myObj.nextLine();
                        magiCoreExecutor.setString(2, clinicalSpecialty);

                        System.out.println("ESTABLISH OPERATIONAL STATUS [ON_DUTY / ON_CALL / OFF_DUTY]:");
                        String operationalStatus = myObj.nextLine();
                        magiCoreExecutor.setString(3, operationalStatus);

                        // Execution Phase
                        int protocolExecuted = magiCoreExecutor.executeUpdate();
                        magiCoreExecutor.close(); // Clean up resource memory

                        if (protocolExecuted > 0) {
                            System.out.println(RED + "\n>>> MAGI SYSTEM: INJECTION MODULE COMPLETE" + RESET);
                            System.out.println(ORANGE + ">>> STATUS: PERSONNEL SYNCED WITH CENTRAL DOGMA" + RESET);
                        }
                        break;
                    case 6:
                        running = false;
                        System.out.println(RED + "CONNECTION TERMINATED. LOGGING OUT..." + RESET);
                        break;

                    default:
                        System.out.println(RED + "INVALID SELECTION. RETRY." + RESET); // reminder to include  the switch order for case 3&4 and the menu option
                } // End of switch
            } // End of while loop

        } catch (SQLException e) {
            System.out.println("--- Connection Failed ---");
            e.printStackTrace();
        }
    } // End of main method
}