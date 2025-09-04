package raros.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public record ShuntingPlan(
        List<ShuntingStep> steps
) {
    @JsonIgnore
    public Collection<String> getUsedTracks() {
        return steps().stream().map(ShuntingStep::track).collect(Collectors.toSet());
    }
}
