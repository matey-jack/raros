package raros.drive;

import de.tuberlin.bbi.dr.Vehicle;

public class Driver {
    public enum Speed {
        STOP(0),
        CREEP(45),
        NORMAL(90),
        MAX(125);
        final int speed;

        Speed(int speed) {
            this.speed = speed;
        }
    }

    // null Vehicle means dry run with logging only.
    Vehicle vehicle = null;
    private Speed speed = Speed.STOP;

    public void setDirection(Vehicle.Direction direction) {
        System.out.println("Set direction to " + direction.toString());
        if (vehicle != null) {
            vehicle.setDirection(direction);
        }
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void startCreeping() {
        if (vehicle == null) {
            System.out.println("Starting fake drive.");
        } else {
            System.out.println("Starting " + vehicle.getDirection().toString() + ".");
        }
        setVehicleSpeed(Speed.CREEP);
    }

    public void stop() {
        setVehicleSpeed(Speed.STOP);
    }

    public void setSpeed(Speed newSpeed) {
        if (this.speed == Speed.STOP) {
            return;
        }
        this.speed = newSpeed;
        setVehicleSpeed(newSpeed);
    }

    // this is private, so we don't go to higher speeds accidentally.
    private void setVehicleSpeed(Speed newSpeed) {
        speed = newSpeed;
        System.out.println("Set speed to " + speed.toString());
        if (vehicle != null) {
            vehicle.setSpeedStep(speed.speed);
        }
    }

    public void creepForward() {
        setDirection(Vehicle.Direction.NORMAL);
        setVehicleSpeed(Speed.CREEP);
    }

    public void creepBackward() {
        setDirection(Vehicle.Direction.REVERSE);
        setVehicleSpeed(Speed.CREEP);
    }

}
