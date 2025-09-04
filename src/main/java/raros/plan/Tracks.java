package raros.plan;

import java.util.Map;

public record Tracks(
        // TODO: make the map immutable
        Map<String, TrackTrains> tracks
) { }
