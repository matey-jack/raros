package raros.plan;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {
    PlanSerde serde = new PlanSerde();
    Validator validator = new Validator();
    List<String> infraTracks = List.of("41 42 43 44 45".split(" "));

    @Test
    void testValidateTask() {
        var given = serde.<Tracks<TrainState>>read("src/test/resources/example-given-state.json");
        var target = serde.<Tracks<TrainRequest>>read("src/test/resources/example-target-state.json");
        var result = validator.validateTask(given, target, infraTracks);
        assertThat(result).isEmpty();
    }

    @Test
    void testValidatePlan() {
        var plan = serde.<ShuntingPlan>read("src/test/resources/example-shunting-plan.json");
        var result = validator.validatePlan(plan, infraTracks);
        assertThat(result).isEmpty();
    }
}