package raros.ui;

import de.tuberlin.bbi.dr.ConfiguredConnection;
import de.tuberlin.bbi.dr.LayoutController;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import raros.drive.Driver;
import raros.drive.Example;
import raros.drive.Gleisharfe;
import raros.drive.Switcher;

import static raros.MainTest.*;
import static raros.drive.Infrastruktur_SG.RechteSeite;

public class Main extends Application {
    final ShuntingUiState state = new ShuntingUiState(Example.plan);
    final Gleisharfe infrastruktur = RechteSeite;

    final Label topStatus = new Label();
    final VBox root = new VBox();
    Scene scene;
    DrivingControls drivingControls;
    CouplingControls couplingControls;
    SwitchControls switchControls;

    ConfiguredConnection conn = null;
    final Driver driver = new Driver();
    final Switcher switcher = new Switcher(infrastruktur);

    public static void main(String[] args) {
        launch(args);
    }

    public void connect() {
        conn = configureController();
        System.out.println(conn.getInfo());
        if (!conn.getHandler().isConnected()) {
            System.out.println("Connection to LocoNet failed!");
            conn = null;
            return;
        }
        driver.setVehicle(LayoutController.vehicleByAddress(ROTE_LOK));
        switcher.setRealRun();
    }

    @Override
    public void start(Stage stage) {
        drivingControls = new DrivingControls(driver, this::next);
        couplingControls = new CouplingControls(driver, this::next);
        switchControls = new SwitchControls(this::next);

        stage.setTitle("RaRoS Steuerung für Rangierbegleiter");

        root.getChildren().add(createInitScreen());
        scene = new Scene(root, 450, 300);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        System.out.println("Window closed, stopping.");
        if (conn != null) {
            driver.stop();
            conn.getHandler().close();
        }
        super.stop();
    }

    private Pane createInitScreen() {
        Button connectButton = new Button("Verbinden mit LokoNet");
        connectButton.setOnAction(_e -> {
            connect();
            firstStep();
        });

        Button dryRunButton = new Button("Testbetrieb ohne LocoNet");
        dryRunButton.setOnAction(e -> firstStep());

        return new VBox(connectButton, dryRunButton);
    }

    private void firstStep() {
        setProgress();
        root.getChildren().removeLast();
        switcher.switchTo(state.currentStep.track(), switchControls::setSwitchStatus, switchControls::done);
        root.getChildren().addAll(topStatus, switchControls.root);
    }

    public void setProgress() {
        String mode = (conn==null) ? "TEST MODUS" : "LOCONET AKTIV";
        topStatus.setText("[" + mode + "] Rangierschritt " + (state.stepNumber + 1) + " von " + state.totalSteps + ".");
    }

    public void next() {
        root.getChildren().removeLast();
        state.next();
        setProgress();
        if (state.done()) {
            root.getChildren().clear();
            root.getChildren().add(new Label("Rangierplan abgeschlossen!"));
            return;
        }
        switch (state.state) {
            case COUPLING -> {
                couplingControls.setStep(state.currentStep);
                root.getChildren().add(couplingControls.root);
            }
            case DRIVING_OUT, DRIVING_IN -> {
                drivingControls.setDirection(state.state);
                root.getChildren().add(drivingControls.root);
            }
            case SWITCHING_POINTS -> {
                switchControls.reset();
                switcher.switchTo(state.currentStep.track(), switchControls::setSwitchStatus, switchControls::done);
                root.getChildren().add(switchControls.root);
            }
        }
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

}