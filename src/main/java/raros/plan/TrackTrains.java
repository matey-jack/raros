package raros.plan;

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

    public List<String> pickAll() {
        var result = new ArrayList<String>();
        for (var train : trains()) {
            result.addAll(train.carIds());
        }
        trains().clear();
        return result;
    }

    public void addCars(List<String> cars, boolean couple) {
        if (!couple) {
            trains().add(new Train());
        }
        trains().getLast().carIds().addAll(cars);
    }
}
