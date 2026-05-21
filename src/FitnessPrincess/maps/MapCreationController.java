package FitnessPrincess.maps;

import FitnessPrincess.app.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;
import java.io.File;
import java.util.Objects;

public class MapCreationController {

    @FXML
    private StackPane dropZone;
    @FXML
    private Label dropZoneLabel;

    @FXML
    private TextField mapNameField;
    @FXML
    private TextField latMinField;
    @FXML
    private TextField latMaxField;
    @FXML
    private TextField lonMinField;
    @FXML
    private TextField lonMaxField;

    private File selectedImageFile = null;

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml"))
            );
            Parent root = loader.load();

            // Tell MainLayout to show the Maps tab as active
            MainLayoutController mainCtrl = loader.getController();
            mainCtrl.showMapManagement();

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Could not return to Map Management.");
            e.printStackTrace();
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

        // Open the file explorer
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) { // TO-DO - MAKE FOTO APPEAR
            selectedImageFile = selectedFile;
            dropZoneLabel.setText(selectedFile.getName());
            System.out.println("File loaded successfully from: " + selectedFile.getAbsolutePath());
        } else {
            System.out.println("File selection was cancelled.");
        }
    }

    @FXML
    private void handleSave(ActionEvent event) { // check and play errors if values are not acceptable
        SportActivityApp app = SportActivityApp.getInstance();
        String mapName = mapNameField.getText();
        double latMin = Double.parseDouble(latMinField.getText());
        double latMax = Double.parseDouble(latMaxField.getText());
        double lonMin = Double.parseDouble(lonMinField.getText());
        double lonMax = Double.parseDouble(lonMaxField.getText());


        MapRegion newRegion = app.addMapRegion(mapName, selectedImageFile, latMin, latMax, lonMin, lonMax);

        if (newRegion != null) {
            System.out.println("Map saved successfully!");
            goBack(event);
        }
    }

}