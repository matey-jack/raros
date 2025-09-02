package raros.plan;

import util.Maps;

import java.util.*;

public class Planner {
    private final Tracks given;
    private final ShuntingTask task;
    private final Map<String, String> targetTracksByCarId = new HashMap<>();
    private final Map<String, TrackTrains> currentState;

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
        var validationResult = new ArrayList<String>();
        Validator.validateTrainTracks(result.tracks(), "planning result", task.maxWagonsPerTrack(), validationResult);
        if (!validationResult.isEmpty()) {
            System.out.println("Validation problems:");
            for (var p: validationResult) {
                System.out.println(p);
            }
        }
        return new PlanningResult(plan, result);
    }

    ShuntingPlan createPlan() {
        List<ShuntingStep> result = new ArrayList<>();
        String currentTrack = getTrackToProcess();
        while (currentTrack != null) {
            // add one Pick and multiple Drop steps until all cars are distributed or only cars with full target tracks are left.
            List<String> pickedCars = currentState.get(currentTrack).pickAll();
            // copy the list to an immutable one
            result.add(new Pick(currentTrack, pickedCars.stream().toList()));
            List<String> carsOnLocomotive = pickedCars.reversed();
            while (!carsOnLocomotive.isEmpty()) {
                // if the target track has capacity, move there, otherwise drop it again on the current track
                String targetTrack = targetTracksByCarId.get(carsOnLocomotive.getLast());
                String dropTrack = currentState.get(targetTrack).size() < task.maxWagonsPerTrack() ? targetTrack : currentTrack;
                boolean couple = !currentState.get(targetTrack).trains().isEmpty();
                List<String> dropCars = List.of(carsOnLocomotive.getLast());
                result.add(new Drop(dropTrack, new ArrayList<>(dropCars), couple));
                currentState.get(targetTrack).addCars(dropCars, couple);
                carsOnLocomotive.removeLast();
            }
            // updating current state on the go
            currentTrack = getTrackToProcess();
        }
        compressPlan(result);
        return new ShuntingPlan(result);
    }

    /***
     * Removes redundant steps from the plan.
     * Drop+Drop becomes a larger Drop, while Drop+Pick (and Pick+Drop) becomes a smaller Pick.
     */
    void compressPlan(List<ShuntingStep> plan) {
        var i = 0;
        // it has to be a while loop, because we progress either be advancing the index or shorteing the list.
        while (i < plan.size() - 1) {
            ShuntingStep first = plan.get(i);
            ShuntingStep second = plan.get(i + 1);
            if (first.track().equals(second.track())) {
                // Modify the first step and remove the second.
                if (first instanceof Pick) {
                    if (second instanceof Drop) {
                        // Modify the Pick to not take the cars that would be dropped next.
                        first.cars().subList(0, second.cars().size()).clear();
                    } else {
                        // Note that even if we removed a lot of Drop steps after a Pick, there should be at least one Drop left,
                        // since the Pick track was chosen to have at least one car going to a different track.
                        throw new IllegalArgumentException("Subsequent Pick steps should not happen!");
                    }
                    plan.remove(i+1);
                } else {
                    if (second instanceof Drop) {
                        // Drop all cars in the first step.
                        first.cars().addAll(second.cars());
                        plan.remove(i+1);
                    } else {
                        // Instead of Drop followed by Pick, keep the cars on the locomotive.
                        // Note that this is the only case, where we remove the first step.
                        // Also note that all our modifications are easier because Pick always picks the whole track full of cars.
                        // This is why we can be sure that the cars in the Drop are actually on the pick and can be removed.
                        second.cars().reversed().subList(0, first.cars().size()).clear();
                        plan.remove(i);
                    }
                }
            } else {
                i++;
            }
        }
    }
    void initTargetTracks() {
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
