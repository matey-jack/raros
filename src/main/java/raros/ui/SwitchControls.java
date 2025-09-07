package raros.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SwitchControls {
    final Runnable next;
    final private Label statusLabel = new Label();
    final private Button confirmButton = new Button();
    final public Pane root = new VBox(statusLabel, confirmButton);

    SwitchControls(Runnable next) {
        this.next = next;
        confirmButton.setOnAction(e -> next.run());
        reset();
    }

    void reset() {
        statusLabel.setText("Weichen werden umgestellt.");
        confirmButton.setText("Bitte Warten.");
        confirmButton.setDisable(true);
    }

    public void setSwitchStatus(List<String> status) {
        statusLabel.setText(String.join("\n",status));
    }

    public void done() {
        confirmButton.setText("Ok");
        confirmButton.setDisable(false);
    }
}
