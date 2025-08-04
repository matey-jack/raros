package raros.plan;

import java.util.List;

public record ShuntingPlan(
        List<ShuntingStep> steps
) { }
