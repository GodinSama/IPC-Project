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
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;


public class LoginController {

    //private variables
    private boolean togglepass;


    @FXML private BorderPane rootPane;
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private PasswordField txtPasswordHided;

    public void initialize() {
        rootPane.setUserData(this);
        txtPassword.textProperty().bindBidirectional(txtPasswordHided.textProperty());
        txtPassword.setVisible(false);
        togglepass = false;
    }

    @FXML
    private void handleLogin() {
        try {
            // Log in logic
            String inUsername = txtUsername.getText();
            String inPassword = txtPassword.getText();

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
                txtPasswordHided.clear();


                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("LOGIN ERROR");
                alert.setHeaderText(null);
                alert.setContentText("User or Password Invalid");
                alert.showAndWait();
            }

        } catch (Exception e) {

            System.err.println("Could not load main application page.t");
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

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml")));
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Could not load main layout page.");
        }
    }

    @FXML
    private void passwordHide(MouseEvent event) {
        if(togglepass){
            txtPassword.setVisible(false);
            txtPasswordHided.setVisible(true);
            togglepass = false;
        } else {
            txtPassword.setVisible(true);
            txtPasswordHided.setVisible(false);
            togglepass = true;
        }
    }

}