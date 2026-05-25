package FitnessPrincess.auth;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;
import FitnessPrincess.user.AvatarController;

import java.time.LocalDate;
import java.util.Objects;

public class RegisterController {
    
    @FXML private VBox rootPane;
    
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPasswordMasked;
    @FXML private TextField txtPasswordVisible;
    @FXML private DatePicker valueBirthdate;
    
    @FXML private Button nextBtn;
    
    @FXML private SVGPath eyeIcon; 

    @FXML private Label usernameError;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label birthdateError;

    private final String EYE_CLOSED = "M12 7c2.76 0 5 2.24 5 5 0 .65-.13 1.26-.36 1.81l2.91 2.91C21.03 15.17 22.19 13.66 23 12c-1.73-4.39-6-7.5-11-7.5-.68 0-1.35.05-2 .14l2.25 2.25C10.74 7.13 11.35 7 12 7zM2 4.27l2.28 2.28.46.46C3.08 8.3 1.78 10.02 1 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.41-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zM7.53 9.8l1.55 1.55c-.05.21-.08.43-.08.65 0 1.66 1.34 3 3 3 .22 0 .44-.03.65-.08l1.55 1.55c-.67.33-1.41.53-2.2.53-2.76 0-5-2.24-5-5 0-.79.2-1.53.53-2.2zm4.34-4.34L13.91 9.9c-.14-.28-.35-.53-.6-.71z";
    private final String EYE_OPEN = "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z";

    @FXML
    public void initialize() {
        rootPane.setUserData(this);
        txtPasswordVisible.textProperty().bindBidirectional(txtPasswordMasked.textProperty());
        Runnable checkFields = () -> {
            boolean filled = !txtUsername.getText().trim().isEmpty()
                    && !txtEmail.getText().trim().isEmpty()
                    && !txtPasswordMasked.getText().isEmpty()
                    && valueBirthdate.getValue() != null;

            if (filled) {
                nextBtn.getStyleClass().removeAll("next-btn");
                if (!nextBtn.getStyleClass().contains("next-btn-active")) {
                    nextBtn.getStyleClass().add("next-btn-active");
                }
                nextBtn.setStyle("");
            } else {
                nextBtn.getStyleClass().removeAll("next-btn-active");
                if (!nextBtn.getStyleClass().contains("next-btn")) {
                    nextBtn.getStyleClass().add("next-btn");
                }
                nextBtn.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #888888; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 24 10 24;");
            }
        };

        txtUsername.textProperty().addListener((o, ov, nv) -> checkFields.run());
        txtEmail.textProperty().addListener((o, ov, nv) -> checkFields.run());
        txtPasswordMasked.textProperty().addListener((o, ov, nv) -> checkFields.run());
        valueBirthdate.valueProperty().addListener((o, ov, nv) -> checkFields.run());
    }

    @FXML
    private void handleTogglePassword(MouseEvent event) {
        boolean isMasked = txtPasswordMasked.isVisible();
        
        txtPasswordMasked.setVisible(!isMasked);
        txtPasswordMasked.setManaged(!isMasked);
        txtPasswordVisible.setVisible(isMasked);
        txtPasswordVisible.setManaged(isMasked);
        
        if (isMasked) {
            eyeIcon.setContent(EYE_OPEN);
        } else {
            eyeIcon.setContent(EYE_CLOSED);
        }
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
        label.setManaged(true);
    }

    private void hideAllErrors() {
        usernameError.setVisible(false); usernameError.setManaged(false);
        emailError.setVisible(false); emailError.setManaged(false);
        passwordError.setVisible(false); passwordError.setManaged(false);
        birthdateError.setVisible(false); birthdateError.setManaged(false);
    }

    @FXML
    private void handleNext() {
        hideAllErrors();
        
        try {
            if (valueBirthdate.getValue() == null && !valueBirthdate.getEditor().getText().isEmpty()) {
                try {
                    valueBirthdate.setValue(valueBirthdate.getConverter().fromString(valueBirthdate.getEditor().getText()));
                } catch (Exception e) {
                    // Exception if enters something illogical
                }
            }
            String inUsername = txtUsername.getText().trim();
            String inEmail = txtEmail.getText().trim();
            String inPassword = txtPasswordMasked.getText();
            LocalDate inBirthdate = valueBirthdate.getValue();

            boolean hasErrors = false;

            if (inUsername.isEmpty()) { showError(usernameError, "Nickname cannot be empty."); hasErrors = true; } 
            else if (!User.checkNickName(inUsername)) { showError(usernameError, "Invalid nickname format."); hasErrors = true; }

            if (inEmail.isEmpty()) { showError(emailError, "Email cannot be empty."); hasErrors = true; } 
            else if (!User.checkEmail(inEmail)) { showError(emailError, "Invalid email format."); hasErrors = true; }

            if (inPassword.isEmpty()) { showError(passwordError, "Password cannot be empty."); hasErrors = true; } 
            else if (!User.checkPassword(inPassword)) { showError(passwordError, "Must be 8-20 chars: upper, lower, digit & symbol."); hasErrors = true; }

            if (inBirthdate == null) { showError(birthdateError, "Birthdate cannot be empty."); hasErrors = true; } 
            else if (!User.isOlderThan(inBirthdate, 12)) { showError(birthdateError, "You must be at least 12 years old."); hasErrors = true; }

            if (hasErrors) return;

            // User registration
            SportActivityApp app = SportActivityApp.getInstance();
            
            // Default avatar
            boolean registered = app.registerUser(inUsername, inEmail, inPassword, inBirthdate, "");

            if (registered) {
                // Automatic log in
                app.login(inUsername, inPassword);

                // Avatar screen
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FitnessPrincess/user/AvatarView.fxml"));
                Parent avatarRoot = loader.load();

                AvatarController avatarCtrl = loader.getController();
                if (avatarCtrl != null) {
                    avatarCtrl.setModoRegistro(true);
                }

                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(new Scene(avatarRoot));

            } else {
                showError(usernameError, "Registration failed. Username or email already exists.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error cargando la pantalla del Avatar.");
        }
    }

    @FXML
    private void handleLogin() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/auth/LoginView.fxml")));
            stage.setScene(new Scene(loginRoot));
            stage.setWidth(stage.getWidth());
            stage.setHeight(stage.getHeight());
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}