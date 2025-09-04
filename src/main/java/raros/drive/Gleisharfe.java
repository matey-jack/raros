package raros.drive;

import de.tuberlin.bbi.dr.LayoutController;
import de.tuberlin.bbi.dr.Turnout;
import util.GermanList;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Gleisharfe {
    final Map<Integer, Set<FahrwegElement>> fahrwegKonfiguration;

    public Gleisharfe(Map<Integer, Set<FahrwegElement>> fahrwegKonfiguration) {
        this.fahrwegKonfiguration = fahrwegKonfiguration;
    }

    public boolean validateTracks(Collection<String> tracks) {
        boolean valid = true;
        for (var track : tracks) {
            if (!fahrwegKonfiguration.containsKey(Integer.parseInt(track))) {
                System.out.println("Gleis " + track + " existiert nicht in der Infrastruktur.");
                valid = false;
            }
        }
        return valid;
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
                System.out.println("Stelle Weiche " + point.turnoutId() + " auf '" + point.positionText() + "'.");
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
