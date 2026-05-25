// used ai to solve my errors
package FitnessPrincess.user;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

// Controller for the user profile view handling data display, edits, and session management
public class ProfileViewController implements Initializable {

    // UI controls
    @FXML private Button botonedit;
    @FXML private TextField nickname;
    @FXML private TextField Email;
    @FXML private TextField password;
    @FXML private DatePicker dateOfBirth;
    @FXML private Button botonsave;
    @FXML private Button botonsignout;
    @FXML private Circle currentavatar;
    @FXML private PasswordField passwordhided;

    // State variables
    private boolean togglepass;
    private User user = null;

    // Style cache for validation resets
    private String estiloOriginalEmail;
    private String estiloOriginalPass;
    private String estiloOriginalFecha;

    // Initialize original styles, bind properties, and load user data
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        estiloOriginalEmail = Email.getStyle();
        estiloOriginalPass = passwordhided.getStyle();
        estiloOriginalFecha = dateOfBirth.getStyle();

        cargarDatosUsuario();

        nickname.setDisable(true);
        password.textProperty().bindBidirectional(passwordhided.textProperty());
        password.setVisible(false);
        togglepass = false;
    }

    // Fetch user data from the app instance and populate fields
    private void cargarDatosUsuario() {
        try {
            SportActivityApp app = SportActivityApp.getInstance();
            user = app.getCurrentUser();

            if (user != null) {
                nickname.setText(user.getNickName());
                Email.setText(user.getEmail());
                passwordhided.setText(user.getPassword());
                dateOfBirth.setValue(user.getBirthDate());

                if (user.getAvatar() != null) {
                    currentavatar.setFill(new ImagePattern(user.getAvatar()));
                }
            }
        } catch(Exception e) {
            System.out.println("Ocurrio un error al cargar los datos del usuario");
        }
    }

    // Navigate to the avatar selection view
    @FXML
    private void darleedit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvatarView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) botonedit.getScene().getWindow();

            AvatarController avatarCtrl = loader.getController();
            avatarCtrl.setModoRegistro(false);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar la pantalla AvatarView.fxml");
        }
    }

    // Validate inputs and save updated profile data
    @FXML
    private void darlesave(ActionEvent event) {
        Email.setStyle(estiloOriginalEmail);
        passwordhided.setStyle(estiloOriginalPass);
        password.setStyle(estiloOriginalPass);
        dateOfBirth.setStyle(estiloOriginalFecha);

        try {
            SportActivityApp app = SportActivityApp.getInstance();
            user = app.getCurrentUser();

            boolean isDataValid = true;

            if (!User.checkEmail(Email.getText())) {
                Email.setStyle("-fx-border-color: red;");
                isDataValid = false;
            }
            if (!User.checkPassword(passwordhided.getText())) {
                passwordhided.setStyle("-fx-border-color: red;");
                password.setStyle("-fx-border-color: red;");
                isDataValid = false;
            }

            boolean fechaValida = true;

            try {
                String textoTecleado = dateOfBirth.getEditor().getText();

                if (textoTecleado == null || textoTecleado.trim().isEmpty()) {
                    fechaValida = false;
                } else {
                    LocalDate fechaParseada = dateOfBirth.getConverter().fromString(textoTecleado);
                    if (fechaParseada == null || !User.isOlderThan(fechaParseada, 12)) {
                        fechaValida = false;
                    } else {
                        dateOfBirth.setValue(fechaParseada);
                    }
                }
            } catch (Exception e) {
                fechaValida = false;
            }

            if (!fechaValida) {
                dateOfBirth.setStyle("-fx-text-box-border: red; -fx-focus-color: red; -fx-text-fill: red;");
                dateOfBirth.requestFocus();
                isDataValid = false;
            }

            if (isDataValid) {
                app.updateCurrentUser(
                        Email.getText(),
                        passwordhided.getText(),
                        dateOfBirth.getValue(),
                        user.getAvatarPath()
                );
                System.out.println("Perfil actualizado con éxito.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Crítico");
            alert.setHeaderText(null);
            alert.setContentText("Ha ocurrido un error inesperado al procesar los datos.");
            alert.showAndWait();
        }
    }

    // Log out the current user and return to the login screen
    @FXML
    private void darlesignout(ActionEvent event) {
        try {
            SportActivityApp.getInstance().logout();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar la pantalla LoginView.fxml");
        }
    }

    // Toggle password text visibility
    @FXML
    private void passwordHide(MouseEvent event) {
        if(togglepass){
            password.setVisible(false);
            passwordhided.setVisible(true);
            togglepass = false;
        } else {
            password.setVisible(true);
            passwordhided.setVisible(false);
            togglepass = true;
        }
    }
}