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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author dgimb
 */
public class AvatarSelectorController implements Initializable {

    @FXML
    private Button btnClose;
    @FXML
    private StackPane btnAddAvatar;
    @FXML
    private Circle darleadd;
    @FXML
    private Button botonDone;

    // variable privada
    private File avatarSeleccionado;
    
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void darleadd(MouseEvent event) {
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nuevo Avatar");
        
       
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Archivos de Imagen JPG", "*.jpg", "*.jpeg")
        );
        
        
        File archivoElegido = fileChooser.showOpenDialog(stage);
        
        
        if (archivoElegido != null) {
            this.avatarSeleccionado = archivoElegido;
            System.out.println("Archivo guardado con éxito: " + avatarSeleccionado.getAbsolutePath());
            
            
        }
    }

    @FXML
    private void darledone(ActionEvent event) {
        try{
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
            Parent root = loader.load();
            
            
            if (this.avatarSeleccionado != null) {
                ProfileViewController profileCtrl = loader.getController();
                profileCtrl.recibirNuevoAvatar(this.avatarSeleccionado);
            }
            
            
            Stage stage = (Stage) botonDone.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar ProfileView.fxml");
        
    }
        
    }

    @FXML
    private void darlex(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ProfileView.fxml"));
            Parent root = loader.load();
            
           
            Stage stage = (Stage) btnClose.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar ProfileView.fxml");
        }
    }
}