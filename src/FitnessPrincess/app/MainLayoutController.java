package FitnessPrincess.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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

    // Separated width and height to prevent weird square window constraints
    private static final double MIN_WINDOW_WIDTH = 838;
    private static final double MIN_WINDOW_HEIGHT = 738;

    @FXML private BorderPane rootPane;
    @FXML private HBox desktopNav;
    @FXML private HBox mobileNav;

    // Desktop nav buttons
    @FXML private Button navDashboard;
    @FXML private Button navHistory;
    @FXML private Button navMaps;

    // Login & Profile elements
    @FXML private Button navLogin;
    @FXML private HBox profileContainer;
    @FXML private Button navProfile;
    @FXML private Circle navAvatarCircle;
    @FXML private Label lblUserNick;

    // Mobile tab buttons
    @FXML private Button tabDashboard;
    @FXML private Button tabProfile;
    @FXML private Button tabHistory;
    @FXML private Button tabMaps;

    // Cache to store loaded views so they don't reset when switching tabs
    private final Map<String, Node> viewCache = new HashMap<>();

    @FXML
    public void initialize() {
        rootPane.setUserData(this);

        // Fix: Force the root node itself to maintain minimum size, preventing scene swap collapse
        rootPane.setMinSize(MIN_WINDOW_WIDTH, MIN_WINDOW_HEIGHT);

        // Apply minimum window bounds to the Stage
        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((winObs, oldWin, newWin) -> {
                    if (newWin instanceof Stage) {
                        Stage stage = (Stage) newWin;
                        stage.setMinWidth(MIN_WINDOW_WIDTH);
                        stage.setMinHeight(MIN_WINDOW_HEIGHT);
                    }
                });

                if (newScene.getWindow() instanceof Stage) {
                    Stage stage = (Stage) newScene.getWindow();
                    stage.setMinWidth(MIN_WINDOW_WIDTH);
                    stage.setMinHeight(MIN_WINDOW_HEIGHT);
                }
            }
        });

        // Permanently set the layout to desktop mode
        applyStaticDesktopLayout();

        // Verify initial state on load
        checkInitialAuthState();
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

    private void checkInitialAuthState() {
        try {
            SportActivityApp app = SportActivityApp.getInstance();
            User user = app.getCurrentUser();

            if (user != null) {
                // If previously logged in session exists, activate app
                activateApp(user);
            } else {
                // Keep center empty and display only the Sign In button
                profileContainer.setVisible(false);
                profileContainer.setManaged(false);
                navLogin.setVisible(true);
                navLogin.setManaged(true);
            }
        } catch (Exception e) {
            System.err.println("Could not check initial authentication state.");
        }
    }

    public void activateApp(User user) {
        // Enable Navigation Buttons
        navDashboard.setDisable(false);
        navHistory.setDisable(false);
        navMaps.setDisable(false);
        tabDashboard.setDisable(false);
        tabProfile.setDisable(false);
        tabHistory.setDisable(false);
        tabMaps.setDisable(false);

        // Swap top-right login for profile block
        navLogin.setVisible(false);
        navLogin.setManaged(false);
        profileContainer.setVisible(true);
        profileContainer.setManaged(true);

        // Apply User Data
        if (user != null) {
            if (lblUserNick != null) {
                lblUserNick.setText(user.getNickName());
            }
            if (user.getAvatar() != null && navAvatarCircle != null) {
                navAvatarCircle.setFill(new ImagePattern(user.getAvatar()));
            }
        }

        // Display Dashboard initially after login
        showDashboard();
    }

    // Active nav highlight (desktop)
    private void setActiveNav(Button active) {
        for (Button nav : new Button[]{navDashboard, navHistory, navMaps, navLogin}) {
            if (nav != null) {
                nav.getStyleClass().removeAll("nav-btn-active");
                if (!nav.getStyleClass().contains("nav-btn") && nav != navLogin) {
                    nav.getStyleClass().add("nav-btn");
                }
            }
        }
        if (active != null && active != navProfile && active != navLogin) {
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
                Node view = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
                viewCache.put(fxmlPath, view);
                rootPane.setCenter(view);
            }

            Platform.runLater(() -> {
                rootPane.requestLayout();
                if (rootPane.getScene() != null && rootPane.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) rootPane.getScene().getWindow();

                    // ensure the stage hasn't collapsed
                    if (stage.getWidth() < MIN_WINDOW_WIDTH) stage.setWidth(MIN_WINDOW_WIDTH);
                    if (stage.getHeight() < MIN_WINDOW_HEIGHT) stage.setHeight(MIN_WINDOW_HEIGHT);
                }
            });

        } catch (Exception e) {
            System.err.println("Could not load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Navigation handlers
    @FXML
    private void showLogin() {
        setActiveNav(navLogin);
        loadView("/FitnessPrincess/auth/LoginView.fxml");
        navLogin.setVisible(false);
        navLogin.setManaged(false);
    }

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