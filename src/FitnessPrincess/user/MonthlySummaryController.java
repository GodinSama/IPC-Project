package FitnessPrincess.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.time.YearMonth;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import upv.ipc.sportlib.Activity;
import upv.ipc.sportlib.SportActivityApp;

public class MonthlySummaryController implements Initializable {

    @FXML private Label monthLabel;
    @FXML private Label timeLabel;
    @FXML private Label distanceLabel;
    @FXML private Label ascentLabel;
    @FXML private Label descentLabel;
    @FXML private Button nextBtn;

    private YearMonth currentMonth;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentMonth = YearMonth.now();
        updateMonthView();
    }
   
    private void updateMonthView() {
        // --- show the current month ---
        String formattedDate = currentMonth.format(formatter);
        formattedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        monthLabel.setText(formattedDate);
        
        // --- right arrow logic ---
        YearMonth realMaxMonth = YearMonth.now();
        if (currentMonth.equals(realMaxMonth) || currentMonth.isAfter(realMaxMonth)) {
            nextBtn.setDisable(true);
        } else {
            nextBtn.setDisable(false);
        }
        
        // --- calculate data ---
        double totalDistance = 0.0;
        Duration totalTime = Duration.ZERO;
        int totalAscent = 0;
        int totalDescent = 0;

        SportActivityApp app = SportActivityApp.getInstance();
        
        for (Activity activity : app.getUserActivities()) {
            YearMonth activityMonth = YearMonth.from(activity.getStartTime());
            
            if (activityMonth.equals(currentMonth)) {
                totalDistance += activity.getTotalDistance();
                totalTime = totalTime.plus(activity.getDuration());
                
                totalAscent += (int) activity.getElevationGain(); 
                totalDescent += (int) activity.getElevationLoss();
            }
        }

        // --- show the calculated data ---
        long hours = totalTime.toHours();
        long minutes = totalTime.toMinutesPart(); 
        
        timeLabel.setText(hours + "h " + minutes + "min");
        
        distanceLabel.setText(String.format(Locale.ENGLISH, "%.1f Km", totalDistance)); 
        
        ascentLabel.setText("+ " + totalAscent + " m");
        descentLabel.setText("- " + totalDescent + " m");
    }

    @FXML
    private void prevMonth(ActionEvent event) {
        currentMonth = currentMonth.minusMonths(1);
        updateMonthView();
    }

    @FXML
    private void nextMonth(ActionEvent event) {
        if (currentMonth.isBefore(YearMonth.now())) {
            currentMonth = currentMonth.plusMonths(1);
            updateMonthView();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}