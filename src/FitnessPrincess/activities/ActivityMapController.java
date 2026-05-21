package FitnessPrincess.activities;

import upv.ipc.sportlib.Activity;
import FitnessPrincess.app.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import java.io.File;
import java.util.Objects;

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
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml"))
            );
            Parent root = loader.load();

            // MainLayout activates Actividades
            MainLayoutController mainCtrl = loader.getController();
            mainCtrl.showActivities();

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Could not return to Dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleImport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a GPX File to Upload");

        // Only GPX or XML files
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("GPX Files", "*.gpx", "*.xml")
        );

        Stage stage = (Stage) dropZoneLabel.getScene().getWindow();

        // Open File Explorer
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

        if (selectedGpxFile == null || activityName.isEmpty()) {
            System.out.println("Error: Faltan datos por rellenar.");
            return;
        }

        try {
            // Add new activity
            
            Activity nuevaActividad = app.importActivity(selectedGpxFile);

            // If the user wrote a name
            if (!activityName.isEmpty()) { 
                nuevaActividad.setName(activityName);
            }
            
            System.out.println("Activity saved successfully!");
            goBack(event);

        } catch (Exception e) {
            System.err.println("Error saving the activity.");
            e.printStackTrace();
        }
    }
}