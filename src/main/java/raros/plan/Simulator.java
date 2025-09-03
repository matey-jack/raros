package raros.plan;

import util.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
    This class simulates a shunting plan by modifying the given state according to the steps in the plan.
    The result can then be compared to the expected target.

    Lesson learned so far: the lack of explicit coupling and decoupling instructions in the Shunting Steps moves
    a lot of logic to the execution stage. In practice this might be an advantage, because staff can see how cars are coupled
    and adapt easily to any states different from how they were reported to be.
 */
public class Simulator {
    private final Map<String, TrackTrains> currentTracks;
    private final List<String> carsOnLocomotive = new ArrayList<>();

    public static Tracks simulate(Tracks given, ShuntingPlan plan) {
        var sim = new Simulator(given);
        sim.processPlan(plan);
        // We can return the state without copying, because nothing will change it anymore.
        return new Tracks(sim.currentTracks);
    }

    Simulator(Tracks given) {
        // deep copy the input value, so we don't mutate it later.
        currentTracks = Maps.mapValuesOnly(given.tracks(), TrackTrains::copy);
    }

    private void processPlan(ShuntingPlan plan) {
        List<ShuntingStep> steps = plan.steps();
        for (int i = 0; i < steps.size(); i++) {
            processStep(i, steps.get(i));
        }
    }

    void processStep(int stepNo, ShuntingStep step) {
        TrackTrains track = currentTracks.get(step.track());
        List<Train> trackTrains = track.trains();
        try {
            if (step instanceof Pick) {
                for (var car : step.cars().reversed()) {
                    removeCar(car, trackTrains);
                    carsOnLocomotive.add(car);
                }
            } else if (step instanceof Drop) {
                for (var car : step.cars()) {
                    removeFromLocomotive(car);
                }
                track.addCars(step.cars(), ((Drop) step).couple());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error in step " + stepNo + ": " + e.getMessage(), e);
        }
    }

    private void removeFromLocomotive(String carId) {
        if (!carsOnLocomotive.getLast().equals(carId)) {
            throw new RuntimeException("Car " + carId + " to be removed, but " + carsOnLocomotive.getLast() + " is present.");
        }
        carsOnLocomotive.removeLast();
    }

    void removeCar(String carId, List<Train> trains) {
        var lastTrain = trains.getLast();
        removeCar(carId, lastTrain);
        if (lastTrain.carIds().isEmpty()) {
            trains.removeLast();
        }
    }

    void removeCar(String carId, Train t) {
        String lastCarId = t.carIds().getLast();
        if (!lastCarId.equals(carId)) {
            throw new RuntimeException("Car " + carId + " to be removed, but " + lastCarId + " is present.");
        }
        t.carIds().removeLast();
    }
}
