package raros.ui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    final Label topStatus = new Label();
    final VBox root = new VBox(topStatus);
    DrivingControls drivingControls;
    ShuntingUiState exampleState = new ShuntingUiState(
            ShuntingState.DRIVING_IN,
            null,
            1,
            10
    );

    @Override
    public void start(Stage stage) {
        drivingControls = new DrivingControls();
        root.getChildren().addAll(drivingControls.root);
        stage.setTitle("RaRoS Steuerung für Rangierbegleiter");
        stage.setScene(new Scene(root, 420, 300));
        stage.show();
        setState(exampleState);
    }

    public void setState(ShuntingUiState state) {
        topStatus.setText("Rangierschritt " + state.stepNumber() + " von " + state.totalSteps() + ".");
        drivingControls.setDirection(state.state());
    }

    public void startGPT(Stage stage) {
        // Controls
        var cbA = new CheckBox("Enable A");
        var cbB = new CheckBox("Enable B");

        var runBtn = new Button("Run");
        var clearBtn = new Button("Clear");
        var exitBtn = new Button("Exit");

        var output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);
        output.setPrefRowCount(6);

        // Simple status text bound to checkbox state
        var status = new Label();
        status.textProperty().bind(
                Bindings.createStringBinding(
                        () -> (cbA.isSelected() || cbB.isSelected())
                                ? "Status: ready"
                                : "Status: select at least one option",
                        cbA.selectedProperty(), cbB.selectedProperty()
                )
        );

        // Disable "Run" until something is selected
        runBtn.disableProperty().bind(
                cbA.selectedProperty().not().and(cbB.selectedProperty().not())
        );

        // Actions
        runBtn.setOnAction(e -> {
            var sb = new StringBuilder("Running with:\n");
            if (cbA.isSelected()) sb.append(" • A\n");
            if (cbB.isSelected()) sb.append(" • B\n");
            sb.append("Time: ").append(java.time.LocalTime.now()).append("\n\n");
            output.appendText(sb.toString());
        });

        clearBtn.setOnAction(e -> output.clear());
        exitBtn.setOnAction(e -> stage.close());

        // Layout
        var options = new HBox(12, cbA, cbB);
        var buttons = new HBox(12, runBtn, clearBtn, exitBtn);
        var root = new VBox(12,
                new Label("Simple Control Panel"),
                options,
                buttons,
                new Label("Output:"),
                output,
                status
        );
        root.setPadding(new Insets(16));

        stage.setTitle("My JavaFX Window");
        stage.setScene(new Scene(root, 420, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}