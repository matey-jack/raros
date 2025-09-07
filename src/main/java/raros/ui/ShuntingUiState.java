package raros.ui;

import raros.plan.ShuntingPlan;
import raros.plan.ShuntingStep;

public class ShuntingUiState {
    final ShuntingPlan plan;
    final int totalSteps;

    ShuntingState state = ShuntingState.SWITCHING_POINTS;
    ShuntingStep currentStep = null;
    int stepNumber = 0;

    public ShuntingUiState(ShuntingPlan plan) {
        this.plan = plan;
        this.currentStep = plan.steps().getFirst();
        this.totalSteps = plan.steps().size();
    }

    // Advance the state in the step, or go to the next step.
    // Returns 'true' on success and 'false' at the end of the plan.
    public boolean next() {
        switch (state) {
            case SWITCHING_POINTS -> {
                state = ShuntingState.DRIVING_IN;
            }
            case DRIVING_IN -> state = ShuntingState.COUPLING;
            case COUPLING -> state = ShuntingState.DRIVING_OUT;
            case DRIVING_OUT -> {
                stepNumber++;
                if (stepNumber >= totalSteps) {
                    return false;
                }
                currentStep = plan.steps().get(stepNumber);
                state = ShuntingState.SWITCHING_POINTS;
            }
        }
        return true;
    }

    public boolean done() {
        return stepNumber >= totalSteps;
    }

}


