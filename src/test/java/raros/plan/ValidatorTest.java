package raros.plan;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {
    private final PlanSerde serde = new PlanSerde();
    private final List<String> infraTracks = List.of("41 42 43 44 45".split(" "));
    private final Path basePath = Path.of("src/test/resources/simple/");

    @Test
    void testValidateTask() {
        var given = serde.read(basePath.resolve("given.json"), Tracks.class);
        var target = serde.read(basePath.resolve("target.json"), ShuntingTask.class);
        var result = Validator.validateTask(given, target);
        assertThat(result).isEmpty();
    }

    @Test
    void testValidatePlan() {
        var plan = serde.read(basePath.resolve("shunting-plan.json"), ShuntingPlan.class);
        var result = Validator.validatePlan(plan, infraTracks);
        assertThat(result).isEmpty();
    }
}