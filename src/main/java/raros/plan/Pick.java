package raros.plan;

import java.util.List;

public record Pick(
        String track,
        List<String> cars
) implements ShuntingStep {
}
