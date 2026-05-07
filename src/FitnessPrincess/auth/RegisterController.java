package FitnessPrincess.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

import java.time.LocalDate;
import java.util.Objects;

public class RegisterController {
    @FXML
    private BorderPane rootPane;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtEmail;
    @FXML private DatePicker valueBirthdate;


    @FXML
    public void initialize() {
        rootPane.setUserData(this);
    }

    @FXML
    private void handleSignUp() {
        try {
            // Log in logic
            String inUsername = txtUsername.getText();
            String inPassword = txtPassword.getText();
            String inEmail = txtEmail.getText();
            LocalDate inBirthdate = valueBirthdate.getValue();

            // TO-DO : Check if user / email with account already exists

            // Get the app instance
            SportActivityApp app = SportActivityApp.getInstance();

            if (!User.checkNickName(inUsername)) {
                System.err.println("Invalid username format.");
                return;
            }
            if (!User.checkEmail(inEmail)) {
                System.err.println("Invalid email format.");
                return;
            }
            if (!User.checkPassword(inPassword)) {
                System.err.println("Invalid password. Must be 8-20 characters, with upper, lower, digit, and symbol.");
                return;
            }
            if (inBirthdate == null || !User.isOlderThan(inBirthdate, 12)) {
                System.err.println("User must be older than 12 years.");
                return;
            }

            // Sign them up
            boolean registered = app.registerUser(inUsername, inEmail, inPassword, inBirthdate, "avatars/1-intro-photo-final.jpg");

            if (registered) {
                // Swap the scene to the login view if registration was successful
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/auth/LoginView.fxml")));
                stage.setScene(new Scene(loginRoot));
            } else {
                System.err.println("Registration failed.");
            }


        } catch (Exception e) {
            System.err.println("Could not load main application page."); // ERROR PANEL
        }
    }

    @FXML
    private void handleLogin() {
        try {
            // Swap the scene to the Registration view
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent registerRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/auth/LoginView.fxml")));
            stage.setScene(new Scene(registerRoot));

        } catch (Exception e) {
            System.err.println("Could not load registration page."); // ERROR PANEL
        }
    }
}
