package raros.plan;

import java.util.List;

public record Pick(
        String track,
        // TODO: make the list immutable
        List<String> cars
) implements ShuntingStep {
}
