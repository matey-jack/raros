package raros.plan;

import java.util.List;

public record Drop(
        String track,
        List<String> cars,
        boolean couple
) implements ShuntingStep {
}
