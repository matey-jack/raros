package raros.plan;

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
    private final Map<String, TrackTrains<TrainState>> currentState;

    public static Tracks<TrainState> simulate(Tracks<TrainState> given, ShuntingPlan plan) {
        var sim = new Simulator(given);
        sim.processPlan(plan);
        return new Tracks<>(sim.currentState);
    }

    Simulator(Tracks<TrainState> given) {
        currentState = given.tracks();
    }

    private void processPlan(ShuntingPlan plan) {
        List<ShuntingStep> steps = plan.steps();
        for (int i = 0; i < steps.size(); i++) {
            processStep(i, steps.get(i));
        }
    }

    void processStep(int stepNo, ShuntingStep step) {
        try {
            var originTrack = currentState.get(step.fromTrack());
            removeCars(step.pickCars(), originTrack);
            var targetTrack = currentState.computeIfAbsent(step.toTrack(), (id) -> new TrackTrains<>(new ArrayList<>()));
            addCars(step.dropCars(), targetTrack.trains());
        } catch (Exception e) {
            throw new RuntimeException("Error in step " + stepNo + ": " + e.getMessage(), e);
        }
    }

    void removeCars(List<String> carIds, TrackTrains<TrainState> tt) {
        for (var car : carIds.reversed()) {
            removeCar(car, tt.trains());
        }
    }

    void removeCar(String carId, List<TrainState> trains) {
        var lastTrain = trains.getLast();
        removeCar(carId, lastTrain);
        if (lastTrain.carIds().isEmpty()) {
            trains.removeLast();
        }
    }

    void removeCar(String carId, TrainState t) {
        if (!t.carIds().getLast().equals(carId)) {
            throw new RuntimeException("Car " + carId + " to be removed, but " + t.carIds().getLast() + " is present.");
        }
        t.carIds().removeLast();
    }

    void addCars(List<List<String>> packets, List<TrainState> trains) {
        // if there is already something on the target track, add the first packet to the last train
        if (!trains.isEmpty()) {
            trains.getLast().carIds().addAll(packets.getFirst());
            packets.removeFirst();
        }
        // add all other packets as separate trains
        for (var p : packets) {
            trains.add(new TrainState(p));
        }
    }
}
