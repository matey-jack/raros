package raros;

import de.tuberlin.bbi.dr.LayoutController;
import de.tuberlin.bbi.dr.Turnout.Position;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Infrastruktur_SG {
    public static final int W1 = 381;
    public static final int W2 = 380;
    private static final int W3 = 383;
    private static final int W4 = 382;

    public static List<Integer> allTrackSections = List.of(34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48);
    public static int MIDDLE_TRACK_SECTION = 40;

    public static Gleisharfe LinkeSeite = new Gleisharfe(Map.of(
            1, Set.of(new FahrwegElement(W1, Position.CLOSED)),
            2, Set.of(
                    new FahrwegElement(W1, Position.THROWN),
                    new FahrwegElement(W2, Position.CLOSED)
            )
    )) {
        public boolean initialize() {
            var w3 = LayoutController.turnoutByAddress(W3);
            w3.setPosition(Position.CLOSED);
            var w4 = LayoutController.turnoutByAddress(W4);
            w4.setPosition(Position.CLOSED);
            return true;
        }
    };

    public static final int W11 = 378;
    public static final int W12 = 384;
    public static final int W13 = 379;
    private static final int W14 = 371;

    public static Gleisharfe RechteSeite = new Gleisharfe(Map.of(
            2, Set.of(
                    new FahrwegElement(W11, Position.THROWN),
                    new FahrwegElement(W12, Position.CLOSED)
            ),
            3, Set.of(
                    new FahrwegElement(W11, Position.THROWN),
                    new FahrwegElement(W12, Position.THROWN),
                    new FahrwegElement(W13, Position.CLOSED)
            ),
            4, Set.of(
                    new FahrwegElement(W11, Position.THROWN),
                    new FahrwegElement(W12, Position.THROWN),
                    new FahrwegElement(W13, Position.THROWN)
            )
    )) {
        public boolean initialize() {
            var w14 = LayoutController.turnoutByAddress(W14);
            w14.setPosition(Position.THROWN);
            return true;
        }
    };
}

