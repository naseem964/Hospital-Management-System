package org.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    private ComboBox<Integer> doctorInput = new ComboBox<>();

    // NEW: Search Bar
    private TextField searchField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("MAGI System - Central Dogma Interface");

        // --- 1. SETUP COLUMNS ---
        TableColumn<Patient, Integer> idColumn = new TableColumn<>("Subject ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, Integer> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> ailmentColumn = new TableColumn<>("Ailment");
        ailmentColumn.setCellValueFactory(new PropertyValueFactory<>("ailment"));

        TableColumn<Patient, Integer> doctorColumn = new TableColumn<>("Assigned Doc ID");
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorId"));

        table.getColumns().addAll(idColumn, nameColumn, ageColumn, ailmentColumn, doctorColumn);

        // --- 2. NEW: REAL-TIME SEARCH FILTER ---
        searchField.setPromptText("Search Subject Name or Ailment...");
        searchField.setPrefWidth(300);

        FilteredList<Patient> filteredData = new FilteredList<>(patientList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) return true; // Show all if search is empty
                String lowerCaseFilter = newValue.toLowerCase();

                if (patient.getName().toLowerCase().contains(lowerCaseFilter)) return true;
                if (patient.getAilment().toLowerCase().contains(lowerCaseFilter)) return true;
                return false; // Does not match
            });
        });

        SortedList<Patient> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        // --- 3. AUTO-FILL EVENT LISTENER ---
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameInput.setText(newSelection.getName());
                ageInput.setText(String.valueOf(newSelection.getAge()));
                ailmentInput.setText(newSelection.getAilment());
                doctorInput.setValue(newSelection.getDoctorId());
            }
        });

        refreshTable();

        // --- 4. FORM UI & BUTTONS ---
        nameInput.setPromptText("Subject Name");
        ageInput.setPromptText("Age");
        ailmentInput.setPromptText("Ailment");

        doctorInput.setPromptText("Assign Doctor ID...");
        doctorInput.getItems().addAll(1, 2, 3, 4, 5);

        Button addButton = new Button("Register Subject");
        addButton.setOnAction(e -> addPatient());

        Button updateButton = new Button("Modify Record");
        updateButton.setOnAction(e -> updatePatient());

        Button deleteButton = new Button("Purge Subject");
        deleteButton.setStyle("-fx-background-color: #ff4c4c; -fx-text-fill: white; -fx-font-weight: bold;");
        deleteButton.setOnAction(e -> deletePatient());

        HBox topLayout = new HBox(10, new Label("Search Registry:"), searchField);
        topLayout.setPadding(new Insets(0, 0, 10, 0));

        HBox formLayout = new HBox(10, nameInput, ageInput, ailmentInput, doctorInput, addButton, updateButton, deleteButton);
        formLayout.setPadding(new Insets(10, 0, 0, 0));

        VBox mainLayout = new VBox(10, topLayout, table, formLayout);
        mainLayout.setPadding(new Insets(20));

        Scene scene = new Scene(mainLayout, 950, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void refreshTable() {
        patientList.setAll(patientDAO.getAllPatients());
    }

    private void addPatient() {
        try {
            Integer doctorId = doctorInput.getValue();
            if (doctorId == null) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "A Subject must be assigned to an Agent (Doctor ID).");
                return;
            }
            Patient newPatient = new Patient(nameInput.getText(), Integer.parseInt(ageInput.getText()), ailmentInput.getText(), doctorId);
            if (patientDAO.addPatient(newPatient)) {
                clearForm();
                refreshTable();
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Age must be a valid number.");
        }
    }

    private void updatePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setName(nameInput.getText());
                selected.setAge(Integer.parseInt(ageInput.getText()));
                selected.setAilment(ailmentInput.getText());
                if (doctorInput.getValue() != null) selected.setDoctorId(doctorInput.getValue());

                patientDAO.updatePatient(selected);
                refreshTable();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Age must be a valid number.");
            }
        }
    }

    // NEW: Defensive Delete with Confirmation Popup
    private void deletePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you want to permanently purge " + selected.getName() + " from the database?",
                    ButtonType.YES, ButtonType.NO);
            confirmDialog.setTitle("CRITICAL WARNING");
            confirmDialog.setHeaderText("Data Purge Authorization Required");

            confirmDialog.showAndWait();

            if (confirmDialog.getResult() == ButtonType.YES) {
                patientDAO.deletePatient(selected.getId());
                clearForm();
                refreshTable();
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a Subject to purge.");
        }
    }

    private void clearForm() {
        nameInput.clear();
        ageInput.clear();
        ailmentInput.clear();
        doctorInput.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}