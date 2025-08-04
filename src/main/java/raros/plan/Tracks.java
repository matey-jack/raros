package raros.plan;

import java.util.Map;

public record Tracks<T extends Train>(
        Map<String, TrackTrains<T>> tracks
) { }
