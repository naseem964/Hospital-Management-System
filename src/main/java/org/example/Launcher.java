package org.example;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("[CORE] Initializing MAGI system...");

        try {
            // Verify DAO exists to satisfy compiler checks
            PatientDAO patientDAO = new PatientDAO();
            System.out.println("[DAO] Data Access Object layer bound. Variable status: " + patientDAO.toString());

            // Hand off to JavaFX
            HospitalManagerGUI.launch(HospitalManagerGUI.class, args);

        } catch (Exception e) {
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "System boot failed", e);
        }
    }
}