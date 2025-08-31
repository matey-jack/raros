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
        var given = serde.read("src/test/resources/example-given-state.json", Tracks.class);
        var target = serde.read("src/test/resources/example-target-state.json", ShuntingTask.class);
        var result = validator.validateTask(given, target, infraTracks);
        assertThat(result).isEmpty();
    }

    @Test
    void testValidatePlan() {
        var plan = serde.read("src/test/resources/example-shunting-plan.json", ShuntingPlan.class);
        var result = validator.validatePlan(plan, infraTracks);
        assertThat(result).isEmpty();
    }
}