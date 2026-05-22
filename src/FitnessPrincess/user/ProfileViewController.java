/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FitnessPrincess.user;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dgimb
 */
public class ProfileViewController implements Initializable {
    
    
    

    @FXML
    private Button botonx;
    @FXML
    private Button botonedit;
    @FXML
    private TextField nickname;
    @FXML
    private TextField Email;
    @FXML
    private TextField password;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private Button botonsave;
    @FXML
    private Button botonsignout;
    @FXML
    private ImageView currentavatar;
    
    private File currentAvatarFile;
    @FXML
    private PasswordField passwordhided;
    
    private boolean togglepass;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        nickname.setDisable(true);
        
        
        password.textProperty().bindBidirectional(passwordhided.textProperty());
        password.setVisible(false);
        togglepass = false;
        
    }    
    public void recibirNuevoAvatar(File nuevaFoto) {
        if (nuevaFoto != null) {
            
            this.currentAvatarFile = nuevaFoto;
            
            
            Image image = new Image(nuevaFoto.toURI().toString());
            currentavatar.setImage(image);
        }
    }
    
    @FXML
    private void darlex(ActionEvent event) {
    }

    @FXML
    private void darleedit(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvatarView.fxml"));
            Parent root = loader.load();
            
           
            Stage stage = (Stage) botonedit.getScene().getWindow();
            
            //controlador
            AvatarController avatarCtrl = loader.getController();
            
            
            avatarCtrl.setModoRegistro(false);
            
            
            if (this.currentAvatarFile != null) {
                avatarCtrl.recibirNuevoAvatar(this.currentAvatarFile);
            }
            
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar la pantalla AvatarView.fxml");
        }
    }

    @FXML
    private void darlesave(ActionEvent event) {
    }

    @FXML
    private void darlesignout(ActionEvent event) {
    }

    @FXML
    private void passwordHide(MouseEvent event) {
        
        if(togglepass){
          
         password.setVisible(false);
         passwordhided.setVisible(true);
         
         
        
        togglepass = false;
        }else{
            
         password.setVisible(true);
         passwordhided.setVisible(false);
         
        
         togglepass=true;}
    }
    
}