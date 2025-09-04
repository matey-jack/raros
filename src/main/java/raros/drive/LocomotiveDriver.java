package raros.drive;

import de.tuberlin.bbi.dr.Vehicle;

public class LocomotiveDriver {
    private final Vehicle vehicle;
    final int DEFAULT_SPEED = 96;

    public LocomotiveDriver(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Locomotive on track must always face forward into the Gleisharfe, backwards into the Ausziehgleis.
     */
    void drive(boolean toGleisharfe) {
        String direction = toGleisharfe ? "in die Gleisharfe zu fahren." : "ins Ausziehgleis zurück zu setzen.";
        System.out.println("ENTER drücken, um " + direction);
        System.console().readLine();

        vehicle.setDirection(toGleisharfe ? Vehicle.Direction.NORMAL : Vehicle.Direction.REVERSE);
        vehicle.setSpeedStep(DEFAULT_SPEED);

        System.out.println("ENTER drücken, um anzuhalten.");
        System.console().readLine();
        vehicle.setSpeedStep(0);
    }
}
