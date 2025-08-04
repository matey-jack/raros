package raros.plan;


import java.util.*;

public class Validator {
    /*
        Return a set of validation errors. Empty set means everything is valid.

        1. Check the uniqueness and completeness of car IDs.

        2. Check that there is one free track for each car packet in the target state.

        Note that target-track-ids and given-track-ids might overlap, since car packets will only be placed on target tracks
        when all of them are complete, thus given tracks are empty.
     */
    List<String> validateTask(Tracks<TrainState> given, Tracks<TrainRequest> target, List<String> availableTracks) {
        List<String> result = new ArrayList<>();
        var givenCarIds = getAllCarIds(given);
        checkForDuplicates(givenCarIds, "given", result);
        var targetCarIds = getAllCarIds(target);
        checkForDuplicates(targetCarIds, "target", result);
        checkForMissingCars(givenCarIds, targetCarIds, "given", result);
        checkForMissingCars(targetCarIds, givenCarIds, "target", result);
        checkTracksExist(given, "given", availableTracks, result);
        checkTracksExist(target, "target", availableTracks, result);
        checkTrackAvailability(given, target, availableTracks, result);
        // TODO: also check for empty trains and carPackets (which should not exist)
        return result;
    }

    static <T extends Train> List<String> getAllCarIds(Tracks<T> t) {
        var result = new ArrayList<String>();
        for (var track : t.tracks().values()) {
            for (var train : track.trains()) {
                result.addAll(train.getAllCarIds());
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

    static <T extends Train> void checkTracksExist(Tracks<T> yard, String label, List<String> availableTracks, List<String> results) {
        var invalidTracks = yard.tracks().keySet();
        invalidTracks.removeAll(availableTracks);
        if (!invalidTracks.isEmpty()) {
            results.add(
                    "The " + label + " tracks " + String.join(", ", invalidTracks) + " don't exist in the infrastructure."
            );
        }
    }

    static void checkTrackAvailability(Tracks<TrainState> given, Tracks<TrainRequest> target, List<String> availableTracks, List<String> result) {
        var givenTracks = given.tracks().keySet();
        var targetPackets = target.tracks().values().stream()
                .map((t) -> t.trains().size())
                .reduce(0, Integer::sum);
        if (givenTracks.size() + targetPackets > availableTracks.size()) {
            result.add(givenTracks.size() + " tracks are occupied at beginning, and " +
                    targetPackets + " tracks are needed to form the target car packets, but only " +
                    availableTracks.size() + " tracks are available in the infrastructure.");
        }
    }
}
