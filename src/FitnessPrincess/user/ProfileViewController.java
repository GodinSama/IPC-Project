/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.User;

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
    
    private User user = null;
    
    private String estiloOriginalEmail;
    private String estiloOriginalPass;
    private String estiloOriginalFecha;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        
        
        
        estiloOriginalEmail = Email.getStyle();
        estiloOriginalPass = passwordhided.getStyle();
        estiloOriginalFecha = dateOfBirth.getStyle();
        
        try{
            
            SportActivityApp app = SportActivityApp.getInstance();
            
            user = app.getCurrentUser();
            nickname.setText(user.getNickName());
            Email.setText(user.getEmail());
            passwordhided.setText(user.getPassword());
            dateOfBirth.setValue(user.getBirthDate());
            currentavatar.setImage(user.getAvatar());
            
        
        }catch(Exception e){
           System.out.println("Ocurrio un error al cargar los datos del usuario");
        
        }
        
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
        
        // VOLVER A LA PAGINA A LA Q ESTABA 
        try {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml"));
        Parent root = loader.load();
        
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("No se ha podido cargar la pantalla MainLayout.fxml");
    }
        
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
        System.out.println("LE DISTE A SAVE");
        
        Email.setStyle(estiloOriginalEmail);
        passwordhided.setStyle(estiloOriginalPass);
        password.setStyle(estiloOriginalPass);
        dateOfBirth.setStyle(estiloOriginalFecha);
        
        
        try{
            
        SportActivityApp app = SportActivityApp.getInstance();
        user = app.getCurrentUser();
        
        if (!User.checkEmail(Email.getText())) {
                System.out.println("Invalid email format.");
                Email.setStyle("-fx-border-color: red;");
                
        }
        if (!User.checkPassword(passwordhided.getText())) {
                System.out.println("Invalid password. Must be 8-20 characters, with upper, lower, digit, and symbol.");
                passwordhided.setStyle("-fx-border-color: red;");
                password.setStyle("-fx-border-color: red;");
                
        }
        
//////////////// validar fecha, esta hecho con IA /////////////////////
        if (dateOfBirth == null || !User.isOlderThan(dateOfBirth.getValue(), 12)) {
            System.out.println("User must be older than 12 years.");
            dateOfBirth.setStyle("-fx-text-box-border: red; -fx-focus-color: red;-fx-text-fill: red;");
               
        }
        boolean fechaValida = true;

    try {
    // Sacamos exactamente lo que el usuario ha tecleado en la caja
        String textoTecleado = dateOfBirth.getEditor().getText();
    
    if (textoTecleado == null || textoTecleado.trim().isEmpty()) {
        fechaValida = false; // No ha puesto nada
    } else {
        // OBLIGAMOS a JavaFX a traducir ese texto a una fecha real.
        // Si escribió "asdskandfjianfs", esta línea fallará y saltará al 'catch'
        LocalDate fechaParseada = dateOfBirth.getConverter().fromString(textoTecleado);
        
        // Si logró traducirlo, comprobamos la edad
        if (fechaParseada == null || !User.isOlderThan(fechaParseada, 12)) {
            fechaValida = false; // Es una fecha válida pero es menor de 12 años
        } else {
            // Forzamos que el DatePicker guarde este valor correcto internamente
            dateOfBirth.setValue(fechaParseada);
        }
    }
    } catch (Exception e) {
    // ¡CAZADO! Si el programa cae aquí, es porque escribió letras o un formato que no es fecha.
        fechaValida = false;
    }

    // 2. Si la fecha resultó ser inválida (por letras, vacía o menor de edad), pintamos de rojo
    if (!fechaValida) {
        System.out.println("La fecha es inválida, tiene letras o el usuario es menor de 12 años.");
        dateOfBirth.setStyle("-fx-text-box-border: red; -fx-focus-color: red; -fx-text-color: red;");
        dateOfBirth.requestFocus();
        
    }
            
    ///////////////IA//////////////////
    ///
    ///
    ///
    ///
    ///
    ///HASTA AQUI LA IA
  
        user.setEmail(Email.getText());
        user.setPassword(passwordhided.getText());
        user.setBirthDate(dateOfBirth.getValue());
        if (currentAvatarFile != null) {
                user.setAvatarPath(currentAvatarFile.toString());
                
        }
        
        app.saveUser(user);
        
        }catch(Exception e){System.out.println("Error al guardar el perfil:");
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Crítico");
        alert.setHeaderText(null);
        alert.setContentText("Ha ocurrido un error inesperado al procesar los datos.");
        alert.showAndWait();
        }
        
        
        
        
    }

    @FXML
    private void darlesignout(ActionEvent event) {
        // volver a login
        
        try {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FitnessPrincess/auth/LoginView.fxml"));
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