package raros.plan;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {
    PlanSerde serde = new PlanSerde();
    List<String> infraTracks = List.of("41 42 43 44 45".split(" "));

    @Test
    void testValidateTask() {
        var given = serde.read("src/test/resources/simple/given.json", Tracks.class);
        var target = serde.read("src/test/resources/simple/target.json", ShuntingTask.class);
        var result = Validator.validateTask(given, target);
        assertThat(result).isEmpty();
    }

    @Test
    void testValidatePlan() {
        var plan = serde.read("src/test/resources/simple/shunting-plan.json", ShuntingPlan.class);
        var result = Validator.validatePlan(plan, infraTracks);
        assertThat(result).isEmpty();
    }
}