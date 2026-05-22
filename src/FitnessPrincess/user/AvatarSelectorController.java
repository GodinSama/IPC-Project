/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package FitnessPrincess.user;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void darleadd(MouseEvent event) {
    }

    @FXML
    private void darledone(ActionEvent event) {
    }
    
}
