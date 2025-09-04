package raros.drive;

import de.tuberlin.bbi.dr.Turnout.Position;

public record FahrwegElement(
        int turnoutId,
        Position turnoutPosition
) {
    public String positionText() {
        return turnoutPosition == Position.THROWN ? "abbiegend" : "gerade";
    }
}