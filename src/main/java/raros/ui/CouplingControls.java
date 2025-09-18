package raros.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import raros.drive.Driver;
import raros.plan.Drop;
import raros.plan.ShuntingStep;
import util.GermanList;


public class CouplingControls {
    final Driver driver;
    final Runnable next;
    final Pane root;
    private final Label instructions = new Label();
    private final Button creepFwdButton = new Button("Schieben");
    private final Button creepBackButton = new Button("Ziehen");
    private final Button doneButton = new Button("Fertig");

    public CouplingControls(Driver driver, Runnable next) {
        this.driver = driver;
        this.next = next;

        var title = new Label("Wagen kuppeln");

        creepFwdButton.armedProperty().addListener((obs, was, is) -> {
                    if (is) driver.creepForward();
                }
        );
        creepFwdButton.setOnAction(e -> driver.stop());

        creepBackButton.armedProperty().addListener((obs, was, is) -> {
            if (is) driver.creepBackward();
        });
        creepBackButton.setOnAction(e -> driver.stop());
        var creepBox = new HBox(creepFwdButton, creepBackButton);

        doneButton.setOnAction(e -> driveBack());
        root = new VBox(title, instructions, creepBox, doneButton);
    }

    private void driveBack() {
        creepFwdButton.setDisable(true);
        creepBackButton.setDisable(true);
        doneButton.setDisable(true);
        next.run();
    }

    public void setStep(ShuntingStep step) {
        creepFwdButton.setDisable(false);
        creepBackButton.setDisable(false);
        doneButton.setDisable(false);
        if (step instanceof Drop d) {
            var ankuppeln = d.couple() ? ", an vorhandene Wagen ankuppeln " : "";
            instructions.setText(
                    "Wagen " + GermanList.join(step.cars()) + " vom Zug abkuppeln" + ankuppeln + " und auf dem Gleis belassen."
            );
        } else {
            instructions.setText("Wagen " + GermanList.join(step.cars()) + " an den Rangierzug kuppeln.");
        }
    }
}
