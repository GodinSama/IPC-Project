package FitnessPrincess.dashboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.util.StringConverter;
import upv.ipc.sportlib.*;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    // ── Left panel controls ──────────────────────────────────────────
    @FXML private VBox activitiesContainer;
    @FXML private TextField searchField;
    @FXML private Button deleteButton;

    // ── Map panel controls ───────────────────────────────────────────
    @FXML private ComboBox<MapRegion> mapSelector;
    @FXML private ScrollPane mapScrollPane;
    @FXML private Group mapGroup;
    @FXML private Pane mapPane;
    @FXML private ImageView mapImageView;

    @FXML private ColorPicker markerColorPicker;
    @FXML private ToggleButton addMarkerToggle;
    @FXML private Label mapModeLabel;

    // ── Statistics Footer ────────────────────────────────────────────
    @FXML private Label statDistance;
    @FXML private Label statDuration;
    @FXML private Label statElevation;
    @FXML private Label statSpeed;

    // ── State variables ──────────────────────────────────────────────
    private Activity currentActivity = null;
    private MapProjection currentProjection = null;
    private HBox currentSelectedRow = null;
    private boolean isProgrammaticMapChange = false;
    private List<Activity> allUserActivities;

    // Custom Drag & Zoom control
    private static final double ZOOM_FACTOR = 1.15;
    private double currentZoom = 1.0;
    private double dragStartX, dragStartY;
    private double mapTranslateX = 0, mapTranslateY = 0;
    private final Scale mapScaleTransform = new Scale(1, 1, 0, 0);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        markerColorPicker.setValue(Color.web("#E74C3C"));

        // Setup search listener for filtering
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (deleteButton != null) {
                    deleteButton.setVisible(newVal != null && !newVal.isEmpty());
                }
                filterActivities();
            });
        }

        // Hijack the ScrollPane: Kill scrollbars and default panning
        mapScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mapScrollPane.setPannable(false);
        mapScrollPane.setContent(mapGroup);

        // Intercept scroll wheel to zoom exactly at the mouse pointer's location
        mapScrollPane.addEventFilter(ScrollEvent.ANY, e -> {
            if (e.getDeltaY() > 0) {
                zoomAroundPoint(ZOOM_FACTOR, e.getX(), e.getY());
            } else if (e.getDeltaY() < 0) {
                zoomAroundPoint(1 / ZOOM_FACTOR, e.getX(), e.getY());
            }
            e.consume(); // Stop the scrollpane from trying to scroll vertically
        });

        mapGroup.getTransforms().add(mapScaleTransform);
        setupMapSelector();

        // Custom Mouse Handlers for drag and pan
        mapPane.setOnMousePressed(e -> {
            if (addMarkerToggle.isSelected()) {
                if (currentActivity != null && currentProjection != null) {
                    createMarkerAt(e.getX(), e.getY());
                    addMarkerToggle.setSelected(false);
                    updateModeLabel();
                } else {
                    System.out.println("Select an activity first to add markers to it.");
                    addMarkerToggle.setSelected(false);
                    updateModeLabel();
                }
            } else {
                dragStartX = e.getSceneX() - mapTranslateX;
                dragStartY = e.getSceneY() - mapTranslateY;
            }
        });

        mapPane.setOnMouseDragged(e -> {
            if (!addMarkerToggle.isSelected()) {
                mapTranslateX = e.getSceneX() - dragStartX;
                mapTranslateY = e.getSceneY() - dragStartY;
                applyTransformsAndClamp();
            }
        });

        addMarkerToggle.selectedProperty().addListener((obs, oldVal, newVal) -> updateModeLabel());

        mapScrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(this::clampZoom);
        });

        Platform.runLater(() -> {
            loadAvailableMaps();
            loadUserActivities();
            if (!mapSelector.getItems().isEmpty()) {
                isProgrammaticMapChange = true;
                mapSelector.getSelectionModel().selectFirst();
                isProgrammaticMapChange = false;
                filterActivities();
                renderMap(null, mapSelector.getValue());
            }
        });
    }

    private void setupMapSelector() {
        mapSelector.setConverter(new StringConverter<>() {
            @Override
            public String toString(MapRegion object) {
                return object == null ? "Select Map..." : object.getName();
            }
            @Override
            public MapRegion fromString(String string) {
                return null;
            }
        });
    }

    private void loadAvailableMaps() {
        SportActivityApp app = SportActivityApp.getInstance();
        mapSelector.getItems().setAll(app.getMapRegions());
    }

    private void updateModeLabel() {
        if (addMarkerToggle.isSelected()) {
            mapModeLabel.setText("Marker Mode: Click map to place");
            mapModeLabel.setStyle("-fx-text-fill: -color-error;");
        } else {
            mapModeLabel.setText("Explore Mode");
            mapModeLabel.setStyle("-fx-text-fill: -color-text-muted;");
        }
    }

    private void loadUserActivities() {
        SportActivityApp app = SportActivityApp.getInstance();
        if (app.getCurrentUser() == null) return;

        allUserActivities = app.getUserActivities();
        filterActivities();
    }

    private void filterActivities() {
        if (allUserActivities == null) return;
        activitiesContainer.getChildren().clear();

        MapRegion selectedMap = mapSelector.getValue();
        String searchText = searchField != null && searchField.getText() != null ? searchField.getText().toLowerCase() : "";

        for (Activity activity : allUserActivities) {
            // Filter by map
            boolean mapMatches = false;
            if (selectedMap != null) {
                MapRegion suggested = activity.getSuggestedMap();
                if (suggested != null && suggested.getName().equals(selectedMap.getName())) {
                    mapMatches = true;
                }
            }

            // Filter by search text
            boolean textMatches = true;
            if (!searchText.isEmpty()) {
                String name = activity.getName() == null ? "Unnamed Route" : activity.getName();
                if (!name.toLowerCase().contains(searchText)) {
                    textMatches = false;
                }
            }

            if (mapMatches && textMatches) {
                activitiesContainer.getChildren().add(buildActivityRow(activity));
            }
        }
    }

    private HBox buildActivityRow(Activity activity) {
        HBox row = new HBox(12);
        row.getStyleClass().add("map-list-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinHeight(68);

        // Thumbnail stylized identically to MapManagementController
        StackPane thumb = new StackPane();
        thumb.getStyleClass().add("map-list-thumb");

        Rectangle rect = new Rectangle(48, 48);
        rect.setArcWidth(20);
        rect.setArcHeight(20);
        rect.setFill(Color.web("#2d5a3d"));

        Label ico = new Label("🏃");
        ico.setStyle("-fx-font-size:20px;");
        thumb.getChildren().addAll(rect, ico);

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(activity.getName() == null ? "Unnamed Route" : activity.getName());
        name.getStyleClass().add("list-row-name");

        Label sub = new Label(String.format("%.2f km  |  %s",
                activity.getTotalDistance() / 1000.0,
                formatDuration(activity.getDuration())));
        sub.getStyleClass().add("list-row-sub");

        info.getChildren().addAll(name, sub);
        row.getChildren().addAll(thumb, info);

        row.setOnMouseClicked(e -> {
            if (currentSelectedRow != null) {
                currentSelectedRow.getStyleClass().remove("map-list-row-selected");
            }
            row.getStyleClass().add("map-list-row-selected");
            currentSelectedRow = row;

            this.currentActivity = activity;
            MapRegion defaultRegion = activity.getSuggestedMap();

            if (defaultRegion != null) {
                isProgrammaticMapChange = true;
                mapSelector.setValue(defaultRegion);
                isProgrammaticMapChange = false;
                renderMap(activity, defaultRegion);
            }
        });

        return row;
    }

    @FXML
    private void onMapSelected() {
        if (isProgrammaticMapChange) return;
        MapRegion selectedRegion = mapSelector.getValue();

        if (selectedRegion != null) {
            filterActivities(); // Reload list to only show maps for this region

            // Unset activity if it doesn't belong to the newly selected map
            if (currentActivity != null) {
                MapRegion suggested = currentActivity.getSuggestedMap();
                if (suggested == null || !suggested.getName().equals(selectedRegion.getName())) {
                    currentActivity = null;
                    if (currentSelectedRow != null) {
                        currentSelectedRow.getStyleClass().remove("map-list-row-selected");
                        currentSelectedRow = null;
                    }
                }
            }
            renderMap(currentActivity, selectedRegion);
        }
    }

    @FXML
    private void deleteField() {
        if (searchField != null) searchField.clear();
    }

    private void renderMap(Activity activity, MapRegion region) {
        if (region == null) return;

        try {
            File imgFile = new File(region.getImagePath());
            if (!imgFile.exists()) {
                System.err.println("WARNING: Map image not found at: " + imgFile.getAbsolutePath());
            }

            Image mapImage = new Image(imgFile.toURI().toString(), false);
            mapImageView.setImage(mapImage);

            if (mapImage.isError() || mapImage.getWidth() == 0) return;

            double imgWidth = mapImage.getWidth();
            double imgHeight = mapImage.getHeight();

            mapPane.setPrefSize(imgWidth, imgHeight);
            mapPane.setMinSize(imgWidth, imgHeight);
            mapPane.setMaxSize(imgWidth, imgHeight);

            Rectangle clip = new Rectangle(imgWidth, imgHeight);
            mapPane.setClip(clip);
            mapPane.getChildren().removeIf(node -> node != mapImageView);

            currentProjection = new MapProjection(region, imgWidth, imgHeight);

            if (activity != null) {
                Polyline routeLine = new Polyline();
                routeLine.setStroke(Color.web("#2eb84b"));
                routeLine.setStrokeWidth(3.0);

                for (TrackPoint tp : activity.getTrackPoints()) {
                    Point2D p = currentProjection.project(tp);
                    routeLine.getPoints().addAll(p.getX(), p.getY());
                }
                mapPane.getChildren().add(routeLine);

                for (Annotation ann : activity.getAnnotations()) {
                    drawAnnotation(ann);
                }

                statDistance.setText(String.format("%.2f km", activity.getTotalDistance() / 1000.0));
                statDuration.setText(formatDuration(activity.getDuration()));
                statElevation.setText(String.format("+%.0f m", activity.getElevationGain()));
                statSpeed.setText(String.format("%.1f km/h", activity.getAverageSpeed()));
            } else {
                statDistance.setText("-- km");
                statDuration.setText("--:--:--");
                statElevation.setText("-- m");
                statSpeed.setText("-- km/h");
            }

            Platform.runLater(() -> {
                mapTranslateX = 0;
                mapTranslateY = 0;
                currentZoom = calculateMinZoom();
                applyTransformsAndClamp();
            });

        } catch (Exception e) {
            System.err.println("Error rendering map: " + e.getMessage());
        }
    }

    private void createMarkerAt(double x, double y) {
        GeoPoint geoPoint = currentProjection.unproject(x, y);
        Color selectedColor = markerColorPicker.getValue();
        String hexColor = String.format("#%02X%02X%02X",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));

        Annotation ann = new Annotation(AnnotationType.POINT, "New Marker", hexColor, 2.0, List.of(geoPoint));
        SportActivityApp app = SportActivityApp.getInstance();
        Annotation savedAnn = app.addAnnotation(currentActivity, ann);

        if (savedAnn != null) drawAnnotation(savedAnn);
    }

    private void drawAnnotation(Annotation ann) {
        if (ann.getType() == AnnotationType.POINT && !ann.getGeoPoints().isEmpty()) {
            Point2D p = currentProjection.project(ann.getGeoPoints().get(0));
            Circle marker = new Circle(p.getX(), p.getY(), 6);
            try { marker.setFill(Color.web(ann.getColor())); }
            catch (Exception e) { marker.setFill(Color.RED); }

            marker.setStroke(Color.WHITE);
            marker.setStrokeWidth(2);
            mapPane.getChildren().add(marker);
        }
    }

    @FXML
    private void onZoomIn() {
        if (currentProjection != null) {
            double centerX = mapScrollPane.getWidth() / 2;
            double centerY = mapScrollPane.getHeight() / 2;
            zoomAroundPoint(ZOOM_FACTOR, centerX, centerY);
        }
    }

    @FXML
    private void onZoomOut() {
        if (currentProjection != null) {
            double centerX = mapScrollPane.getWidth() / 2;
            double centerY = mapScrollPane.getHeight() / 2;
            zoomAroundPoint(1 / ZOOM_FACTOR, centerX, centerY);
        }
    }

    private void zoomAroundPoint(double factor, double pivotX, double pivotY) {
        if (currentProjection == null) return;

        double targetZoom = currentZoom * factor;

        double minZoom = calculateMinZoom();
        if (targetZoom < minZoom) targetZoom = minZoom;

        if (targetZoom == currentZoom) return;

        double mapPixelX = (pivotX - mapTranslateX) / currentZoom;
        double mapPixelY = (pivotY - mapTranslateY) / currentZoom;

        currentZoom = targetZoom;

        mapTranslateX = pivotX - (mapPixelX * currentZoom);
        mapTranslateY = pivotY - (mapPixelY * currentZoom);

        applyTransformsAndClamp();
    }

    private void clampZoom() {
        if (currentProjection == null) return;
        double minZoom = calculateMinZoom();
        if (currentZoom < minZoom) {
            currentZoom = minZoom;
        }
        applyTransformsAndClamp();
    }

    private double calculateMinZoom() {
        double viewWidth = mapScrollPane.getViewportBounds().getWidth();
        double viewHeight = mapScrollPane.getViewportBounds().getHeight();

        if (viewWidth <= 0) viewWidth = mapScrollPane.getWidth();
        if (viewHeight <= 0) viewHeight = mapScrollPane.getHeight();

        double mapWidth = mapPane.getPrefWidth();
        double mapHeight = mapPane.getPrefHeight();

        if (viewWidth <= 0 || viewHeight <= 0 || mapWidth <= 0 || mapHeight <= 0) return 1.0;

        return Math.max(viewWidth / mapWidth, viewHeight / mapHeight);
    }

    private void applyTransformsAndClamp() {
        if (mapPane.getPrefWidth() <= 0) return;

        double viewWidth = mapScrollPane.getViewportBounds().getWidth();
        double viewHeight = mapScrollPane.getViewportBounds().getHeight();
        if (viewWidth <= 0) viewWidth = mapScrollPane.getWidth();
        if (viewHeight <= 0) viewHeight = mapScrollPane.getHeight();

        double scaledWidth = mapPane.getPrefWidth() * currentZoom;
        double scaledHeight = mapPane.getPrefHeight() * currentZoom;

        double minX = viewWidth - scaledWidth;
        double minY = viewHeight - scaledHeight;

        mapTranslateX = Math.max(minX, Math.min(0, mapTranslateX));
        mapTranslateY = Math.max(minY, Math.min(0, mapTranslateY));

        mapGroup.setTranslateX(mapTranslateX);
        mapGroup.setTranslateY(mapTranslateY);
        mapScaleTransform.setX(currentZoom);
        mapScaleTransform.setY(currentZoom);
    }

    private String formatDuration(Duration d) {
        if (d == null) return "00:00:00";
        long hours = d.toHours();
        long mins = d.toMinutesPart();
        long secs = d.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, mins, secs);
    }
}