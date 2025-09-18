package raros.ui;

import de.tuberlin.bbi.dr.Vehicle;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import raros.drive.Driver;

import java.util.Map;


public class DrivingControls {
    enum DrivingState {
        DRIVING_IN, DRIVING_OUT_AUTOMATIC, DRIVING_OUT_MANUAL;
    }
    DrivingState drivingState;

    final Driver driver;
    final Runnable next;
    final Pane root;
    private final Label title = new Label();
    private final Label remainingTime = new Label();
    private final Button doneButton;
    private final Button confirmClearButton;
    private Map<Driver.Speed, Label> speedLabels = Map.of(
            Driver.Speed.CREEP, new Label("C - Creep/Kriechgang"),
            Driver.Speed.NORMAL, new Label("N - Normal"),
            Driver.Speed.MAX, new Label("M - Maximal")
    );

    public DrivingControls(Driver driver, Runnable next) {
        this.driver = driver;
        this.next = next;
        Button drivingButton = setUpDrivingButton(driver);

        confirmClearButton = new Button("Weiche ist frei");
        confirmClearButton.setVisible(false);
        confirmClearButton.setOnAction(e -> {
            confirmClearButton.setVisible(false);
            next.run();
        });

        doneButton = new Button("Fertig");
        doneButton.setOnAction(this::onDone);
        var doneBox = new HBox(doneButton, confirmClearButton);
        // TODO: make this an option list or at least several labels, so that the current speed can be shown
        //      (in bold font or similar).
        Label speedsLabel = new Label("Geschwindigkeiten:");
        HBox speedsBox = new HBox(speedsLabel);
        for (var s : Driver.Speed.values()) {
            var l = speedLabels.get(s);
            if (l != null) speedsBox.getChildren().add(l);
        }
        root = new VBox(title, speedsLabel, drivingButton, remainingTime, doneBox);
        root.setPadding(new Insets(16));
    }

    private void onDone(ActionEvent e) {
        if (drivingState == DrivingState.DRIVING_IN) {
            next.run();
        } else {
            // Button is disabled during automatic driving, thus this must be manual driving out.
            confirmClearButton.setVisible(!confirmClearButton.isVisible());
        }
    }

    private Button setUpDrivingButton(Driver driver) {
        Button button = new Button("Fahren");
        // this is buttonPressed (= start holding).
        button.armedProperty().addListener(
                (obs, was, is) -> {
                    if (!is) return;
                    if (drivingState == DrivingState.DRIVING_OUT_AUTOMATIC) {
                        driver.driveBack(this::setRemainingTime, this::finishedMinimalDriving);
                    } else {
                        driver.startCreeping();
                    }
                }
        );
        // this is button released.
        // Driver has to be smart and stop automatic or manual driving whichever is happening.
        button.setOnAction(e -> driver.stop());
        // speed letters
        button.setOnKeyPressed(e -> {
            var speed = switch (e.getCode()) {
                case C -> Driver.Speed.CREEP;
                case N -> Driver.Speed.NORMAL;
                case M -> Driver.Speed.MAX;
                default -> null;
            };
            if (speed == null) return;
            driver.setSpeed(speed);
            showSpeed(speed);
        });
        return button;
    }

    private void showSpeed(Driver.Speed speed) {
        for (var l : speedLabels.values()) {
            l.setStyle("");
        }
        speedLabels.get(speed).setStyle("-fx-font-weight: bold;");
    }

    public void setDirection(ShuntingState state) {
        showSpeed(Driver.Speed.CREEP);
        switch (state) {
            case DRIVING_IN -> {
                drivingState = DrivingState.DRIVING_IN;
                title.setText("Fahrt ins Abstellgleis");
                doneButton.setText("Kuppeln beginnen");
                driver.setDirection(Vehicle.Direction.NORMAL);
            }
            case DRIVING_OUT -> {
                drivingState = DrivingState.DRIVING_OUT_AUTOMATIC;
                title.setText("Fahrt ins Ausziehgleis");
                doneButton.setText("Kehren");
                doneButton.setDisable(true);
                remainingTime.setVisible(true);
                driver.setDirection(Vehicle.Direction.REVERSE);
            }
        }
    }

    public void setRemainingTime(int remainingTime, Driver.Speed speed) {
        showSpeed(speed);
        this.remainingTime.setText("Mindestfahrzeit zum Ausziehgleis " + remainingTime + " Sekunden.");
    }

    public void finishedMinimalDriving() {
        drivingState = DrivingState.DRIVING_OUT_MANUAL;
        showSpeed(Driver.Speed.CREEP);
        doneButton.setDisable(false);
        remainingTime.setVisible(false);
    }
}
