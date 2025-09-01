package raros.plan;

import java.util.*;

public class Planner {
    private final Tracks given;
    private final ShuntingTask task;
    private final Map<String, String> targetTracksByCarId = new HashMap<>();

    // "free" means that there are only "correct" cars on the track.
    // (That is, we can add more correct cars without limits, since the total number of target cars doesn't exceed
    // the capacity of the track.)
    private final Set<String> freeTargetTracks = new HashSet<>();

    private final Set<String> remainingInputTracks = new HashSet<>();

    private String currentParkingTrack;
    private final List<String> targetTracks = new ArrayList<>();

    Planner(Tracks given, ShuntingTask task) {
        this.given = given;
        this.task = task;
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
        // step 1: if not all target tracks are free, remove cars from them until all of them are free or no more space is available on other tracks.
        compressInputCars();

        /* step 2: pick a working track (any track with cars targeting any of the free target tracks) and distribute its cars.
                - cars with a non-free target get parked on the working track (=parking track).
                - if all target tracks are free, distribute all cars and then the same for remaining input tracks.
                - if not all target tracks are free, then free the working track as soon as the number of working cars (on the working track + on the locomotive)
                  fit on other tracks.
                - if the working track can't distribute any more cars, pick another working track according to above rule.

            There is a rare case in which the first full target track needs to be used as a working track...
            which we can implement later...
         */
    }

    /*
        Algorithm: Move cars away from the target tracks until:
            - either all target tracks are free,
            -

        We can later optimize this by clearing those target tracks first which have the largest number of target cars.
     */
    void compressInputCars() {

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
}
