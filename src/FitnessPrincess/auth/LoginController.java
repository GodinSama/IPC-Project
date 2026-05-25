package FitnessPrincess.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import java.util.Objects;

public class LoginController {

    @FXML private VBox rootPane;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private SVGPath eyeIcon;
    @FXML private Label usernameErrorLabel;
    @FXML private Label passwordErrorLabel;

    private final String EYE_CLOSED = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.81l2.91 2.91C21.03 15.17 22.19 13.66 23 12c-1.73-4.39-6-7.5-11-7.5-.68 0-1.35.05-2 .14l2.25 2.25C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.41-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.34-4.34L13.91 9.9c-.14-.28-.35-.53-.6-.71z";
    private final String EYE_OPEN  = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";

    @FXML
    public void initialize() {
        rootPane.setUserData(this);
        txtPasswordVisible.textProperty().bindBidirectional(txtPassword.textProperty());
    }

    @FXML
    private void handleTogglePassword(MouseEvent event) {
        boolean isMasked = txtPassword.isVisible();
        txtPassword.setVisible(!isMasked);
        txtPassword.setManaged(!isMasked);
        txtPasswordVisible.setVisible(isMasked);
        txtPasswordVisible.setManaged(isMasked);
        eyeIcon.setContent(isMasked ? EYE_OPEN : EYE_CLOSED);
    }

    @FXML
    private void handleLogin() {
        usernameErrorLabel.setVisible(false);
        usernameErrorLabel.setManaged(false);
        passwordErrorLabel.setVisible(false);
        passwordErrorLabel.setManaged(false);

        try {
            String inUsername = txtUsername.getText().trim();
            String inPassword = txtPassword.getText();

            boolean hasErrors = false;
            if (inUsername.isEmpty()) {
                showError(usernameErrorLabel, "Username cannot be empty.");
                hasErrors = true;
            }
            if (inPassword.isEmpty()) {
                showError(passwordErrorLabel, "Password cannot be empty.");
                hasErrors = true;
            }
            if (hasErrors) return;

            SportActivityApp app = SportActivityApp.getInstance();
            if (app.login(inUsername, inPassword)) {
                Stage stage = (Stage) rootPane.getScene().getWindow();
                Parent root = FXMLLoader.load(Objects.requireNonNull(
                        getClass().getResource("/FitnessPrincess/app/MainLayout.fxml")));
                stage.setScene(new Scene(root));
            } else {
                showError(passwordErrorLabel, "Invalid username or password.");
                txtPassword.clear();
            }
        } catch (Exception e) {
            System.err.println("Could not load main application page.");
            e.printStackTrace();
        }
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    @FXML
    private void handleSignUp() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/FitnessPrincess/auth/RegisterView.fxml")));
            stage.setScene(new Scene(root));
            stage.setWidth(stage.getWidth());   // mantiene el ancho actual
            stage.setHeight(stage.getHeight()); // mantiene el alto actual
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            System.err.println("Could not load registration page.");
            e.printStackTrace();
        }
    }
}