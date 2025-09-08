package raros.drive;

import de.tuberlin.bbi.dr.Turnout.Position;

public record FahrwegElement(
        int turnoutId,
        String weichenbezeichnung,
        Position turnoutPosition
) {
    public String positionText() {
        return turnoutPosition == Position.THROWN ? "abbiegend" : "gerade";
    }
}