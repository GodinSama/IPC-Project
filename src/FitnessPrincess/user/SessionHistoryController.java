package FitnessPrincess.user;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import upv.ipc.sportlib.SportActivityApp;
import upv.ipc.sportlib.Session;
import upv.ipc.sportlib.User;

public class SessionHistoryController implements Initializable {

    // ── FXML nodes ────────────────────────────────────────────────────────────
    @FXML private Button  prevMonthButton;
    @FXML private Button  nextMonthButton;
    @FXML private Label   monthLabel;

    @FXML private ScrollPane scrollPane;
    @FXML private VBox       sessionsContainer;

    @FXML private Label totalSessionsLabel;
    @FXML private Label totalDurationLabel;
    @FXML private Label totalImportedLabel;
    @FXML private Label totalViewedLabel;
    @FXML private Label totalAnnotationsLabel;

    // ── State ─────────────────────────────────────────────────────────────────
    private YearMonth currentMonth;
    private SportActivityApp app;

    // ── Formatters ────────────────────────────────────────────────────────────
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("EEE d MMM yyyy");

    // ── Initialisation ────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentMonth = YearMonth.now();
        app = SportActivityApp.getInstance();
        wireButtons();
        loadUserSessions();
    }

    private void wireButtons() {
        if (prevMonthButton != null) {
            prevMonthButton.setOnAction(e -> {
                currentMonth = currentMonth.minusMonths(1);
                refreshView();
            });
        }
        if (nextMonthButton != null) {
            nextMonthButton.setOnAction(e -> {
                currentMonth = currentMonth.plusMonths(1);
                refreshView();
            });
        }
    }

    // ── Data loading ──────────────────────────────────────────────────────────

    /** Called whenever the month changes. */
    private void refreshView() {
        if (monthLabel != null) {
            monthLabel.setText(currentMonth.format(MONTH_FMT));
        }
        loadUserSessions();
    }

    private void loadUserSessions() {
        sessionsContainer.getChildren().clear();

        User currentUser = app.getCurrentUser();
        if (currentUser == null) return;

        List<Session> allSessions = app.getSessionsByUser(currentUser);

        List<Session> monthSessions = new ArrayList<>();
        for (Session s : allSessions) {
            LocalDateTime start = s.getStartTime();
            if (start != null
                    && start.getYear()        == currentMonth.getYear()
                    && start.getMonthValue()  == currentMonth.getMonthValue()) {
                monthSessions.add(s);
            }
        }

        if (monthSessions.isEmpty()) {
            showEmptyState();
            clearTotals();
            return;
        }

        int cardIndex = monthSessions.size();
        long totalMinutes = 0;
        int  totalImported    = 0;
        int  totalViewed      = 0;
        int  totalAnnotations = 0;

        for (Session session : monthSessions) {
            long durationMins = session.getDuration() == null ? 0
                    : session.getDuration().toMinutes();
            totalMinutes      += durationMins;
            totalImported     += session.getImportedActivities();
            totalViewed       += session.getViewedActivities();
            totalAnnotations  += session.getAnnotationsCreated();

            Node card = buildCard(
                    cardIndex--,
                    session.getStartTime(),
                    session.getEndTime(),
                    durationMins,
                    session.getImportedActivities(),
                    session.getViewedActivities(),
                    session.getAnnotationsCreated()
            );
            sessionsContainer.getChildren().add(card);
        }

        updateTotals(monthSessions.size(), totalMinutes,
                totalImported, totalViewed, totalAnnotations);
    }

    // ── Card builder ──────────────────────────────────────────────────────────

    private Node buildCard(int index,
                           LocalDateTime start,
                           LocalDateTime end,
                           long durationMinutes,
                           int imported,
                           int viewed,
                           int annotations) {

        VBox card = new VBox(0);
        card.getStyleClass().add("sh-card");

        // ── Header row ──────────────────────────────────────────────────────
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("sh-card-header");

        Label sessionNum = new Label(String.format("Session %02d", index));
        sessionNum.getStyleClass().add("sh-card-session-num");

        Label timeRange = new Label(
                (start != null ? start.format(TIME_FMT) : "??:??")
                        + " – "
                        + (end   != null ? end.format(TIME_FMT)   : "??:??")
        );
        timeRange.getStyleClass().add("sh-card-time");

        Label dateLabel = new Label(start != null ? start.format(DATE_FMT) : "");
        dateLabel.getStyleClass().add("sh-card-date");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label durationBadge = new Label(formatDuration(durationMinutes));
        durationBadge.getStyleClass().add("sh-card-duration-badge");

        Label chevron = new Label("▼");
        chevron.getStyleClass().add("sh-card-chevron");

        header.getChildren().addAll(sessionNum, timeRange, dateLabel, spacer, durationBadge, chevron);

        // ── Stats grid (hidden by default) ──────────────────────────────────
        GridPane stats = new GridPane();
        stats.setHgap(24);
        stats.setVgap(10);
        stats.setVisible(false);
        stats.setManaged(false);
        stats.getStyleClass().add("sh-card-stats");
        GridPane.setMargin(stats, new Insets(0));

        addStat(stats, "⏱", "Total time",         formatDuration(durationMinutes), 0, 0);
        addStat(stats, "📝","Annotations created", String.valueOf(annotations),      0, 1);
        addStat(stats, "🏃","Activities viewed",   String.valueOf(viewed),            1, 0);
        addStat(stats, "🗂","Activities imported", String.valueOf(imported),          1, 1);

        // ── Toggle on header click ───────────────────────────────────────────
        header.setOnMouseClicked(e -> {
            boolean nowVisible = !stats.isVisible();
            stats.setVisible(nowVisible);
            stats.setManaged(nowVisible);
            chevron.setText(nowVisible ? "▲" : "▼");
            card.getStyleClass().remove("sh-card-expanded");
            if (nowVisible) card.getStyleClass().add("sh-card-expanded");
        });

        card.getChildren().addAll(header, stats);
        return card;
    }

    private void addStat(GridPane grid, String icon, String key, String value, int col, int row) {
        HBox cell = new HBox(8);
        cell.setAlignment(Pos.CENTER_LEFT);
        cell.getStyleClass().add("sh-stat-cell");

        Label iconLbl = new Label(icon);
        iconLbl.getStyleClass().add("sh-stat-icon");

        Label keyLbl = new Label(key + ":");
        keyLbl.getStyleClass().add("sh-stat-key");

        Label valLbl = new Label(value);
        valLbl.getStyleClass().add("sh-stat-value");

        cell.getChildren().addAll(iconLbl, keyLbl, valLbl);
        grid.add(cell, col, row);
    }

    // ── Totals bar ────────────────────────────────────────────────────────────

    private void updateTotals(int sessions, long totalMinutes,
                              int imported, int viewed, int annotations) {
        if (totalSessionsLabel   != null) totalSessionsLabel.setText(String.valueOf(sessions));
        if (totalDurationLabel   != null) totalDurationLabel.setText(formatDuration(totalMinutes));
        if (totalImportedLabel   != null) totalImportedLabel.setText(String.valueOf(imported));
        if (totalViewedLabel     != null) totalViewedLabel.setText(String.valueOf(viewed));
        if (totalAnnotationsLabel!= null) totalAnnotationsLabel.setText(String.valueOf(annotations));
    }

    private void clearTotals() {
        updateTotals(0, 0, 0, 0, 0);
    }

    // ── Empty state ───────────────────────────────────────────────────────────

    private void showEmptyState() {
        VBox empty = new VBox(12);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(60));

        Label icon = new Label("📋");
        icon.setStyle("-fx-font-size: 48px; -fx-opacity: 0.35;");

        Label msg = new Label("No sessions recorded for " + currentMonth.format(MONTH_FMT));
        msg.getStyleClass().add("sh-empty-label");

        empty.getChildren().addAll(icon, msg);
        sessionsContainer.getChildren().add(empty);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private String formatDuration(long totalMinutes) {
        if (totalMinutes <= 0) return "0m";
        long hours = totalMinutes / 60;
        long mins  = totalMinutes % 60;
        if (hours == 0) return mins + "m";
        if (mins  == 0) return hours + "h";
        return hours + "h " + mins + "m";
    }

    // --- To show the monthly summary ---
    @FXML private void onShowSummary() {
        try {
            javafx.stage.Stage mainWindow = (javafx.stage.Stage) prevMonthButton.getScene().getWindow();

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/FitnessPrincess/user/MonthlySummaryView.fxml")
            );
            javafx.scene.Parent root = loader.load();

            javafx.stage.Stage popupStage = new javafx.stage.Stage();

            popupStage.initOwner(mainWindow);
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root, mainWindow.getWidth(), mainWindow.getHeight());
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

            popupStage.setScene(scene);

            popupStage.setX(mainWindow.getX());
            popupStage.setY(mainWindow.getY());

            popupStage.show();

        } catch (Exception e) {
            System.err.println("Error trying to open Monthly Summary.");
            e.printStackTrace();
        }
    }

}