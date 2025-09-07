package raros.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SwitchControls {
    final Runnable next;
    final private Label statusLabel = new Label("Weichen werden umgestellt.");
    final private Button confirmButton = new Button("Bitte Warten.");
    final public Pane root = new VBox(statusLabel, confirmButton);

    SwitchControls(Runnable next) {
        this.next = next;
        confirmButton.setDisable(true);
        confirmButton.setOnAction(e -> next.run());
    }

    public void setSwitchStatus(String status) {
        statusLabel.setText(status);
    }

    public void done() {
        confirmButton.setText("Ok");
        confirmButton.setDisable(false);
    }
}
