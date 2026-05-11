package FitnessPrincess.maps;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class MapManagementController implements Initializable {

    @FXML private TextField searchField;
    @FXML private VBox mapsContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Called automatically after the FXML is loaded.
        // Populate mapsContainer here with real map cards when ready.
    }

    @FXML
    private void onAddMap() {
        // TODO: open dialog or navigate to "add map" screen
        System.out.println("Add map clicked");
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
}