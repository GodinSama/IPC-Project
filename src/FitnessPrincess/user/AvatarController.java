// used ai to solve my errors
package FitnessPrincess.user;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import FitnessPrincess.app.MainLayoutController;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

public class AvatarController implements Initializable {

    @FXML
    private ImageView avatarImageView;
    @FXML
    private HBox vboxmodify;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Button botonSkip;

    private boolean vieneDeRegistro = false;
    private File avatarSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize logic if needed
    }

    public void recibirNuevoAvatar(File nuevaFoto) {
        if (nuevaFoto != null) {
            avatarSeleccionado = nuevaFoto;
            Image image = new Image(nuevaFoto.toURI().toString());
            avatarImageView.setImage(image);

            // Update the global user immediately so other views catch it
            try {
                User currentUser = SportActivityApp.getInstance().getCurrentUser();
                if (currentUser != null) {
                    currentUser.setAvatarPath(nuevaFoto.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("Could not update user avatar path globally.");
            }
        }

        if (!vieneDeRegistro) {
            botonSkip.setText("Continue >");
        }
    }

    public void setModoRegistro(boolean esRegistro) {
        this.vieneDeRegistro = esRegistro;
        if (esRegistro) {
            botonSkip.setText("Skip >");
        } else {
            botonSkip.setText("Go Back");
        }
    }

    @FXML
    private void darlemodify(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvatarSelector.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar AvatarSelector.fxml");
        }
    }

    @FXML
    private void darleskip(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml"));
            Parent root = loader.load();

            MainLayoutController mainCtrl = loader.getController();
            if(mainCtrl != null) {
                mainCtrl.showProfile();
            }

            Stage stage = (Stage) botonSkip.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar MainLayout.fxml");
        }
    }
}