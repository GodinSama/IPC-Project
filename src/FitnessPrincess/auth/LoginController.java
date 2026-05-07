package FitnessPrincess.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;

import java.util.Objects;

public class LoginController {

    @FXML private BorderPane rootPane;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    @FXML
    public void initialize() {
        rootPane.setUserData(this);
    }

    @FXML
    private void handleLogin() {
        try {
            // Log in logic
            String inUsername = txtUsername.getText();
            String inPassword = txtPassword.getText();
            // System.out.println("USERNAME: " + inUsername);
            // System.out.println("PASSWORD: " + inPassword);

            // Get the app instance
            SportActivityApp app = SportActivityApp.getInstance();

            // Check if the correct user was signed in using the app's login method
            if (app.login(inUsername, inPassword)) {
                System.out.println("User logged in.");

                // Swap the scene to root
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml")));
                stage.setScene(new Scene(loginRoot));
            } else {
                System.out.println("Invalid credentials."); // ERROR MESSAGE - INVALID PASSWORD OR USERNAME
                txtPassword.clear();
            }

        } catch (Exception e) {
            System.err.println("Could not load main application page."); // ERROR PANEL
        }
    }

    @FXML
    private void handleSignUp() {
        try {
            // Swap the scene to the Registration view
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent registerRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/auth/RegisterView.fxml")));
            stage.setScene(new Scene(registerRoot));

        } catch (Exception e) {
            System.err.println("Could not load registration page."); // ERROR PANEL
        }
    }
}