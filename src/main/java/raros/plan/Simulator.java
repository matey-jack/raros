package raros.plan;

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
    final Map<String, TrackTrains> currentState;
    Simulator(Tracks given) {
        currentState = given.tracks();
    }

    void removeCar(String carId, TrainState t) {
        if (!t.carIds().getLast().equals(carId)) {
            throw new RuntimeException("Car " + carId + " to be removed, but " + t.carIds().getLast() + " is present.");
        }
        t.carIds().removeLast();
    }

    void removeCar(String carId, TrackTrains<TrainState> tt) {
        var lastTrain = tt.trains().getLast();
        removeCar(carId, lastTrain);
        if (lastTrain.carIds().isEmpty()) {
            tt.trains().removeLast();
        }
    }

    void removeCars(List<String> carIds, TrackTrains<TrainState> tt) {
        for (var car : carIds.reversed()) {
            removeCar(car, tt);
        }
    }

    private void addCars(List<List<String>> packets, TrackTrains<TrainState> tt) {
        //if (tt.trains().getLast().cars().getLast().equals())
    }

    void processStep(ShuntingStep step) {
        var originTrack = currentState.get(step.fromTrack());
        removeCars(step.pickCars(), originTrack);
        var targetTrack = currentState.get(step.toTrack());
        addCars(step.dropCars(), targetTrack);
    }
}
