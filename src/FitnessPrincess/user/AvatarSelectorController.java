package FitnessPrincess.user;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class AvatarSelectorController implements Initializable {

    @FXML private Button btnClose;
    @FXML private StackPane btnAddAvatar;
    @FXML private Button botonDone;

    @FXML private ImageView avatar1;
    @FXML private ImageView avatar2;
    @FXML private ImageView avatar3;
    @FXML private ImageView avatar4;
    @FXML private ImageView avatar5;

    private File avatarSeleccionado;
    private ImageView avatarMarcado;
    private final DropShadow efectoSeleccion = new DropShadow(25, Color.valueOf("#00d684"));

    // Reference of the principal screen
    private AvatarController parentController;

    public void setParentController(AvatarController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void darleadd(MouseEvent event) {
        if(avatarMarcado != null){
            avatarMarcado.setOpacity(1.0);
            avatarMarcado.setEffect(null);
            avatarMarcado = null;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nuevo Avatar");
        // Only .jpg and .jpeg images
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos de Imagen JPG", "*.jpg", "*.jpeg")
        );

        File archivoElegido = fileChooser.showOpenDialog(stage);

        if (archivoElegido != null) {
            this.avatarSeleccionado = archivoElegido;
            btnAddAvatar.setEffect(efectoSeleccion); 
        }
    }

    @FXML
    private void darledone(ActionEvent event) {
        // Selected photo is sent to the principal screen 
        if (this.avatarSeleccionado != null && parentController != null) {
            parentController.recibirNuevoAvatar(this.avatarSeleccionado);
        }
        cerrarPopup();
    }

    @FXML
    private void darlex(ActionEvent event) {
        cerrarPopup();
    }

    private void cerrarPopup() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void seleccionarAvatarDefecto(MouseEvent event) {
        btnAddAvatar.setEffect(null);

        if (avatarMarcado != null) {
            avatarMarcado.setEffect(null);
            avatarMarcado.setOpacity(1.0);
        }

        ImageView imagenClickeada = (ImageView) event.getSource();
        imagenClickeada.setEffect(efectoSeleccion);

        avatarMarcado = imagenClickeada;
        avatarMarcado.setOpacity(0.5);

        try {
            String rutaImagen = imagenClickeada.getImage().getUrl();
            if (rutaImagen != null) {
                if (rutaImagen.startsWith("file:")) {
                    this.avatarSeleccionado = new File(new URI(rutaImagen));
                } else {
                    this.avatarSeleccionado = new File(rutaImagen);
                }
            }
        } catch (Exception e) {
            System.out.println("No se pudo convertir la imagen a File: " + e.getMessage());
        }
    }
}