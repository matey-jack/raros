package raros.plan;


import java.util.*;

public class Validator {
    /*
        Return a set of validation errors. Empty set means everything is valid.

        1. Check the uniqueness and completeness of car IDs.
            Optional: we could allow car IDs not to be mentioned in the target state
            as long as their number does not exceed the capacity of tracks not used in the target state.

        2. Target state has at most maxWagonsPerTrack on each track.

        3. Target state has at most one train per track.

        4. Total number of wagons doesn't exceed (numTracks-1)*maxWagonsPerTrack.
     */
    public List<String> validateTask(Tracks given, ShuntingTask task, List<String> availableTracks) {
        List<String> result = new ArrayList<>();
        Map<String, TrackTrains> givenTracks = given.tracks();
        var givenCarIds = getAllCarIds(givenTracks);
        checkForDuplicates(givenCarIds, "given", result);
        Map<String, TrackTrains> targetTracks = task.targetTracks();
        var targetCarIds = getAllCarIds(targetTracks);
        checkForDuplicates(targetCarIds, "target", result);
        checkForMissingCars(givenCarIds, targetCarIds, "given", result);
        checkForMissingCars(targetCarIds, givenCarIds, "target", result);
        checkTracksExist(givenTracks.keySet(), "given", availableTracks, result);
        checkTracksExist(targetTracks.keySet(), "target", availableTracks, result);
        checkTrackCapacity(givenTracks, "given", task.maxWagonsPerTrack(), result);
        checkTrackCapacity(targetTracks, "target", task.maxWagonsPerTrack(), result);
        oneTrainPerTrack(targetTracks, result);
        checkSpareCapacity(task, givenCarIds.size(), result);
        return result;
    }

    static List<String> getAllCarIds(Map<String, TrackTrains> tracks) {
        var result = new ArrayList<String>();
        for (var track : tracks.values()) {
            for (var train : track.trains()) {
                result.addAll(train.carIds());
            }
        }
        return result;
    }

    static Set<String> getDuplicates(Collection<String> input) {
        var foundOnce = new HashSet<String>();
        var result = new HashSet<String>();
        for (var id : input) {
            if (foundOnce.contains(id)) {
                result.add(id);
            }
            foundOnce.add(id);
        }
        return result;
    }

    static void checkForDuplicates(Collection<String> ids, String label, List<String> validationErrors) {
        var duplicates = getDuplicates(ids);
        if (!duplicates.isEmpty()) {
            validationErrors.add(
                    "The " + label + " data contains duplicate car IDs: "
                            + String.join(", ", duplicates) + "."
            );
        }
    }

    static void checkForMissingCars(List<String> carIds, List<String> requiredCarIds, String label, List<String> result) {
        // There doesn't seem to be a Java standard method for immutable set substraction, so we create temporary mutable sets.
        var missing = new HashSet<>(requiredCarIds);
        missing.removeAll(carIds);
        if (!missing.isEmpty()) {
            result.add(
                    "Some car IDs are missing in the " + label + " state: "
                            + String.join(", ", missing) + "."
            );
        }
    }

    static <T extends Train> void checkTracksExist(Set<String> trackIds, String label, List<String> availableTracks, List<String> results) {
        var invalidTracks = new HashSet<>(trackIds);
        invalidTracks.removeAll(availableTracks);
        if (!invalidTracks.isEmpty()) {
            results.add(
                    "The " + label + " tracks " + String.join(", ", invalidTracks) + " don't exist in the infrastructure."
            );
        }
    }

    private void checkTrackCapacity(Map<String, TrackTrains> givenTracks, String label, int capacity, List<String> result) {
        for (var track : givenTracks.entrySet()) {
            int numCars = track.getValue().size();
            if (numCars > capacity) {
                result.add(label + " track " + track.getKey() + " has " + numCars + " cars, but only " + capacity + " are allowed.");
            }
        }
    }

    private void oneTrainPerTrack(Map<String, TrackTrains> targetTracks, List<String> result) {
        for (var track : targetTracks.entrySet()) {
            if (track.getValue().trains().size() > 1) {
                result.add("Track " + track.getKey() + " has more than one train.");
            }
        }
    }

    private void checkSpareCapacity(ShuntingTask task, int numWagons, List<String> result) {
        var numTracks = task.targetTracks().size();
        var capacity = (numTracks - 1) * task.maxWagonsPerTrack();
        if (numWagons > capacity) {
            result.add("There are " + numWagons + " wagons to be shunted, " +
                    "which is above the capacity of " + capacity + " wagons, given " + numTracks + " tracks.");
        }
    }

    public List<String> validatePlan(ShuntingPlan plan, List<String> availableTracks) {
        List<String> report = new ArrayList<>();
        for (var step : plan.steps()) {
            checkStep(step, availableTracks, report);
        }
        return report;
    }

    private void checkStep(ShuntingStep step, List<String> availableTracks, List<String> report) {
//        if (!availableTracks.contains(step.fromTrack())) {
//            report.add("fromTrack " + step.fromTrack() + " is not available in the infrastructure.");
//        }
//        if (!availableTracks.contains(step.toTrack())) {
//            report.add("toTrack " + step.toTrack() + " is not available in the infrastructure.");
//        }
//        var dropCars = step.dropCars().stream().flatMap(List::stream).toList();
//        if (!step.pickCars().equals(dropCars)) {
//            report.add("pickCars [" + String.join(", ", step.pickCars()) + "]" +
//                    " does not match dropCars [" + String.join(", ", dropCars) + "].");
//        }
    }

    public List<String> checkResult(Tracks state, Tracks target) {
        List<String> report = new ArrayList<>();
        // TODO: check that keySet are the same
        for (var track : target.tracks().keySet()) {
            checkTrack(track, state.tracks().get(track).trains(), target.tracks().get(track).trains(), report);
        }
        return report;
    }

    private void checkTrack(String track, List<Train> trains, List<Train> requests, List<String> report) {
        if (trains.size() != requests.size()) {
            report.add(
                    "Track " + track + " has " + trains.size() + " trains, " +
                            "but should have " + requests.size() + ".");
            return;
        }
        for (var i = 0; i < trains.size(); i++) {
            checkTrain(track, i, trains.get(i), requests.get(i), report);
        }
    }

    private void checkTrain(String track, int trainNumber, Train train, Train request, List<String> report) {
        var targetSize = request.carIds().size();
        if (train.carIds().size() != targetSize) {
            report.add(
                    "Train " + trainNumber + " on track " + track + ":" +
                            " expected " + targetSize + " cars, but only got " + train.carIds().size() + ".");
            return;
        }
        var actualCars = new HashSet<>(train.carIds());
        var expectedCars = new HashSet<>(request.carIds());
        if (!actualCars.equals(expectedCars)) {
            report.add(
                    "Train " + trainNumber + " on track " + track + " has wrong car(s): " +
                            "[" + String.join(", ", actualCars) + "] vs [" + String.join(", ", expectedCars) + "]."
            );
        }
    }
}
