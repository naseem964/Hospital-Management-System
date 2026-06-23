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

    // Instantiate our working Data Access Object to talk to MySQL
    // A special JavaFX list that automatically updates the visual table grid when data changes
    private final PatientDAO patientDAO = new PatientDAO();
    private TableView<Patient> patientTable;
    private ObservableList<Patient> masterDataList;

    @Override
    public void start(Stage primaryStage) {
        //  Root Container layout split into sections
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ==========================================
        // 1. LEFT SIDE: REGISTRATION FORM LAYOUT
        // ==========================================
        VBox formBox = new VBox(15); // 15px layout spacing between elements
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
        submitBtn.setMaxWidth(Double.MAX_VALUE); // Full-width button

        formBox.getChildren().addAll(
                sectionLabel,
                new Label("Patient Name:"), nameField,
                new Label("Age:"), ageField,
                new Label("Ailment:"), ailmentField,
                submitBtn
        );
        root.setLeft(formBox);

        // ==========================================
        // 2. RIGHT / CENTER: RELATIONAL DATA GRID
        // ==========================================
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(10, 10, 10, 10));

        patientTable = new TableView<>();

        //ADDED: Setup an explicit ID column mapped to the new getId() getter
        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id")); // looks for getId()
        idCol.setPrefWidth(60);

        // Setup individual grid columns mapped directly to Patient object getters
        TableColumn<Patient, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name")); // looks for getName()
        nameCol.setPrefWidth(200);

        TableColumn<Patient, Integer> ageCol = new TableColumn<>("AGE");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));  // looks for getAge()
        ageCol.setPrefWidth(60);

        TableColumn<Patient, String> ailmentCol = new TableColumn<>("AILMENT / DIAGNOSIS");
        ailmentCol.setCellValueFactory(new PropertyValueFactory<>("ailment")); // looks for getAilment()
        ailmentCol.setPrefWidth(300);

        // MODIFIED: Added idCol to the front of the column insertion sequence
        patientTable.getColumns().addAll(idCol, nameCol, ageCol, ailmentCol);

        // Sub-control panel context menu directly under the grid
        HBox controlMenu = new HBox(15);
        controlMenu.setAlignment(Pos.CENTER_RIGHT);

        Button deleteButton = new Button("Delete Selected Patient");
        deleteButton.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
        controlMenu.getChildren().add(deleteButton);

        centerBox.getChildren().addAll(patientTable, controlMenu);
        root.setCenter(centerBox);

        // Populate initial grid data hydration loop
        refreshTableData();

        // ==========================================
        // 3. DATA PIPELINE LOGIC (INTEGRATION)
        // ==========================================

        // Initial Fetch: Read from XAMPP database and fill the visual grid right when it opens
        refreshTableData();

        // Form Submit Action Loop
        submitBtn.setOnAction(event -> {
            // Grab input strings from text fields
            try {
            String inputName = nameField.getText().trim();
            String inputAgeStr = ageField.getText().trim();
            String inputAilment = ailmentField.getText().trim();


                if (inputName.isEmpty() || inputAgeStr.isEmpty() || inputAilment.isEmpty()) {
                    
                    triggerTerminalIntercept(Alert.AlertType.ERROR, "Synchronization Failure", "All registration parameters must be populated. Blank entries are rejected by the Central Dogma.");
                    return;
                }


                int inputAge = Integer.parseInt(inputAgeStr);

                // Package fields into a real Java Patient object blueprint
                Patient newPatient = new Patient(inputName, inputAge, inputAilment);

                // Fire the pipeline execution straight into MySQL
                boolean isSaved = patientDAO.addPatient(newPatient);

                if (isSaved) {
                    System.out.println("✅ Central Dogma updated via GUI input!");

                    // Clear the visual form inputs for the next entry
                    nameField.clear();
                    ageField.clear();
                    ailmentField.clear();

                    // Pull fresh data rows out of MySQL so the table reflects the new insert instantly
                    refreshTableData();
                } else {
                    System.out.println("❌ Database write rejected by persistence tier.");
                }

            } catch (NumberFormatException e) {
                triggerTerminalIntercept(Alert.AlertType.ERROR, "Data Corruption Detected", "Age field must contain a valid numerical integer. Text strings are strictly prohibited.");
            }
        });

        deleteButton.setOnAction(event -> {
            // Identify which unique patient object was highlighted in the data matrix grid
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient == null) {
                System.out.println("⚠ Selection Warning: No patient row was highlighted for execution.");
                return;
            }

            // Bind the operation directly to the exposed database primary key
            if (patientDAO.deletePatient(selectedPatient.getId())) {
                System.out.println("✅ Record ID " + selectedPatient.getId() + " systematically purged from database.");
                refreshTableData(); // Force live structural data-binding synchronization loop
            } else {
                System.out.println("❌ Persistent layer deletion block error occurred.");
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

        primaryStage.setTitle("HMS Relational Database Interface");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Executes a background database read loop, updates the master list collection,
     * and refreshes the live data grid view dynamically.
     */
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

        // Applies our dark-theme stylesheet to the pop-up window so it matches the main terminal
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            // Fallback if stylesheet is missing
        }

        alert.showAndWait();
    }
}