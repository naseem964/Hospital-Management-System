package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PatientDAO {

    /**
     * Takes a Patient object and writes their data directly into the XAMPP MySQL database.
     * @param patient The patient data to insert.
     * @return true if the database row was created successfully, false otherwise.
     */
    public boolean addPatient(Patient patient) {
        // FIXED: Updated column name from condition_summary to matching database column 'ailment'
        String sql = "INSERT INTO patients (name, age, ailment) VALUES (?, ?, ?)";

        // Use a try-with-resources block to automatically open and close database connections cleanly
        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // FIXED: Aligned parameter targets directly with your Patient.java model fields
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getAilment()); // Matches your exact getter method name

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error executing INSERT query inside PatientDAO:");
            e.printStackTrace();
            return false;
        }
    }
}