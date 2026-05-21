package FitnessPrincess.activities;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import java.net.URL;
import java.util.ResourceBundle;
import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;

public class DashboardController implements Initializable {

    @FXML private VBox activitiesContainer;
    @FXML private TextField searchField;
    @FXML private Button deleteButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Searching activities ---
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            deleteButton.setVisible(!newVal.isEmpty());
            loadActivities(newVal);
        });
        
        loadActivities("");
    }

    private void loadActivities(String filter) {
        activitiesContainer.getChildren().clear();
        SportActivityApp app = SportActivityApp.getInstance();
        
        // --- Obtaining Activities from library ---
        for (Activity activity : app.getUserActivities()) {
            if (filter.isEmpty() || activity.getName().toLowerCase().contains(filter.toLowerCase())) {
                activitiesContainer.getChildren().add(buildActivityCard(activity));
            }
        }
    }

    private HBox buildActivityCard(Activity activity) {
        // --- Creating cards with CSS ---
        HBox card = new HBox(15);
        card.getStyleClass().add("card-dark"); 
        card.setAlignment(Pos.CENTER_LEFT);

        // --- Thumbnail - icon depending on sport ---
        StackPane iconArea = new StackPane(new Label("🏃")); 
        iconArea.getStyleClass().add("map-thumbnail"); 

        VBox info = new VBox(5);
        Label title = new Label(activity.getName());
        title.getStyleClass().add("card-title");

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
Label date = new Label(activity.getStartTime().format(formatter)); 
        date.getStyleClass().add("card-subtitle");

        // --- Data: distance and time ---
        HBox stats = new HBox(15);
        Label dist = new Label("Dist: " + String.format("%.2f", activity.getTotalDistance())+ " km");
dist.getStyleClass().add("card-value");
        dist.getStyleClass().add("card-value");
        
        Label duration = new Label("Time: " + activity.getDuration().toMinutes() + " min");
        duration.getStyleClass().add("card-value");
        
        stats.getChildren().addAll(dist, duration);
        info.getChildren().addAll(title, date, stats);
        
        card.getChildren().addAll(iconArea, info);
        return card;
    }

    @FXML private void onClearSearch() { searchField.clear(); }
    
    // --- To add a new activity ---
    @FXML private void onAddActivity() { 
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

            popupStage.show();

        } catch (Exception e) {
            System.err.println("Error al intentar abrir la pantalla de crear actividad.");
            e.printStackTrace();
        }
    }
    
    // --- To show the monthly summary ---
    @FXML private void onShowSummary() { 
        try {
            javafx.stage.Stage mainWindow = (javafx.stage.Stage) searchField.getScene().getWindow();

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/FitnessPrincess/user/MonthlySummaryView.fxml")
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

            popupStage.show();

        } catch (Exception e) {
            System.err.println("Error trying to open Monthly Summary.");
            e.printStackTrace();
        }
    }
    
    // --- To show the session history ---
    @FXML private void onShowHistory() { 
        
    }
}