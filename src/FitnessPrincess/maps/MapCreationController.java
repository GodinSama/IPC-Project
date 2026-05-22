package FitnessPrincess.maps;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;
import java.io.File;

public class MapCreationController {

    @FXML private StackPane dropZone;
    @FXML private Label dropZoneLabel;

    @FXML private TextField mapNameField;
    @FXML private TextField latMinField;
    @FXML private TextField latMaxField;
    @FXML private TextField lonMinField;
    @FXML private TextField lonMaxField;
    @FXML private Label errorLabel;

    private File selectedImageFile = null;
    private MapManagementController parentController;

    // Inject the parent controller to handle inner-panel navigation
    public void setParentController(MapManagementController parent) {
        this.parentController = parent;
    }

    @FXML
    private void goBack(ActionEvent event) {
        if (parentController != null) {
            parentController.hideCreationView();
        }
    }

    @FXML
    private void handleDropZone(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File to Upload");

        // Filter for JPEG
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) dropZone.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            selectedImageFile = selectedFile;
            dropZoneLabel.setText(selectedFile.getName());
            System.out.println("File loaded successfully from: " + selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            SportActivityApp app = SportActivityApp.getInstance();
            String mapName = mapNameField.getText();
            double latMin = Double.parseDouble(latMinField.getText());
            double latMax = Double.parseDouble(latMaxField.getText());
            double lonMin = Double.parseDouble(lonMinField.getText());
            double lonMax = Double.parseDouble(lonMaxField.getText());

            MapRegion newRegion = app.addMapRegion(mapName, selectedImageFile, latMin, latMax, lonMin, lonMax);

            if (newRegion != null) {
                System.out.println("Map saved successfully!");
                if (parentController != null) {
                    parentController.loadMaps(); // Refresh the parent's list
                    parentController.hideCreationView(); // Close this panel
                }
            }
        } catch (NumberFormatException e) {
            if (errorLabel != null) {
                errorLabel.setText("Please enter valid numeric coordinates.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        }
    }
}