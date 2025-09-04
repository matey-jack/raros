package raros.drive;

import de.tuberlin.bbi.dr.LayoutController;

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
            LayoutController.turnoutByAddress(point.turnoutId())
                    .setPosition(point.turnoutPosition());
        }
        try {
            TimeUnit.SECONDS.sleep(1);
            for (FahrwegElement point : points) {
                if (LayoutController.turnoutByAddress(point.turnoutId()).getPosition() != point.turnoutPosition()) {
                    TimeUnit.SECONDS.sleep(1);
                }
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

}
