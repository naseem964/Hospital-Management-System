package org.example;

public class Launcher {
    public static void main(String[] args)  {
        System.out.println("Initializing system components....");

// 1. Create a dummy patient object using your existing Patient constructor
        // Adjust parameters to match your exact Patient.java constructor fields (e.g., Name, Age, Condition)
        Patient testPatient = new Patient("John Doe", 34, "Sync Ratio Dissociation");

        // 2. Instantiate the Data Access Object and fire the query
        PatientDAO patientDAO = new PatientDAO();
        System.out.println("🔄 Sending test data to MySQL...");

        if (patientDAO.addPatient(testPatient)) {
            System.out.println("✅ Success! John Doe was written to the MySQL 'patients' table.");
        } else {
            System.out.println("❌ Failed to write test patient to database.");
        }

        // Boot up the visual interface layout
        HospitalManagerGUI.main(args);
    }
}