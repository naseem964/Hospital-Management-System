package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class HospitalManagerGUI extends Application {

    // Instantiate our MAGI Data Access Object to talk to the Central Dogma
    private final PatientDAO patientDAO = new PatientDAO();
    private TableView<Patient> patientTable;
    private ObservableList<Patient> masterDataList;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ==========================================
        // 1. LEFT SIDE: REGISTRATION FORM LAYOUT
        // ==========================================
        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(10, 20, 10, 10));
        formBox.setPrefWidth(300);
        formBox.setAlignment(Pos.TOP_LEFT);

        Label sectionLabel = new Label("REGISTER NEW SUBJECT");
        sectionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Patient Full Name");

        TextField ageField = new TextField();
        ageField.setPromptText("Enter Patient Age");

        TextField ailmentField = new TextField();
        ailmentField.setPromptText("Identify Clinical Ailment");

        Button submitBtn = new Button("Sync to Central Dogma");
        submitBtn.setMaxWidth(Double.MAX_VALUE);

        Button updateButton = new Button("Modify Subject Record");
        updateButton.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-weight: bold;");
        updateButton.setMaxWidth(Double.MAX_VALUE);

        // PERFECTLY SYNCED: Variables match exactly what is defined above
        formBox.getChildren().addAll(sectionLabel, new Label("Name:"), nameField,
                new Label("Age:"), ageField, new Label("Ailment:"),
                ailmentField, submitBtn, updateButton);

        root.setLeft(formBox);

        // ==========================================
        // 2. RIGHT / CENTER: RELATIONAL DATA GRID
        // ==========================================
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(10, 10, 10, 10));

        patientTable = new TableView<>();

        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Patient, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Patient, Integer> ageCol = new TableColumn<>("AGE");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setPrefWidth(60);

        TableColumn<Patient, String> ailmentCol = new TableColumn<>("AILMENT / DIAGNOSIS");
        ailmentCol.setCellValueFactory(new PropertyValueFactory<>("ailment"));
        ailmentCol.setPrefWidth(300);

        patientTable.getColumns().addAll(idCol, nameCol, ageCol, ailmentCol);

        HBox controlMenu = new HBox(15);
        controlMenu.setAlignment(Pos.CENTER_RIGHT);

        Button deleteButton = new Button("Delete Selected Patient");
        deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
        controlMenu.getChildren().add(deleteButton);

        centerBox.getChildren().addAll(patientTable, controlMenu);
        root.setCenter(centerBox);

        refreshTableData();

        // ==========================================
        // 3. DATA PIPELINE LOGIC (INTEGRATION)
        // ==========================================

        // --- NEW: MAGI SYSTEM AUTO-FILL LISTENER ---
        // Auto-fills terminal fields when an operator clicks a row
        patientTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                ageField.setText(String.valueOf(newSelection.getAge()));
                ailmentField.setText(newSelection.getAilment());
            }
        });

        // --- NEW: RECORD MUTATION / UPDATE EXECUTION ---
        updateButton.setOnAction(event -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                triggerTerminalIntercept(Alert.AlertType.WARNING, "Target Lock Missing", "You must select a Subject from the grid before initiating an overwrite.");
                return;
            }

            try {
                String updatedName = nameField.getText().trim();
                int updatedAge = Integer.parseInt(ageField.getText().trim());
                String updatedAilment = ailmentField.getText().trim();

                if (updatedName.isEmpty() || updatedAilment.isEmpty()) {
                    triggerTerminalIntercept(Alert.AlertType.ERROR, "Synchronization Failure", "Text streams cannot be empty during an overwrite.");
                    return;
                }

                // Inject the updated data into the runtime object
                selectedPatient.setName(updatedName);
                selectedPatient.setAge(updatedAge);
                selectedPatient.setAilment(updatedAilment);

                // Pass the object to the DAO for permanent Central Dogma modification
                if (patientDAO.updatePatient(selectedPatient)) {
                    refreshTableData();
                    nameField.clear();
                    ageField.clear();
                    ailmentField.clear();
                    triggerTerminalIntercept(Alert.AlertType.INFORMATION, "Overwrite Complete", "Subject data has been successfully re-synchronized.");
                } else {
                    triggerTerminalIntercept(Alert.AlertType.ERROR, "Database Rejection", "The Central Dogma rejected the overwrite command.");
                }
            } catch (NumberFormatException e) {
                triggerTerminalIntercept(Alert.AlertType.ERROR, "Data Corruption Detected", "Age field must contain a numerical integer.");
            }
        });

        // --- EXISTING: CREATION EXECUTION ---
        submitBtn.setOnAction(event -> {
            try {
                String inputName = nameField.getText().trim();
                String inputAgeStr = ageField.getText().trim();
                String inputAilment = ailmentField.getText().trim();

                if (inputName.isEmpty() || inputAgeStr.isEmpty() || inputAilment.isEmpty()) {
                    triggerTerminalIntercept(Alert.AlertType.ERROR, "Synchronization Failure", "All registration parameters must be populated. Blank entries are rejected by the Central Dogma.");
                    return;
                }

                int inputAge = Integer.parseInt(inputAgeStr);
                Patient newPatient = new Patient(inputName, inputAge, inputAilment);

                if (patientDAO.addPatient(newPatient)) {
                    System.out.println("✅ Central Dogma updated via GUI input!");
                    nameField.clear();
                    ageField.clear();
                    ailmentField.clear();
                    refreshTableData();
                } else {
                    System.out.println("❌ Database write rejected by persistence tier.");
                }
            } catch (NumberFormatException e) {
                triggerTerminalIntercept(Alert.AlertType.ERROR, "Data Corruption Detected", "Age field must contain a valid numerical integer. Text strings are strictly prohibited.");
            }
        });

        // --- EXISTING: PURGE EXECUTION ---
        deleteButton.setOnAction(event -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                triggerTerminalIntercept(Alert.AlertType.WARNING, "Selection Warning", "No subject row was highlighted for execution.");
                return;
            }

            if (patientDAO.deletePatient(selectedPatient.getId())) {
                System.out.println("✅ Record ID " + selectedPatient.getId() + " systematically purged from database.");
                refreshTableData();
            } else {
                triggerTerminalIntercept(Alert.AlertType.ERROR, "Purge Failure", "Persistent layer deletion block error occurred.");
            }
        });

        // ==========================================
        // 4. DISPLAY INITIALIZATION
        // ==========================================
        Scene scene = new Scene(root, 1024, 768);
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS Style sheet not found yet, running default layout configuration.");
        }

        primaryStage.setTitle("MAGI System Interface - Subject Tracking");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTableData() {
        List<Patient> currentDbList = patientDAO.getAllPatients();
        masterDataList = FXCollections.observableArrayList(currentDbList);
        patientTable.setItems(masterDataList);
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * MAGI SYSTEM COMMAND: Generates a visual terminal intercept alert for the operator.
     */
    private void triggerTerminalIntercept(Alert.AlertType alertType, String header, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "CRITICAL SYSTEM WARNING" : "MAGI NOTIFICATION");
        alert.setHeaderText(header);
        alert.setContentText(message);

        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback if stylesheet is missing
        }

        alert.showAndWait();
    }
}