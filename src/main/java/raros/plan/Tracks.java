package raros.plan;

import java.util.Map;

public record Tracks(
        Map<String, TrackTrains> tracks
) { }
