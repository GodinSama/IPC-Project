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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;
import java.net.URI;
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

    // variables privadas creadas para metodos
    
    private File avatarSeleccionado;
    
    
    private ImageView avatarMarcado;
    
    
    private final DropShadow efectoSeleccion = new DropShadow(25, Color.valueOf("#00d684"));
    
    
    @FXML
    private ImageView avatar1;
    @FXML
    private ImageView avatar2;
    @FXML
    private ImageView avatar3;
    @FXML
    private ImageView avatar4;
    @FXML
    private ImageView avatar5;
    
    
    

    /**
     * Initializes the controller class.
     */
    @Override
public void initialize(URL url, ResourceBundle rb) {
    
}    
    

    @FXML
    private void darleadd(MouseEvent event) {
        
        
        if(avatarMarcado!=null){
        avatarMarcado.setOpacity(1.0);
        }
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
         try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvatarView.fxml"));
            Parent root = loader.load();
            
            
            if (this.avatarSeleccionado != null) {
                AvatarController avatarCtrl = loader.getController();
                avatarCtrl.recibirNuevoAvatar(this.avatarSeleccionado);
            }
            
            stage = (Stage) botonDone.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar Avatar.fxml");
        }
    }

    @FXML
    private void darledone(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvatarView.fxml"));
            Parent root = loader.load();
            
            if (this.avatarSeleccionado != null) {
                AvatarController avatarCtrl = loader.getController();
                avatarCtrl.recibirNuevoAvatar(this.avatarSeleccionado);
            }
            
            Stage stage = (Stage) botonDone.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar Avatar.fxml");
        }
    }

    @FXML
    private void darlex(ActionEvent event) {
        try {
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AvatarView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnClose.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No se ha podido cargar Avatar.fxml");
        }
    }

    @FXML
    private void seleccionarAvatarDefecto(MouseEvent event) {
        
        if (avatarMarcado != null) {
            avatarMarcado.setEffect(null);
            avatarMarcado.setOpacity(1.0);
        }

        
        
        ImageView imagenClickeada = (ImageView) event.getSource();

        
        imagenClickeada.setEffect(efectoSeleccion);

        
        avatarMarcado = imagenClickeada;
        
        
        try {
            // Sacamos la ruta 
            String rutaImagen = imagenClickeada.getImage().getUrl();
            
            if (rutaImagen != null) {
                // Convertimos esa ruta en un File 
                this.avatarSeleccionado = new File(new URI(rutaImagen));
                System.out.println("Avatar por defecto seleccionado: " + avatarSeleccionado.getName());
                avatarMarcado.setOpacity(0.5);
            }
        } catch (Exception e) {
            System.out.println("No se pudo convertir la imagen a File.");
        }
    }
    }
