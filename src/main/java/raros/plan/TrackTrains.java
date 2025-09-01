package raros.plan;

import java.util.ArrayList;
import java.util.List;

public record TrackTrains(
        List<Train> trains
) {
    public int size() {
        return trains().stream().mapToInt(Train::size).sum();
    }

    public TrackTrains copy() {
        return new TrackTrains(trains().stream().map(Train::copy).toList());
    }

    public List<String> pickAll() {
        var result = new ArrayList<String>();
        for (var train : trains()) {
            result.addAll(train.carIds());
        }
        return result;
    }
}
