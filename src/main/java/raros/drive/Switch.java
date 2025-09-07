package raros.drive;

import de.tuberlin.bbi.dr.Turnout;

public record Switch(
        Turnout turnout,
        FahrwegElement weiche
) {
    @Override
    public String toString() {
        var status = currentPosition() == weiche.turnoutPosition() ? "liegt richtig." : "wird umgelegt...";
        return "Weiche " + weiche.turnoutId() + " auf " + weiche.positionText() + " " + status;
    }

    public boolean isDone() {
        return currentPosition() == desiredPosition();
    }

    // this actually queries LocoNet
    public Turnout.Position currentPosition() {
        return turnout.getPosition();
    }

    public Turnout.Position desiredPosition() {
        return weiche.turnoutPosition();
    }
}
