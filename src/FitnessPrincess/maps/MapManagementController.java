package FitnessPrincess.maps;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MapManagementController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ScrollPane mapsScrollPane;
    @FXML private VBox mapsContainer;
    @FXML private Button deleteButton;

    private ObservableList<MapRegion> masterData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            performSearch(newValue);
        });
        loadMaps();
    }

    private HBox buildMapCard(MapRegion region) {
        HBox mapCard = new HBox(12);
        mapCard.getStyleClass().add("map-card");
        mapCard.setAlignment(Pos.CENTER_LEFT);

        StackPane thumbnail = new StackPane();
        thumbnail.getStyleClass().add("map-thumbnail");
        Label icon = new Label("");
        icon.getStyleClass().add("map-thumbnail-icon");
        thumbnail.getChildren().add(icon);

        VBox infoBox = new VBox(3);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setAlignment(Pos.TOP_LEFT);

        Label nameLabel = new Label(region.getName());
        nameLabel.getStyleClass().addAll("card-title", "map-card-name");

        Label subtitleLabel = new Label("Offline coverage for city and surroundings");
        subtitleLabel.getStyleClass().addAll("card-subtitle");

        Label latLabel = new Label(String.format("Latitude:  %.4f (S) to %.4f (N)", region.getLatMin(), region.getLatMax()));
        latLabel.getStyleClass().add("map-coord");

        Label lonLabel = new Label(String.format("Longitude: %.4f (W) to %.4f (E)", region.getLonMin(), region.getLonMax()));
        lonLabel.getStyleClass().add("map-coord");

        infoBox.getChildren().addAll(nameLabel, subtitleLabel, latLabel, lonLabel);
        mapCard.getChildren().addAll(thumbnail, infoBox);

        return mapCard;
    }

    private void refreshCards(List<MapRegion> regions) {
        mapsContainer.getChildren().clear();
        for (MapRegion region : regions) {
            mapsContainer.getChildren().add(buildMapCard(region));
        }
    }

    @FXML
    private void loadMaps() {
        SportActivityApp app = SportActivityApp.getInstance();
        masterData.setAll(app.getMapRegions());
        refreshCards(masterData);
    }

    @FXML
    private void performSearch(String txtSearch) {
        deleteButton.setVisible(txtSearch != null && !txtSearch.isEmpty());

        if (txtSearch == null || txtSearch.isEmpty()) {
            refreshCards(masterData);
        } else {
            List<MapRegion> filtered = masterData.stream()
                    .filter(r -> r.getName().toLowerCase().contains(txtSearch.toLowerCase()))
                    .toList();
            refreshCards(filtered);
        }
    }

    @FXML
    private void onAddMap() {
        try {
            Stage stage = (Stage) searchField.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/maps/MapCreationView.fxml")));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Could not open Map Creation view.");
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteField() {
        searchField.clear();
    }
}