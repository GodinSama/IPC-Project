package tempname.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class LoginController {

    @FXML private BorderPane rootPane;

    @FXML
    public void initialize() {
        rootPane.setUserData(this);
    }

    @FXML
    private void handleLogin() {
        try {
            // Log in logic
            System.out.println("User logged in.");

            // Swap the scene to root
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/tempname/app/MainLayout.fxml"));
            stage.setScene(new Scene(loginRoot));

        } catch (Exception e) {
            System.err.println("Could not log in.");
            e.printStackTrace();
        }
    }
}
