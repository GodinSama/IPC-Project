package FitnessPrincess.maps;

import FitnessPrincess.app.MainLayoutController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.util.Objects;

public class MapCreationController {

    @FXML
    private void goBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(getClass().getResource("/FitnessPrincess/app/MainLayout.fxml"))
            );
            Parent root = loader.load();

            // Tell MainLayout to show the Maps tab as active
            MainLayoutController mainCtrl = loader.getController();
            mainCtrl.showMapManagement();

            stage.setScene(new Scene(root));
        } catch (Exception e) {
            System.err.println("Could not return to Map Management.");
            e.printStackTrace();
        }
    }
}