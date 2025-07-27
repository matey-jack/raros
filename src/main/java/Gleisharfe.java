import java.util.List;

public interface Gleisharfe {
    List<Integer> getAllTrackIds();

    /**
     * Fahrweg zum Gleis einstellen und alle Weichen prüfen.
     *
     * @param trackId - Gleis in der Harfe zu dem es gehen soll.
     * @return true bei Erfolgreicher Einstellung und Prüfung.
     */
    boolean setFahrwegToTrack(int trackId);

}
