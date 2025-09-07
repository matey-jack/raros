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

    public CouplingControls(Driver driver, Runnable next) {
        this.driver = driver;
        this.next = next;

        var title = new Label("Wagen kuppeln");

        var creepFwdButton = new Button("Schieben");
        creepFwdButton.armedProperty().addListener((obs, was, is) -> {
                    if (is) driver.creepForward();
                }
        );
        creepFwdButton.setOnAction(e -> driver.stop());

        var creepBackButton = new Button("Ziehen");
        creepBackButton.armedProperty().addListener((obs, was, is) -> {
            if (is) driver.creepBackward();
        });
        creepBackButton.setOnAction(e -> driver.stop());
        var creepBox = new HBox(creepFwdButton, creepBackButton);

        var doneButton = new Button("Fertig");
        doneButton.setOnAction(e -> next.run());
        root = new VBox(title, instructions, creepBox, doneButton);
    }

    public void setStep(ShuntingStep step) {
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
