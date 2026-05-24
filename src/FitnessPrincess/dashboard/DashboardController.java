package FitnessPrincess.dashboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

    @FXML private ToggleButton addMarkerToggle;
    @FXML private Button toggleMenuBtn;
    @FXML private Label mapModeLabel;

    // ── Statistics Footer ────────────────────────────────────────────
    @FXML private Label statDistance;
    @FXML private Label statDuration;
    @FXML private Label statElevation;
    @FXML private Label statSpeed;
    @FXML private VBox bottomStatsContainer;

    // ── Elevation Chart ──────────────────────────────────────────────
    @FXML private StackPane chartContainer;
    @FXML private LineChart<Number, Number> elevationChart;
    @FXML private Pane chartOverlay;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    // Markers for tracking user mouse
    private Circle graphHoverMarker = new Circle(6, Color.web("#3498db"));
    private Circle chartHoverMarker = new Circle(5, Color.web("#3498db"));

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
    private double lastRightClickX = 0, lastRightClickY = 0;
    private final Scale mapScaleTransform = new Scale(1, 1, 0, 0);

    // Menu tracking to prevent overlaps
    private ContextMenu mapContextMenu;
    private ContextMenu currentActiveMarkerMenu;

    private boolean isMenuExpanded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Hide the toggle button since we use context menu now
        if (addMarkerToggle != null) {
            addMarkerToggle.setVisible(false);
            addMarkerToggle.setManaged(false);
        }

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

        // Setup map hover marker
        graphHoverMarker.setStroke(Color.WHITE);
        graphHoverMarker.setStrokeWidth(2);
        graphHoverMarker.setVisible(false);
        graphHoverMarker.setMouseTransparent(true);

        // Setup chart hover marker
        chartHoverMarker.setStroke(Color.WHITE);
        chartHoverMarker.setStrokeWidth(1.5);
        chartHoverMarker.setVisible(false);
        if (chartOverlay != null) {
            chartOverlay.getChildren().add(chartHoverMarker);
        }

        // Set up the elevation chart mouse hover listener
        if (elevationChart != null && xAxis != null) {
            elevationChart.setOnMouseMoved(e -> {
                if (currentActivity == null || currentProjection == null) return;

                // Convert mouse position to X axis coordinate
                double xInAxis = xAxis.sceneToLocal(e.getSceneX(), e.getSceneY()).getX();
                if (xInAxis >= 0 && xInAxis <= xAxis.getWidth()) {
                    Number xValue = xAxis.getValueForDisplay(xInAxis);
                    double targetDistance = xValue.doubleValue();

                    // Find closest trackpoint by cumulative distance
                    double currentDist = 0;
                    TrackPoint prev = null;
                    TrackPoint closest = null;
                    double minDiff = Double.MAX_VALUE;
                    double closestDist = 0;

                    for (TrackPoint tp : currentActivity.getTrackPoints()) {
                        if (prev != null) {
                            currentDist += prev.distanceTo(tp) / 1000.0;
                        }
                        double diff = Math.abs(currentDist - targetDistance);
                        if (diff < minDiff) {
                            minDiff = diff;
                            closest = tp;
                            closestDist = currentDist;
                        }
                        prev = tp;
                    }

                    // Highlight on the map
                    if (closest != null) {
                        Point2D p = currentProjection.project(closest);
                        graphHoverMarker.setCenterX(p.getX());
                        graphHoverMarker.setCenterY(p.getY());
                        graphHoverMarker.setVisible(true);

                        // Highlight on the chart
                        Node plotBackground = elevationChart.lookup(".chart-plot-background");
                        if (plotBackground != null && chartOverlay != null) {
                            double displayX = xAxis.getDisplayPosition(closestDist);
                            double displayY = yAxis.getDisplayPosition(closest.getElevation());

                            Point2D pt = chartOverlay.sceneToLocal(plotBackground.localToScene(displayX, displayY));
                            chartHoverMarker.setCenterX(pt.getX());
                            chartHoverMarker.setCenterY(pt.getY());
                            chartHoverMarker.setVisible(true);
                        }
                    }
                }
            });

            elevationChart.setOnMouseExited(e -> {
                graphHoverMarker.setVisible(false);
                chartHoverMarker.setVisible(false);
            });
        }

        // --- Context Menu Setup ---
        mapContextMenu = new ContextMenu();
        MenuItem addAnnotationItem = new MenuItem("📍 Add Annotation");
        mapContextMenu.getItems().add(addAnnotationItem);

        addAnnotationItem.setOnAction(e -> {
            if (currentActivity != null && currentProjection != null) {
                showAnnotationPopup(lastRightClickX, lastRightClickY);
            } else {
                System.out.println("Select an activity first to add markers to it.");
            }
        });

        // Custom Mouse Handlers for drag, pan, and right-click
        mapPane.setOnMousePressed(e -> {
            // Hide active marker menus if we click anywhere on the map
            if (currentActiveMarkerMenu != null) {
                currentActiveMarkerMenu.hide();
                currentActiveMarkerMenu = null;
            }

            if (e.getButton() == MouseButton.SECONDARY) {
                // Right Click -> Show Context Menu
                lastRightClickX = e.getX();
                lastRightClickY = e.getY();
                mapContextMenu.show(mapPane, e.getScreenX(), e.getScreenY());
            } else if (e.getButton() == MouseButton.PRIMARY) {
                // Left Click -> Hide Menu, start dragging
                if (mapContextMenu != null) mapContextMenu.hide();
                dragStartX = e.getSceneX() - mapTranslateX;
                dragStartY = e.getSceneY() - mapTranslateY;
            }
        });

        mapPane.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                mapTranslateX = e.getSceneX() - dragStartX;
                mapTranslateY = e.getSceneY() - dragStartY;
                applyTransformsAndClamp();
            }
        });

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
        mapModeLabel.setText("Explore Mode");
        mapModeLabel.setStyle("-fx-text-fill: -color-text-muted;");
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

        // --- Activity Context Menu for Deletion ---
        ContextMenu activityMenu = new ContextMenu();
        MenuItem deleteActItem = new MenuItem("🗑️ Delete Activity");
        deleteActItem.setOnAction(actionEvent -> {
            deleteActivity(activity);
        });
        activityMenu.getItems().add(deleteActItem);

        row.setOnContextMenuRequested(e -> {
            activityMenu.show(row, e.getScreenX(), e.getScreenY());
        });

        row.setOnMouseClicked(e -> {
            // Hide context menu if active on standard left click
            if (e.getButton() == MouseButton.PRIMARY) {
                activityMenu.hide();
            }

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

    private void deleteActivity(Activity activity) {
        if (activity == null) return;
        SportActivityApp app = SportActivityApp.getInstance();

        try {
            // The library structure might vary, attempt reflection for safety on closed-source model
            try {
                SportActivityApp.class.getMethod("removeActivity", Activity.class)
                        .invoke(app, activity);
            } catch (NoSuchMethodException e1) {
                try {
                    if (app.getCurrentUser() != null) {
                        app.getCurrentUser().getClass().getMethod("removeActivity", Activity.class)
                                .invoke(app.getCurrentUser(), activity);
                    }
                } catch (NoSuchMethodException e2) {
                    // Direct modification fallback
                    app.getUserActivities().remove(activity);
                }
            }
        } catch (Exception ex) {
            System.err.println("Notice: Activity removed from list, but library lacks an accessible model removal method.");
        }

        // Clean up from state
        if (allUserActivities != null) {
            allUserActivities.remove(activity);
        }

        // Unset if the currently viewed map is the one we just deleted
        if (currentActivity == activity) {
            currentActivity = null;
            currentSelectedRow = null;
            renderMap(null, mapSelector.getValue());
        }

        // Re-render list
        filterActivities();
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

        // Clear elevation chart data
        if (elevationChart != null) {
            elevationChart.getData().clear();
        }

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

                XYChart.Series<Number, Number> elevationSeries = new XYChart.Series<>();
                double currentDistance = 0;
                TrackPoint prev = null;

                for (TrackPoint tp : activity.getTrackPoints()) {
                    // Update route on map
                    Point2D p = currentProjection.project(tp);
                    routeLine.getPoints().addAll(p.getX(), p.getY());

                    // Update elevation chart series
                    if (prev != null) {
                        currentDistance += prev.distanceTo(tp) / 1000.0;
                    }
                    elevationSeries.getData().add(new XYChart.Data<>(currentDistance, tp.getElevation()));
                    prev = tp;
                }
                mapPane.getChildren().add(routeLine);

                if (elevationChart != null) {
                    elevationChart.getData().add(elevationSeries);
                }

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

            // Add marker for graph hover on top of everything
            mapPane.getChildren().add(graphHoverMarker);

            Platform.runLater(() -> {
                mapTranslateX = 0;
                mapTranslateY = 0;
                currentZoom = calculateMinZoom();
                applyTransformsAndClamp();
                updateModeLabel();
            });

        } catch (Exception e) {
            System.err.println("Error rendering map: " + e.getMessage());
        }
    }

    // ── Popup for Adding Annotations
    private void showAnnotationPopup(double x, double y) {
        try {
            javafx.stage.Stage mainWindow = (javafx.stage.Stage) mapScrollPane.getScene().getWindow();

            URL fxmlLocation = getClass().getResource("AddAnnotationView.fxml");

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(fxmlLocation);
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage popupStage = new javafx.stage.Stage();
            popupStage.initOwner(mainWindow);
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            TextArea textArea = (TextArea) root.lookup("#annotationTextArea");
            ColorPicker colorPicker = (ColorPicker) root.lookup("#markerColorPicker");
            Button saveBtn = (Button) root.lookup("#saveAnnotationBtn");
            Button closeBtn = (Button) root.lookup("#closeAnnotationBtn");

            if (closeBtn != null) {
                closeBtn.setOnAction(e -> popupStage.close());
            }

            if (colorPicker != null) {
                colorPicker.setValue(Color.web("#E74C3C"));
            }

            if (saveBtn != null && textArea != null && colorPicker != null) {
                saveBtn.setOnAction(e -> {
                    String text = textArea.getText();
                    if (text == null || text.trim().isEmpty()) {
                        text = "Marker";
                    }

                    Color selectedColor = colorPicker.getValue();
                    String hexColor = String.format("#%02X%02X%02X",
                            (int) (selectedColor.getRed() * 255),
                            (int) (selectedColor.getGreen() * 255),
                            (int) (selectedColor.getBlue() * 255));

                    createMarkerAt(x, y, text.trim(), hexColor);
                    popupStage.close();
                });
            }

            javafx.scene.Scene scene = new javafx.scene.Scene(root, mainWindow.getWidth(), mainWindow.getHeight());
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            scene.getStylesheets().addAll(mapScrollPane.getScene().getStylesheets());

            popupStage.setScene(scene);
            popupStage.setX(mainWindow.getX());
            popupStage.setY(mainWindow.getY());
            popupStage.show();

        } catch (Exception e) {
            System.err.println("Error opening Add Annotation FXML:");
            e.printStackTrace();
        }
    }

    private void createMarkerAt(double x, double y, String text, String hexColor) {
        GeoPoint geoPoint = currentProjection.unproject(x, y);

        Annotation ann = new Annotation(AnnotationType.POINT, text, hexColor, 2.0, List.of(geoPoint));
        SportActivityApp app = SportActivityApp.getInstance();
        Annotation savedAnn = app.addAnnotation(currentActivity, ann);

        if (savedAnn != null) drawAnnotation(savedAnn);
    }

    private void drawAnnotation(Annotation ann) {
        if (ann.getType() == AnnotationType.POINT && !ann.getGeoPoints().isEmpty()) {
            Point2D p = currentProjection.project(ann.getGeoPoints().get(0));

            Group markerGroup = new Group();

            Circle marker = new Circle(p.getX(), p.getY(), 6);
            try { marker.setFill(Color.web(ann.getColor())); }
            catch (Exception e) { marker.setFill(Color.RED); }

            marker.setStroke(Color.WHITE);
            marker.setStrokeWidth(2);
            markerGroup.getChildren().add(marker);

            Label textLabel = null;
            // If the annotation has text, prepare a label next to the dot
            if (ann.getText() != null && !ann.getText().trim().isEmpty() && !ann.getText().equals("New Marker")) {
                textLabel = new Label(ann.getText());
                // Simple inline styling to ensure text is readable over the map
                textLabel.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-padding: 3px 6px; -fx-background-radius: 4px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a; -fx-border-color: #cccccc; -fx-border-radius: 4px;");
                textLabel.setLayoutX(p.getX() + 10);
                textLabel.setLayoutY(p.getY() - 10);
                textLabel.setVisible(false); // Make it hidden by default
                markerGroup.getChildren().add(textLabel);
            }

            final Label finalTextLabel = textLabel;

            // --- Deletion Logic: Right-click on the marker to delete it ---
            ContextMenu markerMenu = new ContextMenu();
            MenuItem deleteItem = new MenuItem("🗑️ Delete Annotation");
            deleteItem.setOnAction(actionEvent -> {
                if (currentActivity != null) {
                    try {
                        // The library returns an unmodifiable list for getAnnotations(), so we can't remove directly.
                        // We use reflection to safely attempt standard removal methods without risking compilation errors.
                        try {
                            SportActivityApp.class.getMethod("removeAnnotation", Activity.class, Annotation.class)
                                    .invoke(SportActivityApp.getInstance(), currentActivity, ann);
                        } catch (NoSuchMethodException e1) {
                            try {
                                SportActivityApp.class.getMethod("removeAnnotation", Annotation.class)
                                        .invoke(SportActivityApp.getInstance(), ann);
                            } catch (NoSuchMethodException e2) {
                                try {
                                    Activity.class.getMethod("removeAnnotation", Annotation.class)
                                            .invoke(currentActivity, ann);
                                } catch (NoSuchMethodException e3) {
                                    Activity.class.getMethod("deleteAnnotation", Annotation.class)
                                            .invoke(currentActivity, ann);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Notice: Marker removed from view, but library lacks an accessible model removal method.");
                    }
                }
                mapPane.getChildren().remove(markerGroup); // Remove from the view immediately
            });
            markerMenu.getItems().add(deleteItem);

            // Intercept clicks directly on the marker group
            markerGroup.setOnMousePressed(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    // Hide any globally open menus to avoid overlap
                    if (mapContextMenu != null) mapContextMenu.hide();
                    if (currentActiveMarkerMenu != null) currentActiveMarkerMenu.hide();

                    markerMenu.show(markerGroup, e.getScreenX(), e.getScreenY());
                    currentActiveMarkerMenu = markerMenu;
                    e.consume(); // Prevents the mapPane's context menu from triggering beneath it
                } else if (e.getButton() == MouseButton.PRIMARY) {
                    // Toggle visibility of the annotation text on left click
                    if (finalTextLabel != null) {
                        finalTextLabel.setVisible(!finalTextLabel.isVisible());
                    }
                    e.consume(); // Prevents map panning initiation
                }
            });

            // FIX: Consume dragging on the marker to prevent teleportation
            // Prevents the mapPane from receiving a MouseDragged event with stale start coordinates
            markerGroup.setOnMouseDragged(e -> {
                e.consume();
            });
            // --------------------------------------------------------------

            mapPane.getChildren().add(markerGroup);
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

    @FXML
    private void onToggleMenu() {
        if (isMenuExpanded) {
            // Shrink
            bottomStatsContainer.setPrefHeight(80);
            toggleMenuBtn.setText("⌃");
            isMenuExpanded = false;

            if (chartContainer != null) {
                chartContainer.setVisible(false);
                chartContainer.setManaged(false);
            }
        } else {
            // Expand
            bottomStatsContainer.setPrefHeight(350);
            toggleMenuBtn.setText("⌄");
            isMenuExpanded = true;

            if (chartContainer != null) {
                chartContainer.setVisible(true);
                chartContainer.setManaged(true);
            }
        }
    }

    @FXML
    private void onAddActivity() {
        try {
            javafx.stage.Stage mainWindow = (javafx.stage.Stage) searchField.getScene().getWindow();

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/FitnessPrincess/activities/ActivityMapView.fxml")
            );
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage popupStage = new javafx.stage.Stage();
            popupStage.initOwner(mainWindow);
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, mainWindow.getWidth(), mainWindow.getHeight());
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            popupStage.setScene(scene);

            popupStage.setX(mainWindow.getX());
            popupStage.setY(mainWindow.getY());

            popupStage.showAndWait(); // <--- Pauses the thread here until the popup is closed

            // <--- Refreshes the view automatically with the newly added activity
            loadUserActivities();

        } catch (Exception e) {
            System.err.println("Error al intentar abrir la pantalla de crear actividad.");
            e.printStackTrace();
        }
    }
}