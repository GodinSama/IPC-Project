package FitnessPrincess.maps;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapManagementController implements Initializable {

    @FXML private TextField searchField;
    @FXML private VBox mapsContainer;
    @FXML private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
    }

    @FXML
    private void onAddMap() {
        try {
            Stage stage = (Stage) searchField.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/maps/MapCreationView.fxml"))
            );
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Could not open Map Creation view.");
            e.printStackTrace();
        }
    }

    @FXML
    private void performSearch(String txtSearch) {
        System.out.println("Searching for: " + txtSearch);
        if (txtSearch.isEmpty()) {
            deleteButton.setVisible(false);
        } else {
            deleteButton.setVisible(true);
        }
    }

    // ── Bottom tab navigation ─────────────────────────────────────────

    @FXML
    private void showActivities() {
        // TODO: delegate to MainLayoutController or switch view
        System.out.println("Navigate to Activities");
    }

    @FXML
    private void showStatistics() {
        System.out.println("Navigate to Statistics");
    }

    @FXML
    private void showMaps() {
        // Already on Maps — no-op or refresh
        System.out.println("Already on Maps");
    }

    @FXML
    private void deleteField() {
        searchField.clear();
    }
}