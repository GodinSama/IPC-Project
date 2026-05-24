package FitnessPrincess.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainLayoutController {

    private static final double MIN_WINDOW_SIZE = 838;

    @FXML private BorderPane rootPane;
    @FXML private HBox desktopNav;
    @FXML private HBox mobileNav;

    // Desktop nav buttons
    @FXML private Button navDashboard;
    @FXML private Button navHistory;
    @FXML private Button navMaps;

    // User profile elements in Desktop nav
    @FXML private Button navProfile;
    @FXML private Circle navAvatarCircle;
    @FXML private Label lblUserNick;

    // Mobile tab buttons (Kept to prevent FXML injection errors, even if hidden)
    @FXML private Button tabDashboard;
    @FXML private Button tabProfile;
    @FXML private Button tabHistory;
    @FXML private Button tabMaps;

    // Cache to store loaded views so they don't reset when switching tabs
    private final Map<String, Node> viewCache = new HashMap<>();

    @FXML
    public void initialize() {
        rootPane.setUserData(this);

        // Apply minimum window bounds
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((winObs, oldWin, newWin) -> {
                    if (newWin instanceof Stage) {
                        Stage stage = (Stage) newWin;
                        stage.setMinWidth(MIN_WINDOW_SIZE);
                        stage.setMinHeight(MIN_WINDOW_SIZE);
                    }
                });

                if (newScene.getWindow() instanceof Stage) {
                    Stage stage = (Stage) newScene.getWindow();
                    stage.setMinWidth(MIN_WINDOW_SIZE);
                    stage.setMinHeight(MIN_WINDOW_SIZE);
                }
            }
        });

        // Permanently set the layout to desktop mode - Had no time to make phone mode
        applyStaticDesktopLayout();

        // Load User Data (Nickname and Avatar)
        try {
            SportActivityApp app = SportActivityApp.getInstance();
            User user = app.getCurrentUser();

            if (user != null) {
                if (lblUserNick != null) {
                    lblUserNick.setText(user.getNickName());
                }
                if (user.getAvatar() != null && navAvatarCircle != null) {
                    navAvatarCircle.setFill(new ImagePattern(user.getAvatar()));
                }
            }
        } catch (Exception e) {
            System.err.println("Could not load user data for navigation bar.");
        }

        // Initialize active states for the default view
        setActiveNav(navDashboard);
        loadView("/FitnessPrincess/dashboard/DashboardView.fxml");
    }

    private void applyStaticDesktopLayout() {
        if (desktopNav != null) {
            desktopNav.setVisible(true);
            desktopNav.setManaged(true);
        }

        if (mobileNav != null) {
            mobileNav.setVisible(false);
            mobileNav.setManaged(false);
        }
    }

    // Active nav highlight (desktop) - Selected nav menu shown as a diff colour
    private void setActiveNav(Button active) {
        for (Button nav : new Button[]{navDashboard, navHistory, navMaps}) {
            if (nav != null) {
                nav.getStyleClass().removeAll("nav-btn-active");
                if (!nav.getStyleClass().contains("nav-btn")) {
                    nav.getStyleClass().add("nav-btn");
                }
            }
        }
        if (active != null && active != navProfile) {
            active.getStyleClass().removeAll("nav-btn");
            active.getStyleClass().add("nav-btn-active");
        }
    }

    // Core view loader
    public void loadView(String fxmlPath) {
        try {
            if (viewCache.containsKey(fxmlPath)) {
                rootPane.setCenter(viewCache.get(fxmlPath));
            } else {
                // Load and save the view for the first time
                Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                viewCache.put(fxmlPath, view);
                rootPane.setCenter(view);
            }
        } catch (Exception e) {
            System.err.println("Could not load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Navigation handlers
    @FXML
    private void showDashboard() {
        setActiveNav(navDashboard);
        loadView("/FitnessPrincess/dashboard/DashboardView.fxml");
    }

    @FXML
    public void showProfile() {
        setActiveNav(navProfile);
        loadView("/FitnessPrincess/user/ProfileView.fxml");
    }

    @FXML
    private void showSessionHistory() {
        setActiveNav(navHistory);
        loadView("/FitnessPrincess/user/SessionHistoryView.fxml");
    }

    @FXML
    public void showMapManagement() {
        setActiveNav(navMaps);
        loadView("/FitnessPrincess/maps/MapManagementView.fxml");
    }
}