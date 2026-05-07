package FitnessPrincess.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Objects;

public class MainLayoutController {

    @FXML private BorderPane rootPane;

    @FXML
    public void initialize() {
        rootPane.setUserData(this);

        // Load the default view upon entering
        loadView("/FitnessPrincess/activities/DashboardView.fxml");
    }

    // Core method - call this to swap any view into the center

    public void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            rootPane.setCenter(view);
        } catch (Exception e) {
            System.err.println("Could not load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Navigation handlers

    @FXML
    private void showActivities() {
        loadView("/FitnessPrincess/activities/DashboardView.fxml");
    }

    @FXML
    private void showProfile() {
        loadView("/FitnessPrincess/user/ProfileView.fxml");
    }

    @FXML
    private void showSessionHistory() {
        loadView("/FitnessPrincess/user/SessionHistoryView.fxml");
    }

    @FXML
    private void showMapManagement() {
        loadView("/FitnessPrincess/maps/MapManagementView.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            // Log out logic
            System.out.println("User logged out.");

            // Swap the scene root back to the Login View
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/auth/LoginView.fxml")));
            stage.setScene(new Scene(loginRoot));

        } catch (Exception e) {
            System.err.println("Could not return to login screen.");
            e.printStackTrace();
        }
    }
}