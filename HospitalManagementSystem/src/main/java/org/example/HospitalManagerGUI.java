package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HospitalManagerGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Create a root container pane for the layout
        StackPane root = new StackPane();

        // 2. Add a clean header title label
        Label welcomeLabel = new Label("Hospital Management System Backend");
        welcomeLabel.getStyleClass().add("welcome-text");
        root.getChildren().add(welcomeLabel);

        // 3. Define a crisp canvas size (1024x768 widescreen)
        Scene scene = new Scene(root, 1024, 768);

        // 4. Hook up the external CSS stylesheet for design control
        try {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS Style sheet not found yet, running default layout.");
        }

        // 5. Finalize the window presentation
        primaryStage.setTitle("HMS Relational Database Interface");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}