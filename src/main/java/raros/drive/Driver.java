package raros.drive;

import de.tuberlin.bbi.dr.Vehicle;
import javafx.application.Platform;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

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
        MAX(120);
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

    // null Vehicle means dry run with logging only.
    Vehicle vehicle = null;

    private boolean automaticDrive = false;

    // speed and onSpeedSince only changed together by setVehicleSpeed(), nowhere else!
    private Speed speed = Speed.STOP;
    private long onSpeedSince;

    private Vehicle.Direction direction = null;

    private final List<Pair<Speed, Long>> drivenMilliSeconds = new ArrayList<>();

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
                long elapsed = time - onSpeedSince;
                drivenMilliSeconds.add(new Pair<>(speed, elapsed));
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
     * Can be interrupted by setting automaticDrive to false.
     * In that case, the list of drivenMilliSeconds is changed to reflect the remaining drive.
     */
    public void driveBack(BiConsumer<Integer, Speed> remaining, Runnable done) {
        automaticDrive = true;
        executor.submit(() -> {
            try {
                setDirection(Vehicle.Direction.REVERSE);
                long totalRemaining = drivenMilliSeconds.stream().mapToLong(Pair::getValue).sum();
                while (automaticDrive && !drivenMilliSeconds.isEmpty()) {
                    var currentSpeed = drivenMilliSeconds.getLast().getKey();
                    var currentInterval = drivenMilliSeconds.getLast().getValue();
                    var startTime = System.currentTimeMillis();
                    long elapsed = 0;
                    setVehicleSpeed(currentSpeed);
                    while (automaticDrive && elapsed < currentInterval) {
                        elapsed = System.currentTimeMillis() - startTime;
                        final int currentlyRemaining = (int) ((totalRemaining - elapsed) / 1000);
                        Platform.runLater(() -> remaining.accept(currentlyRemaining, currentSpeed));
                        TimeUnit.MILLISECONDS.sleep(10);
                    }
                    drivenMilliSeconds.removeLast();
                    totalRemaining -= elapsed;
                    if (elapsed < currentInterval) {
                        // in this case, automaticDrive must be false and the outer loop will exit
                        drivenMilliSeconds.add(new Pair<>(currentSpeed, currentInterval - elapsed));
                    }
                }
                if (drivenMilliSeconds.isEmpty()) {
                    Platform.runLater(done);
                }
            } catch (InterruptedException e) {
                System.out.println();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                setVehicleSpeed(Speed.STOP);
            }
        });
    }
}
