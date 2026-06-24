package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {

    /**
     * Executes a secure SQL INSERT to register new personnel into the Central Dogma.
     */
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialty) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnecction();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialty()); // e.g., "Neurology", "Sync-Rate Specialist"

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: MAGI Layer failed to execute Personnel INSERT query.");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Extracts the complete personnel matrix, including primary key identity signatures.
     */
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctorList = new ArrayList<>();
        // Extracting 'id' for full-stack identity mapping
        String sql = "SELECT id, name, specialty FROM doctors";

        try (Connection conn = DatabaseConnection.getConnecction();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String specialty = rs.getString("specialty");

                // Assembling the Operational Agent packet with its identity intact
                Doctor doc = new Doctor(id, name, specialty);
                doctorList.add(doc);
            }
        } catch (SQLException e) {
            System.err.println("❌ CRITICAL: MAGI Layer failed to execute Personnel SELECT query.");
            e.printStackTrace();
        }
        return doctorList;
    }
}