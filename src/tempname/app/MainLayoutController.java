package tempname.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import upv.ipc.sportlib.SportActivityApp;



public class MainLayoutController {

    @FXML private BorderPane rootPane;
    @FXML private Label lblUserNick;

    @FXML
    public void initialize() {
        rootPane.setUserData(this);

        // SportActivityApp instance
        SportActivityApp app = (SportActivityApp) SportActivityApp.getInstance();

        // TO-DO : Check if already signed in

        // if ( not signed in)
        // loadView("/tempname/auth/LoginView.fxml");

        // else
        loadView("/tempname/activities/DashboardView.fxml");
    }

    // ── Core method — call this to swap any view into the center

    public void loadView(String fxmlPath) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootPane.setCenter(view);
        } catch (Exception e) {
            System.err.println("Could not load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Navigation handlers

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
        System.out.println("TODO: logout");
    }
}
