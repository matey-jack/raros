package raros.drive;

import de.tuberlin.bbi.dr.LayoutController;
import de.tuberlin.bbi.dr.Turnout;
import util.GermanList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class Gleisharfe {
    Map<Integer, Set<FahrwegElement>> fahrwegKonfiguration;

    public Gleisharfe(Map<Integer, Set<FahrwegElement>> fahrwegKonfiguration) {
        this.fahrwegKonfiguration = fahrwegKonfiguration;
    }

    public List<Integer> getAllTrackIds() {
        return fahrwegKonfiguration.keySet().stream().toList();
    }

    public abstract boolean initialize();

    /**
     * Fahrweg zum Gleis einstellen und alle Weichen prüfen.
     *
     * @param trackId - Gleis in der Harfe zu dem es gehen soll.
     * @return true bei Erfolgreicher Einstellung und Prüfung.
     */
    public boolean setFahrwegToTrack(int trackId) {
        var points = fahrwegKonfiguration.get(trackId);
        for (FahrwegElement point : points) {
            Turnout t = LayoutController.turnoutByAddress(point.turnoutId());
            if (t.getPosition() != point.turnoutPosition()) {
                t.setPosition(point.turnoutPosition());
            }
        }
        try {
            List<String> awaitedPoints;
            do {
                TimeUnit.SECONDS.sleep(1);
                awaitedPoints = new ArrayList<>();
                for (FahrwegElement point : points) {
                    if (LayoutController.turnoutByAddress(point.turnoutId()).getPosition() != point.turnoutPosition()) {
                        awaitedPoints.add(Integer.toString(point.turnoutId()));
                        System.out.println("Stelle Weiche " + point.turnoutId() + " auf '" + point.positionText() + "'.");
                    }
                }
                System.out.println(
                        "Warten auf Endstellung der Weichen " + GermanList.join(awaitedPoints) + "."
                );
            } while (!awaitedPoints.isEmpty());
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

}
