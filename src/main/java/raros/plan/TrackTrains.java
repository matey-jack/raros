package raros.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record TrackTrains(
        List<Train> trains
) {
    public int numberOfCars() {
        return trains().stream().mapToInt(Train::size).sum();
    }

    public TrackTrains copy() {
        return new TrackTrains(
                trains().stream()
                        .map(Train::copy)
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    /***
     * Returns all cars from all trains.
     */
    @JsonIgnore
    public List<String> getAllCars() {
        return trains.stream()
                .flatMap(t -> t.carIds().stream())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /***
     * Returns all cars from all trains and removes them from the track.
     */
    public List<String> pickAll() {
        var result = getAllCars();
        trains().clear();
        return result;
    }

    /***
     * If `couple` adds `cars` to the last train, otherwise adds them as a new train.
     */
    public void addCars(List<String> cars, boolean couple) {
        if (!couple) {
            trains().add(new Train());
        }
        trains().getLast().carIds().addAll(cars);
    }
}
