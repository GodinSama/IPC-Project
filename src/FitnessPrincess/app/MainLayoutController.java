package FitnessPrincess.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainLayoutController {

    private static final double MOBILE_BREAKPOINT = 900;

    @FXML private BorderPane rootPane;
    @FXML private HBox desktopNav;
    @FXML private HBox mobileNav;

    // Mobile tab buttons
    @FXML private Button tabActivities;
    @FXML private Button tabProfile;
    @FXML private Button tabHistory;
    @FXML private Button tabMaps;
    @FXML private Button tabSignOut;

    @FXML
    public void initialize() {
        rootPane.setUserData(this);

        // Listen for scene attachment, then watch width
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                applyLayout(newScene.getWidth());

                // Update layout whenever window is resized
                newScene.widthProperty().addListener((o, oldW, newW) -> applyLayout(newW.doubleValue())
                );
            }
        });

        loadView("/FitnessPrincess/activities/DashboardView.fxml");
    }

    // Responsive switch
    private void applyLayout(double width) {
        boolean isMobile = width < MOBILE_BREAKPOINT;

        // Desktop nav: top bar
        desktopNav.setVisible(!isMobile);
        desktopNav.setManaged(!isMobile);

        // Mobile nav: bottom tab bar
        mobileNav.setVisible(isMobile);
        mobileNav.setManaged(isMobile);
    }

    // Active tab highlight (mobile)
    private void setActiveTab(Button active) {
        for (Button tab : new Button[]{tabActivities, tabProfile, tabHistory, tabMaps, tabSignOut}) {
            tab.getStyleClass().removeAll("tab-btn-active");
            tab.getStyleClass().add("tab-btn");
        }
        active.getStyleClass().removeAll("tab-btn");
        active.getStyleClass().add("tab-btn-active");
    }

    // Core view loader
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
        setActiveTab(tabActivities);
        loadView("/FitnessPrincess/activities/DashboardView.fxml");
    }

    @FXML
    private void showProfile() {
        setActiveTab(tabProfile);
        loadView("/FitnessPrincess/user/ProfileView.fxml");
    }

    @FXML
    private void showSessionHistory() {
        setActiveTab(tabHistory);
        loadView("/FitnessPrincess/user/SessionHistoryView.fxml");
    }

    @FXML
    private void showMapManagement() {
        setActiveTab(tabMaps);
        loadView("/FitnessPrincess/maps/MapManagementView.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/auth/LoginView.fxml")));
            stage.setScene(new Scene(loginRoot));
        } catch (Exception e) {
            System.err.println("Could not return to login screen.");
            e.printStackTrace();
        }
    }
}