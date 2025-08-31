package raros.plan;

import java.util.List;

public record TrackTrains(
        List<Train> trains
) {
    public int size() {
        return trains().stream().mapToInt(Train::size).sum();
    }
}
