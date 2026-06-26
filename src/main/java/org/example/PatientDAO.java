package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    /**
     * Executes a secure SQL INSERT with the newly required doctor_id.
     */
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, age, ailment, doctor_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getAilment());
            pstmt.setInt(4, patient.getDoctorId()); // Injecting the Synchro-Link

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: MAGI Layer failed to execute Subject INSERT query.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * MAGI SYSTEM COMMAND: Executes a targeted data overwrite including Personnel re-assignments.
     */
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, age = ?, ailment = ?, doctor_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getAilment());
            pstmt.setInt(4, patient.getDoctorId()); // Re-assigning the Agent
            pstmt.setInt(5, patient.getId()); // The absolute target lock

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: MAGI Layer failed to execute overwrite sequence.");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: Persistent layer deletion block error occurred.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extracts the complete subject matrix including their assigned Personnel IDs.
     */
    public List<Patient> getAllPatients() {
        List<Patient> patientList = new ArrayList<>();
        String sql = "SELECT id, name, age, ailment, doctor_id FROM patients";

        try (Connection conn = DatabaseConnection.getConnecction();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String ailment = rs.getString("ailment");
                int doctorId = rs.getInt("doctor_id"); // Extracting the Synchro-Link

                Patient patient = new Patient(id, name, age, ailment, doctorId);
                patientList.add(patient);
            }
        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: MAGI Layer failed to execute Subject SELECT query.");
            e.printStackTrace();
        }
        return patientList;
    }
}

