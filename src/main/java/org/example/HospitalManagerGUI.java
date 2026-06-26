package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HospitalManagerGUI extends Application {
    private PatientDAO patientDAO = new PatientDAO();
    private TableView<Patient> table = new TableView<>();
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();

    // Data Entry Fields
    private TextField nameInput = new TextField();
    private TextField ageInput = new TextField();
    private TextField ailmentInput = new TextField();

    // NEW: The Synchro-Link Dropdown Menu
    private ComboBox<Integer> doctorInput = new ComboBox<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MAGI System - Central Dogma Interface");

        // Setup Columns
        TableColumn<Patient, Integer> idColumn = new TableColumn<>("Subject ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> ailmentColumn = new TableColumn<>("Ailment");
        ailmentColumn.setCellValueFactory(new PropertyValueFactory<>("ailment"));

        // NEW: Agent Assignment Column
        TableColumn<Patient, Integer> doctorColumn = new TableColumn<>("Assigned Doc ID");
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorId"));

        table.getColumns().addAll(idColumn, nameColumn, ageColumn, ailmentColumn, doctorColumn);

        // Event Listener: Auto-fill form when a row is clicked
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameInput.setText(newSelection.getName());
                ageInput.setText(String.valueOf(newSelection.getAge()));
                ailmentInput.setText(newSelection.getAilment());
                doctorInput.setValue(newSelection.getDoctorId()); // Pulls down the assigned Doctor
            }
        });

        refreshTable();

        // Setup Input Form UI
        nameInput.setPromptText("Subject Name");
        ageInput.setPromptText("Age");
        ailmentInput.setPromptText("Ailment");

        // Populating the Dropdown with Authorized Personnel IDs
        doctorInput.setPromptText("Assign Doctor ID...");
        doctorInput.getItems().addAll(1, 2, 3, 4, 5); // Assuming you have 5 doctors in the DB

        Button addButton = new Button("Register Subject");
        addButton.setOnAction(e -> addPatient());

        Button updateButton = new Button("Modify Record");
        updateButton.setOnAction(e -> updatePatient());

        Button deleteButton = new Button("Purge Subject");
        deleteButton.setOnAction(e -> deletePatient());

        HBox formLayout = new HBox(10, nameInput, ageInput, ailmentInput, doctorInput, addButton, updateButton, deleteButton);
        formLayout.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, table, formLayout);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 950, 500);

        // UNCOMMENT THIS LINE IF YOU HAVE YOUR style.css IN THE RESOURCES FOLDER!
        // scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTable() {
        patientList.setAll(patientDAO.getAllPatients());
        table.setItems(patientList);
    }

    private void addPatient() {
        try {
            String name = nameInput.getText();
            int age = Integer.parseInt(ageInput.getText());
            String ailment = ailmentInput.getText();

            // Ensure a doctor is selected
            Integer doctorId = doctorInput.getValue();
            if (doctorId == null) {
                System.out.println("❌ CRITICAL: Subject cannot be registered without an assigned Agent!");
                return;
            }

            Patient newPatient = new Patient(name, age, ailment, doctorId);
            patientDAO.addPatient(newPatient);

            // Clear fields after success
            nameInput.clear();
            ageInput.clear();
            ailmentInput.clear();
            doctorInput.setValue(null);

            refreshTable();
        } catch (NumberFormatException e) {
            System.out.println("❌ Form input error. Check age values.");
        }
    }

    private void updatePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setName(nameInput.getText());
                selected.setAge(Integer.parseInt(ageInput.getText()));
                selected.setAilment(ailmentInput.getText());

                if (doctorInput.getValue() != null) {
                    selected.setDoctorId(doctorInput.getValue());
                }

                patientDAO.updatePatient(selected);
                refreshTable();
            } catch (NumberFormatException e) {
                System.out.println("❌ Form input error.");
            }
        }
    }

    private void deletePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            patientDAO.deletePatient(selected.getId());
            refreshTable();
        }
    }
}