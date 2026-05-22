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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void darlex(ActionEvent event) {
    }

    @FXML
    private void darleedit(ActionEvent event) {
    }

    @FXML
    private void darlesave(ActionEvent event) {
    }

    @FXML
    private void darlesignout(ActionEvent event) {
    }
    
}
