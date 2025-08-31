package raros.plan;

import java.util.Map;

public record ShuntingTask(
        int maxWagonsPerTrack,
        Map<String, TrackTrains> targetTracks
) { }
