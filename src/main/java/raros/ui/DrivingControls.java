package raros.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

public class DrivingControls {
    final Label title = new Label();
    final Button drivingButton;
    final Button doneButton;
    final Button confirmClearButton;
    final List<Button> speedButtons;
    final Map<String, Integer> speeds = Map.of(
            "X", 30,
            "C", 60,
            "V", 90,
            "B", 105,
            "N", 115,
            "M", 127
    );
    final Pane root;

    private static Button createSpeedButton(Map.Entry<String, Integer> speed) {
        return new Button(speed.getKey() + " - " + speed.getValue());
    }

    public DrivingControls() {
        this.drivingButton = new Button("Fahren");
        this.doneButton = new Button("Fertig");
        this.confirmClearButton = new Button("Weiche ist frei");
        confirmClearButton.setVisible(false);
        this.speedButtons = speeds.entrySet().stream().map(DrivingControls::createSpeedButton).toList();
        var speedBox = new HBox(speedButtons.toArray(new Button[0]));
        var doneBox = new HBox(doneButton, confirmClearButton);
        this.root = new VBox(title, speedBox, drivingButton, doneBox);
        root.setPadding(new Insets(16));
    }

    public void setDirection(ShuntingState state) {
        switch (state) {
            case DRIVING_IN:
                title.setText("Fahrt ins Harfengleis");
                doneButton.setText("Kuppeln beginnen");
                break;
            case DRIVING_OUT:
                title.setText("Fahrt ins Ausziehgleis");
                doneButton.setText("Kehren");
                break;
        }
    }
}
