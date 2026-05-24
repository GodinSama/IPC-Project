package FitnessPrincess.activities;

import upv.ipc.sportlib.Activity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

import java.io.File;

public class ActivityMapController {

    @FXML
    private StackPane dropZone;
    @FXML
    private Label dropZoneLabel;
    @FXML
    private TextField activityNameField;

    private File selectedGpxFile = null;

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleImport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a GPX File to Upload");

        // --- Only GPX or XML files ---
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx", "*.xml")
        );

        Stage stage = (Stage) dropZoneLabel.getScene().getWindow();

        // --- Open File Explorer ---
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedGpxFile = selectedFile;
            dropZoneLabel.setText(selectedFile.getName());
            System.out.println("GPX loaded successfully from: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection was cancelled.");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        SportActivityApp app = SportActivityApp.getInstance();
        String activityName = activityNameField.getText();

        if (selectedGpxFile == null || activityName == null || activityName.trim().isEmpty()) {
            System.out.println("Error: Faltan datos por rellenar.");
            return;
        }

        try {
            // --- Import the default activity ---
            Activity nuevaActividad = app.importActivity(selectedGpxFile);

            // --- Persist the custom name to the database using the library's official method ---
            if (nuevaActividad != null && !activityName.trim().isEmpty()) {
                app.renameActivity(nuevaActividad, activityName.trim());
            }

            System.out.println("Activity saved successfully with custom name!");
            goBack(event);

        } catch (Exception e) {
            System.err.println("Error saving the activity.");
            e.printStackTrace();
        }
    }
}