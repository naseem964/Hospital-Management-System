package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import java.sql.Statement;

public class PatientDAO {

    /**
     * Takes a Patient object and writes their data directly into the XAMPP MySQL database.
     * @param patient The patient data to insert.
     * @return true if the database row was created successfully, false otherwise.
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, age, ailment) VALUES (?, ?, ?)";

        // Use a try-with-resources block to automatically open and close database connections cleanly
        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // FIXED: Aligned parameter targets directly with  Patient.java model fields
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getAilment()); // Matches  exact getter method name

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error executing INSERT query inside PatientDAO:");
            e.printStackTrace();
            return false;
        }
    }
    public List<Patient> getAllPatients() {
        List<Patient> patientList = new ArrayList<>();
        // UPDATED: Selecting 'id' to pull down the primary key data map full-stack
        String sql = "SELECT id, name, age, ailment FROM patients";

        try (Connection conn = DatabaseConnection.getConnecction();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // ADDED: Extract the auto-increment integer value from the database row
                int id = rs.getInt("id");

                String name = rs.getString("name");
                int age = rs.getInt("age");
                String ailment = rs.getString("ailment");

                // MODIFIED: Reconstruct the object using our 4-parameter constructor
                // Maps persistent database identifiers directly into runtime objects
                Patient patient = new Patient(id, name, age, ailment);
                patientList.add(patient);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error executing SELECT query inside PatientDAO:");
            e.printStackTrace();
        }

        return patientList;
    }
    /**
     * Executes a secure SQL DELETE targeting an explicit record row via primary key mapping.
     */
    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error executing DELETE query inside PatientDAO:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * MAGI SYSTEM COMMAND: Executes a targeted data overwrite on an existing Central Dogma record.
     */
    public boolean updatePatient(Patient patient) {
        // Strict cryptographic targeting via primary key ID
        String sql = "UPDATE patients SET name = ?, age = ?, ailment = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Binding the updated runtime properties to the SQL placeholders
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getAilment());
            pstmt.setInt(4, patient.getId()); // The absolute target lock

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\n>>> MAGI SYSTEM: RECORD OVERWRITE SUCCESSFUL");
                System.out.println(">>> STATUS: SUBJECT ID " + patient.getId() + " RE-SYNCHRONIZED.");
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: MAGI Layer failed to execute overwrite sequence.");
            e.printStackTrace();
            return false;
        }
    }
}

