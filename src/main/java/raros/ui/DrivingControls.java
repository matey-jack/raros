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


public class DrivingControls {
    final Driver driver;
    final Runnable next;
    final Pane root;
    private final Label title = new Label();
    private final Button doneButton;
    private final Button confirmClearButton;
    private boolean drivingIn = true;

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
        Label speedsLabel = new Label("Geschwindigkeiten: C - Creep/Kriechgang, N - Normal, M - Maximal");
        root = new VBox(title, speedsLabel, drivingButton, doneBox);
        root.setPadding(new Insets(16));
    }

    private void onDone(ActionEvent e) {
        if (drivingIn) {
            next.run();
        } else {
            confirmClearButton.setVisible(!confirmClearButton.isVisible());
        }
    }

    private Button setUpDrivingButton(Driver driver) {
        Button button = new Button("Fahren");
        // this is buttonPressed (= start holding).
        button.armedProperty().addListener(
                (obs, was, is) -> {
                    if (is) driver.startCreeping();
                }
        );
        // this is button released.
        button.setOnAction(e -> driver.stop());
        // speed letters
        button.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case C -> driver.setSpeed(Driver.Speed.CREEP);
                case N -> driver.setSpeed(Driver.Speed.NORMAL);
                case M -> driver.setSpeed(Driver.Speed.MAX);
            }
        });
        return button;
    }

    public void setDirection(ShuntingState state) {
        switch (state) {
            case DRIVING_IN -> {
                drivingIn = true;
                title.setText("Fahrt ins Abstellgleis");
                doneButton.setText("Kuppeln beginnen");
                driver.setDirection(Vehicle.Direction.NORMAL);
            }
            case DRIVING_OUT -> {
                drivingIn = false;
                title.setText("Fahrt ins Ausziehgleis");
                doneButton.setText("Kehren");
                driver.setDirection(Vehicle.Direction.REVERSE);
            }
        }
    }
}
