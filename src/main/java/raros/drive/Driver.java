package raros.drive;

import de.tuberlin.bbi.dr.Vehicle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * When manually setting the speed (and stop), methods of this class run in the calling thread.
 * Only when driving automatically (back to the head shunt) a separate thread is used.
 * This allows the UI to be used concurrently, for example, to show a progress bar or enable emergency stop.
 */
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

    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "switcher");
        t.setDaemon(true);
        return t;
    });

    private final Speed[] descendingSpeeds = List.of(Speed.values()).reversed().toArray(Speed[]::new);

    // null Vehicle means dry run with logging only.
    Vehicle vehicle = null;

    // speed and onSpeedSince only changed together by setVehicleSpeed(), nowhere else!
    private Speed speed = Speed.STOP;
    private long onSpeedSince;

    private Vehicle.Direction direction = null;

    private Map<Speed, Long> drivenMilliSeconds = new HashMap<>();

    public void setDirection(Vehicle.Direction direction) {
        System.out.println("Set direction to " + direction.toString());
        this.direction = direction;
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

    /**
     * Public method only allows changing speed if the locomotive is already driving.
     *
     * @param newSpeed
     */
    public void setSpeed(Speed newSpeed) {
        if (this.speed == Speed.STOP) {
            return;
        }
        this.speed = newSpeed;
        setVehicleSpeed(newSpeed);
    }

    // this is private, so we don't go to higher speeds accidentally.
    private void setVehicleSpeed(Speed newSpeed) {
        if (direction == Vehicle.Direction.NORMAL) {
            long time = System.currentTimeMillis();
            if (speed != Speed.STOP) {
                Long previous = drivenMilliSeconds.getOrDefault(speed, 0L);
                long elapsed = time - onSpeedSince;
                drivenMilliSeconds.put(speed, elapsed + previous);
            }
            onSpeedSince = time;
        }
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

    /**
     * Drives back out of the siding with exactly the speed and duration that was recorded on driving in.
     */
    public void driveBack() {
        executor.submit(() -> {
            try {
                setDirection(Vehicle.Direction.REVERSE);
                try {
                    for (Speed speed : descendingSpeeds) {
                        if (speed == Speed.STOP) {
                            continue;
                        }
                        setVehicleSpeed(speed);
                        TimeUnit.MILLISECONDS.sleep(drivenMilliSeconds.getOrDefault(speed, 0L));
                    }
                } catch (InterruptedException e) {
                    System.out.println();
                } finally {
                    setVehicleSpeed(Speed.STOP);
                    drivenMilliSeconds.clear();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }
}
