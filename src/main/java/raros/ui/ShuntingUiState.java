package raros.ui;

import raros.plan.ShuntingStep;

public record ShuntingUiState(
        ShuntingState state,
        ShuntingStep couplingStep,
        int stepNumber,
        int totalSteps
) {
}
