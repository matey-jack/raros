package raros;

import de.tuberlin.bbi.dr.Turnout.Position;

public record FahrwegElement(
        int turnoutId,
        Position turnoutPosition
) {
}