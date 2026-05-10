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

    private static final double MOBILE_BREAKPOINT = 838;

    @FXML private BorderPane rootPane;
    @FXML private HBox desktopNav;
    @FXML private HBox mobileNav;

    // Desktop nav buttons
    @FXML private Button navActivities;
    @FXML private Button navProfile;
    @FXML private Button navHistory;
    @FXML private Button navMaps;
    @FXML private Button navSignOut;

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
                newScene.widthProperty().addListener((o, oldW, newW) -> applyLayout(newW.doubleValue()));
            }
        });

        // Initialize active states for the default view
        setActiveTab(tabActivities);
        setActiveNav(navActivities);
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
        if (tabActivities == null) return; // Safeguard if FXML injection is incomplete
        for (Button tab : new Button[]{tabActivities, tabProfile, tabHistory, tabMaps, tabSignOut}) {
            if (tab != null) {
                tab.getStyleClass().removeAll("tab-btn-active");
                if (!tab.getStyleClass().contains("tab-btn")) {
                    tab.getStyleClass().add("tab-btn");
                }
            }
        }
        if (active != null) {
            active.getStyleClass().removeAll("tab-btn");
            active.getStyleClass().add("tab-btn-active");
        }
    }

    // Active nav highlight (desktop)
    private void setActiveNav(Button active) {
        if (navActivities == null) return; // Safeguard if FXML injection is incomplete
        for (Button nav : new Button[]{navActivities, navProfile, navHistory, navMaps, navSignOut}) {
            if (nav != null) {
                nav.getStyleClass().removeAll("nav-btn-active");
                if (!nav.getStyleClass().contains("nav-btn")) {
                    nav.getStyleClass().add("nav-btn");
                }
            }
        }
        if (active != null) {
            active.getStyleClass().removeAll("nav-btn");
            active.getStyleClass().add("nav-btn-active");
        }
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
        setActiveNav(navActivities);
        loadView("/FitnessPrincess/activities/DashboardView.fxml");
    }

    @FXML
    private void showProfile() {
        setActiveTab(tabProfile);
        setActiveNav(navProfile);
        loadView("/FitnessPrincess/user/ProfileView.fxml");
    }

    @FXML
    private void showSessionHistory() {
        setActiveTab(tabHistory);
        setActiveNav(navHistory);
        loadView("/FitnessPrincess/user/SessionHistoryView.fxml");
    }

    @FXML
    private void showMapManagement() {
        setActiveTab(tabMaps);
        setActiveNav(navMaps);
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