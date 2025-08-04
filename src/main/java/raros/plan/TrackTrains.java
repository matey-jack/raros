package raros.plan;

import java.util.List;

public record TrackTrains<T extends Train>(
        List<T> trains
) { }
