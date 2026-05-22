/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */package FitnessPrincess.user;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dgimb
 */
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    public void recibirNuevoAvatar(File nuevaFoto) {
        if (nuevaFoto != null) {
            avatarSeleccionado = nuevaFoto;
            javafx.scene.image.Image image = new javafx.scene.image.Image(nuevaFoto.toURI().toString());
            avatarImageView.setImage(image);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
            Parent root = loader.load();
            
            if (this.avatarSeleccionado != null) {
                ProfileViewController profileCtrl = loader.getController();
                profileCtrl.recibirNuevoAvatar(this.avatarSeleccionado);
            }
            
            
            Stage stage = (Stage) botonSkip.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar ProfileView.fxml");
        }
    }
}
