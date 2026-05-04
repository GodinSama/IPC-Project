package tempname.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

/**
 * MainLayoutController
 *
 * Controls the app shell (MainLayout.fxml).
 *
 * The single most important method here is loadView(String fxmlPath).
 * Every nav button calls it with a different path to swap the center pane.
 *
 * HOW TO USE FROM OTHER CONTROLLERS:
 *   Stage stage = (Stage) anyNode.getScene().getWindow();
 *   MainLayoutController main =
 *       (MainLayoutController) stage.getScene().getRoot().getUserData();
 *   main.loadView("/tempname/activities/DashboardView.fxml");
 */
public class MainLayoutController {

    @FXML private BorderPane rootPane;
    @FXML private Label      lblUserNick;

    // ── Lifecycle ──────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // Store a reference to this controller in the root node so other
        // controllers can call loadView() without a static reference.
        rootPane.setUserData(this);

        // TODO: show the logged-in user's nickname
        // lblUserNick.setText(SportActivityApp.getInstance().getCurrentUser().getNickName());

        // Default view on startup
        loadView("/tempname/activities/DashboardView.fxml");
    }

    // ── Core method — call this to swap any view into the center ───────

    /**
     * Loads an FXML file and places it in the BorderPane center.
     *
     * @param fxmlPath absolute resource path, e.g. "/tempname/auth/LoginView.fxml"
     */
    public void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootPane.setCenter(view);
        } catch (Exception e) {
            System.err.println("Could not load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ── Nav handlers — each one just calls loadView() ──────────────────

    @FXML
    private void showActivities() {
        loadView("/tempname/activities/DashboardView.fxml");
    }

    @FXML
    private void showProfile() {
        loadView("/tempname/user/ProfileView.fxml");
    }

    @FXML
    private void showSessionHistory() {
        loadView("/tempname/user/SessionHistoryView.fxml");
    }

    @FXML
    private void showMapManagement() {
        loadView("/tempname/maps/MapManagementView.fxml");
    }

    @FXML
    private void handleLogout() {
        // TODO:
        //   1. SportActivityApp.getInstance().logout();   ← saves session stats
        //   2. loadView("/tempname/auth/LoginView.fxml");
        System.out.println("TODO: logout");
    }
}
