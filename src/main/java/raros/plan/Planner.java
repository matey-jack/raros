package raros.plan;

import util.Maps;

import java.util.*;
import java.util.stream.Collectors;

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
        var validationResult = Validator.checkResult(result, task);
        if (!validationResult.isEmpty()) {
            System.out.println("Validation problems:");
            for (var p : validationResult) {
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
            result.add(new Pick(currentTrack, new ArrayList<>(pickedCars)));
            List<String> carsOnLocomotive = pickedCars.reversed();
            while (!carsOnLocomotive.isEmpty()) {
                // if the target track has capacity, move there, otherwise drop it again on the current track
                String targetTrack = targetTracksByCarId.get(carsOnLocomotive.getLast());
                String dropTrack = currentState.get(targetTrack).numberOfCars() < task.maxWagonsPerTrack()
                        ? targetTrack
                        : getTrackWithLeastCarsExcept(currentTrack);
                boolean couple = !currentState.get(dropTrack).trains().isEmpty();
                List<String> dropCars = List.of(carsOnLocomotive.getLast());
                result.add(new Drop(dropTrack, new ArrayList<>(dropCars), couple));
                currentState.get(dropTrack).addCars(dropCars, couple);
                carsOnLocomotive.removeLast();
            }
            // updating current state on the go
            currentTrack = getTrackToProcess();
        }
        compressPlan(result);
        return new ShuntingPlan(result);
    }

    String getTrackWithLeastCarsExcept(String exceptTrackId) {
        // Note that we use the negative to avoid having to program a Maps.min function
        Map<String, Integer> carsPerTrack = Maps.mapValues(currentState, tt -> -tt.numberOfCars());
        carsPerTrack.remove(exceptTrackId);
        return Maps.max(carsPerTrack).get().getKey();
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
                    plan.remove(i + 1);
                } else {
                    if (second instanceof Drop) {
                        // Drop all cars in the first step.
                        first.cars().addAll(second.cars());
                        plan.remove(i + 1);
                    } else {
                        // Instead of Drop followed by Pick, keep the cars on the locomotive.
                        // Note that this is the only case where we remove the first step.
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

    // Returns the id of the track with the most "removable cars", i.e. cars whose target track has free capacity.
    // Since the value is 0 for tracks with only correct cars, we will automatically ignore them.
    // Return value of _null_ means that all cars are properly sorted!
    String getTrackToProcess() {
        var capacityPerTrack = getCapacityPerTrack();
        var removableCars = Maps.createMap(currentState.keySet(), (trackId -> removableCars(trackId, capacityPerTrack)));
        var maxRemovableCars = Maps.max(removableCars).get();
        if (maxRemovableCars.getValue() > 0) {
            return maxRemovableCars.getKey();
        }

        // If no cars are removable, we might have a deadlock, so let's look for unfinished cars instead.
        var unfinishedCars = Maps.createMap(currentState.keySet(), this::unfinishedCars);
        var maxUnfinishedCars = Maps.max(unfinishedCars).get();
        if (maxUnfinishedCars.getValue() > 0) {
            return maxUnfinishedCars.getKey();
        }
        return null;
    }

    Map<String, Integer> getCapacityPerTrack() {
        return Maps.mapValues(currentState, tt -> task.maxWagonsPerTrack() - tt.numberOfCars());
    }

    /***
     * @return Number of cars on @param trackId which need to be moved to another track.
     */
    long unfinishedCars(String trackId) {
        return currentState.get(trackId).getAllCars().stream()
                .filter(car -> !targetTracksByCarId.get(car).equals(trackId))
                .count();
    }

    long removableCars(String trackId, Map<String, Integer> capacityPerTargetTrack) {
        // count the number of cars by target track.
        var targetCarsPerTrack = currentState.get(trackId).getAllCars().stream()
                .collect(Collectors.groupingBy(targetTracksByCarId::get, Collectors.counting()));

        // then cap the numbers by capacity (ignoring the current track) and add them up
        long result = 0;
        for (var targetCars : targetCarsPerTrack.entrySet()) {
            if (targetCars.getKey().equals(trackId)) {
                continue;
            }
            result += Math.min(targetCars.getValue(), capacityPerTargetTrack.get(targetCars.getKey()));
        }
        return result;
    }
}
