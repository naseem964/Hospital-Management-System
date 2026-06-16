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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class HospitalManagerGUI extends Application {

    // Instantiate our working Data Access Object to talk to MySQL
    private final PatientDAO patientDAO = new PatientDAO();

    // A special JavaFX list that automatically updates the visual table grid when data changes
    private TableView<Patient> patientTable;
    private ObservableList<Patient> masterDataList;

    @Override
    public void start(Stage primaryStage) {
        // 1. Root Container layout split into sections
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // ==========================================
        // 2. LEFT SIDE: REGISTRATION FORM LAYOUT
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
        // 3. RIGHT / CENTER: RELATIONAL DATA GRID
        // ==========================================
        patientTable = new TableView<>();

        // Setup individual grid columns mapped directly to Patient object getters
        TableColumn<Patient, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name")); // looks for getName()
        nameCol.setPrefWidth(250);

        TableColumn<Patient, Integer> ageCol = new TableColumn<>("AGE");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));  // looks for getAge()
        ageCol.setPrefWidth(80);

        TableColumn<Patient, String> ailmentCol = new TableColumn<>("AILMENT / DIAGNOSIS");
        ailmentCol.setCellValueFactory(new PropertyValueFactory<>("ailment")); // looks for getAilment()
        ailmentCol.setPrefWidth(350);

        patientTable.getColumns().addAll(nameCol, ageCol, ailmentCol);
        root.setCenter(patientTable);

        // ==========================================
        // 4. DATA PIPELINE LOGIC (INTEGRATION)
        // ==========================================

        // Initial Fetch: Read from XAMPP database and fill the visual grid right when it opens
        refreshTableData();

        // Form Submit Action Loop
        submitBtn.setOnAction(event -> {
            // Grab input strings from text fields
            String inputName = nameField.getText().trim();
            String inputAgeStr = ageField.getText().trim();
            String inputAilment = ailmentField.getText().trim();

            // Simple validation to ensure fields aren't completely blank
            if (inputName.isEmpty() || inputAgeStr.isEmpty() || inputAilment.isEmpty()) {
                System.out.println("❌ Form Error: All registration parameters must be populated.");
                return;
            }

            try {
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
                System.out.println("❌ Input Validation Failure: Age field must contain a numerical integer.");
            }
        });

        // ==========================================
        // 5. WINDOW CONFIGURATION
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
}