package raros.drive;

import de.tuberlin.bbi.dr.ConfiguredConnection;
import de.tuberlin.bbi.dr.LayoutController;
import raros.plan.Drop;
import raros.plan.Pick;
import raros.plan.ShuntingPlan;
import raros.plan.ShuntingStep;
import util.GermanList;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static raros.Main.*;
import static raros.drive.Infrastruktur_SG.RechteSeite;

public class Example {
    final static ShuntingPlan plan = new ShuntingPlan(List.of(
            // new Pick("3", List.of("a", "b")),
            new Drop("2", List.of("b"), false),
            new Drop("3", List.of("a"), false)
    ));

    public static void main(String[] args) throws InterruptedException {
        ConfiguredConnection conn = null;
        try {
            conn = configureController();
            if (conn.getHandler().isConnected()) {
                shuntingMain();
            }
        } finally {
            if (conn != null) conn.getHandler().close();
        }
    }

    private static void shuntingMain() throws InterruptedException {
        final LocomotiveDriver loco = new LocomotiveDriver(LayoutController.vehicleByAddress(ROTE_LOK));
        Gleisharfe infrastruktur = RechteSeite;
        if (!infrastruktur.validateTracks(plan.getUsedTracks())) {
            System.out.println("Rangier-Plan nicht ausführbar.");
            return;
        }
        for (var step: plan.steps()) {
            infrastruktur.setFahrwegToTrack(Integer.parseInt(step.track()));
            loco.drive(true);
            doTheCoupling(step);
            loco.drive(false);
        }
        System.out.println("Programm wird beendet.");
        TimeUnit.SECONDS.sleep(2);
    }

    private static void doTheCoupling(ShuntingStep step) {
        String anweisung = step instanceof Pick ? " an den Zug kuppeln." : " abkuppeln und auf dem Gleis belassen.";
        System.out.println("Jetzt Wagen " + GermanList.join(step.cars()) + anweisung);
        System.out.print("ENTER drücken, wenn fertig.");
        System.console().readLine();
    }
}
