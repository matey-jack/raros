package raros.plan;

import java.util.List;

public record Drop(
        String track,
        // TODO: make the list immutable
        List<String> cars,
        boolean couple
) implements ShuntingStep {
}
