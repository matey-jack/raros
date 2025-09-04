package raros.drive;

import de.tuberlin.bbi.dr.LayoutController;
import raros.plan.Drop;
import raros.plan.Pick;
import raros.plan.ShuntingPlan;
import raros.plan.ShuntingStep;
import util.GermanList;

import java.util.List;

import static raros.Main.*;
import static raros.drive.Infrastruktur_SG.RechteSeite;

public class Example {
    final static ShuntingPlan plan = new ShuntingPlan(List.of(
            new Pick("3", List.of("a", "b")),
            new Drop("2", List.of("b"), false),
            new Drop("3", List.of("a"), false)
    ));

    public static void main(String[] args) {
        var conn = configureController();
        final LocomotiveDriver loco = new LocomotiveDriver(LayoutController.vehicleByAddress(ROTE_LOK));
        Gleisharfe infrastruktur = RechteSeite;
        if (!infrastruktur.validateTracks(plan.getUsedTracks())) {
            System.out.println("Rangier-Plan nicht ausführbar.");
            return;
        }
        for (var step: plan.steps()) {
            //infrastruktur.setFahrwegToTrack(Integer.parseInt(step.track()));
            loco.drive(true);
            doTheCoupling(step);
            loco.drive(false);
        }

        sleepSeconds(2);
        conn.getHandler().close();
    }

    private static void doTheCoupling(ShuntingStep step) {
        String anweisung = step instanceof Pick ? " an den Zug kuppeln." : " abkuppeln und auf dem Gleis belassen.";
        System.out.println("Jetzt Wagen " + GermanList.join(step.cars()) + anweisung);
        System.out.println("ENTER drücken, wenn fertig.");
        System.console().readLine();
    }
}
