package raros.plan;

import util.Maps;

import java.util.*;

public class Planner {
    private final Tracks given;
    private final ShuntingTask task;
    private final Map<String, String> targetTracksByCarId = new HashMap<>();
    private final Map<String, TrackTrains> currentState;
    private List<String> carsOnLocomotive = new ArrayList<>();

    // TODO: maybe not needed
    private final List<String> targetTracks = new ArrayList<>();

    Planner(Tracks given, ShuntingTask task) {
        this.given = given;
        this.task = task;
        // deep copy the input value, so we don't mutate it later.
        this.currentState = Maps.mapValues(given.tracks(), TrackTrains::copy);
        initTargetTracks();
    }

    PlanningResult planAndValidate() {
        var validationProblems = Validator.validateTask(given, task);
        if (!validationProblems.isEmpty()) {
            throw new IllegalArgumentException("Invalid task: " + String.join("\n", validationProblems));
        }
        var plan = createPlan();
        var result = Simulator.simulate(given, plan);
        return new PlanningResult(plan, result);
    }

    ShuntingPlan createPlan() {
        List<ShuntingStep> result = new ArrayList<>();
        String currentTrack = getTrackToProcess();
        while (currentTrack != null) {
            // add one Pick and multiple Drop steps until all cars are distributed or only cars with full target tracks are left.
            List<String> pickedCars = currentState.get(currentTrack).pickAll();
            result.add(new Pick(currentTrack, pickedCars));
            carsOnLocomotive = pickedCars;
            while (!carsOnLocomotive.isEmpty()) {
                // if the target track has capacity, move there, otherwise drop it again on the current track
                String targetTrack = targetTracksByCarId.get(carsOnLocomotive.getLast());
                String dropTrack = currentState.get(targetTrack).size() < task.maxWagonsPerTrack() ? targetTrack : currentTrack;
                result.add(new Drop(dropTrack, List.of(carsOnLocomotive.getLast()), false));
                carsOnLocomotive.removeLast();
            }
            // updating current state on the go
            currentTrack = getTrackToProcess();
        }
        // TODO: the plan can have redundant steps when the first Drop goes to currentTrack again or multiple Drops go to the same track.
        //      Remove that in post-processing.
        return new ShuntingPlan(result);
    }

    void initTargetTracks() {
        targetTracks.addAll(task.targetTracks().keySet());

        for (var track : task.targetTracks().entrySet()) {
            var trackId = track.getKey();
            for (Train train : track.getValue().trains()) {
                for (String carId : train.carIds()) {
                    targetTracksByCarId.put(carId, trackId);
                }
            }
        }
    }

    // TODO: complex enough to warrant some unit tests :/
    // Returns the id of the track with the most "removable cars", i.e. cars whose target track has free capacity.
    // Since the value is 0 for tracks with only correct cars, we will automatically ignore them.
    // Return value of _null_ means that all cars are properly sorted!
    String getTrackToProcess() {
        Map<String, Integer> capacityPerTrack = Maps.mapValues(currentState, tt -> task.maxWagonsPerTrack() - tt.trains().size());
        String result = null;
        int maxToRemove = 0;
        for (var track : currentState.entrySet()) {
            int removableCars = removableCars(track.getKey(), track.getValue().trains(), capacityPerTrack);
            if (removableCars > maxToRemove) {
                maxToRemove = removableCars;
                result = track.getKey();
            }
        }
        return result;
    }

    // TODO: maybe the way to avoid the "swap-worst-case" is to return "blocked cars" separately.
    //       Then the track selector can decide whether to move some of the blocked cars away.
    //       We need to change the drop track for the "drop in case of full target track"... this could be either the track with highest capacity
    //       or also depend on the target track: one temp track for each full target track would make sense!
    int removableCars(String trackId, List<Train> trains, Map<String, Integer> capacityPerTargetTrack) {
        // count the number of cars by target track.
        Map<String, Integer> targetCarsPerTrack = new HashMap<>();
        for (var train : trains) {
            for (var carId : train.carIds()) {
                targetCarsPerTrack.merge(targetTracksByCarId.get(carId), 1, Integer::sum);
            }
        }

        // then cap the numbers by capacity (ignoring the current track) and add them up
        int result = 0;
        for (var targetCars : targetCarsPerTrack.entrySet()) {
            if (targetCars.getKey().equals(trackId)) {
                continue;
            }
            result += Math.min(targetCars.getValue(), capacityPerTargetTrack.get(targetCars.getKey()));
        }
        return result;
    }
}
