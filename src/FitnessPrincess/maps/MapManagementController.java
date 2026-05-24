package FitnessPrincess.maps;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import upv.ipc.sportlib.MapRegion;
import upv.ipc.sportlib.SportActivityApp;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MapManagementController implements Initializable {

    private static final double MOBILE_BREAKPOINT = 800;

    // ── Layout skeleton ──────────────────────────────────────────────
    @FXML private HBox      rootContainer;
    @FXML private VBox      leftPanel;
    @FXML private Separator panelDivider;
    @FXML private StackPane detailPanel;

    // ── Left panel ───────────────────────────────────────────────────
    @FXML private VBox      topBar;
    @FXML private HBox      titleRow;
    @FXML private HBox      searchRow;
    @FXML private HBox      searchBar;
    @FXML private Region    titleSpacer;
    @FXML private Button    addMapBtn;
    @FXML private TextField searchField;
    @FXML private Button    deleteButton;
    @FXML private VBox      mapsContainer;

    // ── Right panel ──────────────────────────────────────────────────
    @FXML private VBox      emptyState;
    @FXML private VBox      detailContent;
    @FXML private StackPane detailImagePane;
    @FXML private Label     detailName;
    @FXML private Label     detailSubtitle;
    @FXML private Label     detailLat;
    @FXML private Label     detailLon;

    // ── State ────────────────────────────────────────────────────────
    private boolean pcMode = false;
    private MapRegion selectedRegion = null;
    private final ObservableList<MapRegion> masterData = FXCollections.observableArrayList();

    // Active embedded creation view
    private Node activeCreationView = null;

    // ════════════════════════════════════════════════════════════════
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchField.textProperty().addListener((obs, o, n) -> performSearch(n));

        mapsContainer.setFillWidth(true);

        rootContainer.widthProperty().addListener((obs, oldW, newW) -> {
            if (newW != null && newW.doubleValue() > 0) {
                applyLayout(newW.doubleValue());
            }
        });

        Platform.runLater(() -> {
            double w = rootContainer.getWidth();
            if (w <= 0 && rootContainer.getScene() != null) {
                w = rootContainer.getScene().getWidth();
            }
            if (w > 0) applyLayout(w);

            loadMaps();
        });
    }

    private HBox buildListRow(MapRegion region) {
        HBox row = new HBox(12);
        row.getStyleClass().add("map-list-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(68);
        row.setMaxWidth(Double.MAX_VALUE);

        StackPane thumb = new StackPane();
        thumb.getStyleClass().add("map-list-thumb");

        Rectangle rect = new Rectangle(48, 48);
        rect.setArcWidth(20);
        rect.setArcHeight(20);

        try {
            String url = new java.io.File(region.getImagePath()).toURI().toString();
            Image img = new Image(url, false);
            if (!img.isError()) {
                double z = 12.0, off = (1.0 - z) / 2.0;
                rect.setFill(new ImagePattern(img, off, off, z, z, true));
            } else {
                rect.setFill(javafx.scene.paint.Color.web("#2d5a3d"));
                Label ico = new Label("🗺");
                ico.setStyle("-fx-font-size:18px;");
                thumb.getChildren().add(ico);
            }
        } catch (Exception e) {
            rect.setFill(javafx.scene.paint.Color.web("#2d5a3d"));
        }
        thumb.getChildren().add(0, rect);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label name = new Label(region.getName());
        name.getStyleClass().add("list-row-name");
        Label sub = new Label(String.format("%.2f°–%.2f° N  |  %.2f°–%.2f° E",
                region.getLatMin(), region.getLatMax(),
                region.getLonMin(), region.getLonMax()));
        sub.getStyleClass().add("list-row-sub");
        info.getChildren().addAll(name, sub);

        row.getChildren().addAll(thumb, info);
        row.setOnMouseClicked(e -> selectRegion(region, row));

        return row;
    }

    private HBox buildMapCard(MapRegion region) {
        HBox mapCard = new HBox(12);
        mapCard.getStyleClass().add("map-card");
        mapCard.setAlignment(Pos.CENTER_LEFT);
        mapCard.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(mapCard, Priority.ALWAYS);

        StackPane thumbnail = new StackPane();
        thumbnail.getStyleClass().add("map-thumbnail");

        Rectangle mapImage = new Rectangle(84, 74);
        mapImage.setArcWidth(20);
        mapImage.setArcHeight(20);
        mapImage.getStyleClass().add("map-thumbnail-image");

        Label fallbackIcon = new Label("🗺");
        fallbackIcon.getStyleClass().add("map-thumbnail-icon");

        try {
            String imageUrl = new java.io.File(region.getImagePath()).toURI().toString();
            Image img = new Image(imageUrl, false);
            if (img.isError()) {
                thumbnail.getChildren().setAll(fallbackIcon);
            } else {
                double z = 16.0, off = (1.0 - z) / 2.0;
                mapImage.setFill(new ImagePattern(img, off, off, z, z, true));
                thumbnail.getChildren().add(mapImage);
            }
        } catch (Exception e) {
            thumbnail.getChildren().setAll(fallbackIcon);
        }

        VBox infoBox = new VBox(3);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        infoBox.setAlignment(Pos.TOP_LEFT);
        infoBox.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label(region.getName());
        nameLabel.getStyleClass().addAll("card-title", "map-card-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        Label latLabel = new Label(String.format("Lat:  %.4f → %.4f", region.getLatMin(), region.getLatMax()));
        latLabel.getStyleClass().add("map-coord");
        latLabel.setWrapText(true);
        latLabel.setMaxWidth(Double.MAX_VALUE);
        Label lonLabel = new Label(String.format("Lon: %.4f → %.4f", region.getLonMin(), region.getLonMax()));
        lonLabel.getStyleClass().add("map-coord");
        lonLabel.setWrapText(true);
        lonLabel.setMaxWidth(Double.MAX_VALUE);

        infoBox.getChildren().addAll(nameLabel, latLabel, lonLabel);
        mapCard.getChildren().addAll(thumbnail, infoBox);
        return mapCard;
    }

    private HBox currentSelectedRow = null;

    private void selectRegion(MapRegion region, HBox row) {
        // If they are creating a map, cancel it to look at the new selected region
        if (activeCreationView != null) {
            hideCreationView();
        }

        selectedRegion = region;

        if (currentSelectedRow != null)
            currentSelectedRow.getStyleClass().remove("map-list-row-selected");
        row.getStyleClass().add("map-list-row-selected");
        currentSelectedRow = row;

        detailName.setText(region.getName());
        detailSubtitle.setText("Offline coverage for city and surroundings");
        detailLat.setText(String.format("%.4f° S  →  %.4f° N", region.getLatMin(), region.getLatMax()));
        detailLon.setText(String.format("%.4f° W  →  %.4f° E", region.getLonMin(), region.getLonMax()));

        detailImagePane.getChildren().clear();
        try {
            String url = new java.io.File(region.getImagePath()).toURI().toString();
            Image img = new Image(url, false);
            if (!img.isError()) {
                Rectangle rect = new Rectangle();
                rect.widthProperty().bind(detailImagePane.widthProperty());
                rect.heightProperty().bind(detailImagePane.heightProperty());
                double z = 4.0, off = (1.0 - z) / 2.0;
                rect.setFill(new ImagePattern(img, off, off, z, z, true));
                detailImagePane.getChildren().add(rect);
            }
        } catch (Exception ignored) {}

        emptyState.setVisible(false);
        emptyState.setManaged(false);
        detailContent.setVisible(true);
        detailContent.setManaged(true);
    }

    private void refreshCards(List<MapRegion> regions) {
        mapsContainer.getChildren().clear();
        for (MapRegion region : regions) {
            mapsContainer.getChildren().add(
                    pcMode ? buildListRow(region) : buildMapCard(region)
            );
        }
    }

    @FXML
    public void loadMaps() {
        SportActivityApp app = SportActivityApp.getInstance();
        masterData.setAll(app.getMapRegions());
        refreshCards(masterData);
    }

    private void applyLayout(double width) {
        boolean isMobile  = width < MOBILE_BREAKPOINT;
        boolean wasPcMode = pcMode;
        pcMode = !isMobile;

        panelDivider.setVisible(!isMobile);
        panelDivider.setManaged(!isMobile);
        detailPanel.setVisible(!isMobile);
        detailPanel.setManaged(!isMobile);

        if (isMobile) {
            leftPanel.setMinWidth(Region.USE_COMPUTED_SIZE);
            leftPanel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            leftPanel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(leftPanel, Priority.ALWAYS);
        } else {
            leftPanel.setMinWidth(320);
            leftPanel.setPrefWidth(320);
            leftPanel.setMaxWidth(320);
            HBox.setHgrow(leftPanel, Priority.NEVER);
        }

        if (pcMode != wasPcMode) {
            refreshCards(masterData);
            selectedRegion = null;
            currentSelectedRow = null;
            hideCreationView(); // Clear layout cleanly on resize

            emptyState.setVisible(true);
            emptyState.setManaged(true);
            detailContent.setVisible(false);
            detailContent.setManaged(false);
        }
    }

    @FXML
    private void performSearch(String txt) {
        deleteButton.setVisible(txt != null && !txt.isEmpty());
        if (txt == null || txt.isEmpty()) {
            refreshCards(masterData);
        } else {
            List<MapRegion> filtered = masterData.stream()
                    .filter(r -> r.getName().toLowerCase().contains(txt.toLowerCase()))
                    .toList();
            refreshCards(filtered);
        }
    }

    @FXML
    private void onAddMap() {
        // Prevent stacking views if one is already open
        if (activeCreationView != null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FitnessPrincess/maps/MapCreationView.fxml"));
            Parent root = loader.load();

            // Link the child to the parent
            MapCreationController ctrl = loader.getController();
            ctrl.setParentController(this);

            // Add view overlay to detailPanel
            activeCreationView = root;
            detailPanel.getChildren().add(root);

            // Disable the add button
            if (addMapBtn != null) {
                addMapBtn.setDisable(true);
            }

            // Hide normal content
            emptyState.setVisible(false);
            emptyState.setManaged(false);
            detailContent.setVisible(false);
            detailContent.setManaged(false);

            // For mobile, switch to showing the right panel
            if (!pcMode) {
                leftPanel.setVisible(false);
                leftPanel.setManaged(false);
                detailPanel.setVisible(true);
                detailPanel.setManaged(true);
            }

        } catch (Exception e) {
            System.err.println("Could not open embedded Map Creation view.");
            e.printStackTrace();
        }
    }

    // Called by the child controller to close itself
    public void hideCreationView() {
        if (activeCreationView != null) {
            detailPanel.getChildren().remove(activeCreationView);
            activeCreationView = null;
        }

        // Re-enable the add button
        if (addMapBtn != null) {
            addMapBtn.setDisable(false);
        }

        if (pcMode) {
            // Restore previous state depending on selection
            if (selectedRegion != null) {
                detailContent.setVisible(true);
                detailContent.setManaged(true);
            } else {
                emptyState.setVisible(true);
                emptyState.setManaged(true);
            }
        } else {
            // For mobile, go back to the left list
            leftPanel.setVisible(true);
            leftPanel.setManaged(true);
            detailPanel.setVisible(false);
            detailPanel.setManaged(false);
        }
    }

    @FXML
    private void onEditMap() {
        // TODO: open edit view for selectedRegion
    }

    @FXML
    private void onDeleteMap() {
        // 1. Double-check that a region is actually selected
        if (selectedRegion == null) {
            return;
        }

        // 2. Show a confirmation popup
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Map");
        alert.setHeaderText("Delete '" + selectedRegion.getName() + "'?");
        alert.setContentText("Are you sure you want to delete this map? This action cannot be undone.");

        // 3. Wait for the user to click OK or Cancel
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {

                // 4. Remove it from your database backend
                SportActivityApp app = SportActivityApp.getInstance();

                // Note: If your method in SportActivityApp is named differently
                // (e.g., deleteMap, removeRegion), change this line to match it:
                app.removeMapRegion(selectedRegion);

                // 5. Clear the selection state
                selectedRegion = null;
                if (currentSelectedRow != null) {
                    currentSelectedRow.getStyleClass().remove("map-list-row-selected");
                    currentSelectedRow = null;
                }

                // 6. Reload the map list so the deleted map disappears from the sidebar
                loadMaps();

                // 7. Hide the right-side detail panel and bring back the empty state
                detailContent.setVisible(false);
                detailContent.setManaged(false);
                emptyState.setVisible(true);
                emptyState.setManaged(true);
            }
        });
    }

    @FXML
    private void onFavMap() {
        // TODO: FAV selectedRegion + put tpo the top
    }

    @FXML
    private void deleteField() {
        searchField.clear();
    }
}
