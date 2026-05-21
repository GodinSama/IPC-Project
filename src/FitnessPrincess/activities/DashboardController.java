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
    
    @FXML private void onAddActivity() { /* Lógica para abrir creación */ }
    
    @FXML private void onShowSummary() { /* Lógica para abrir resumen */ }
    
    @FXML private void onShowHistory() { /* Lógica para abrir historial */ }
}