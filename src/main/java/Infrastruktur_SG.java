import de.tuberlin.bbi.dr.LayoutController;

import java.util.List;
import java.util.Map;

import static de.tuberlin.bbi.dr.Turnout.Position.CLOSED;
import static de.tuberlin.bbi.dr.Turnout.Position.THROWN;

public class Infrastruktur_SG {
    public static class LinkeSeite implements Gleisharfe {
        public static final int W1 = 381;
        public static final int W2 = 380;
        private static final int W3 = 380;
        private static final int W4 = 380;

        // now that could be read from a CSV file ...
        Map<Integer, FahrwegElement> fahrwegKonfiguration;

        public void initialize(LayoutController controller) {
            var w3 = LayoutController.turnoutByAddress(W3);
            w3.setPosition(CLOSED);
            var w4 = LayoutController.turnoutByAddress(W4);
            w4.setPosition(CLOSED);
        }

        @Override
        public List<Integer> getAllTrackIds() {
            return List.of(1, 2, 3, 4);
        }

        @Override
        public boolean setFahrwegToTrack(int trackId) {
            return false;
        }
    }

    public static class RechteSeite {
        public static final int W11 = 378;
        public static final int W12 = 384;
        public static final int W13 = 379;
        private static final int W14 = 371;

        public void initialize(LayoutController controller) {
            var w14 = LayoutController.turnoutByAddress(W14);
            w14.setPosition(THROWN);
        }
    }
}
