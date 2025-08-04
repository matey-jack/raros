package raros.plan;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {
    PlanSerde serde = new PlanSerde();
    Validator validator = new Validator();

    @Test
    void testAllGood() {
        var given = serde.<Tracks<TrainState>>read("src/test/resources/example-given-state.json");
        var target = serde.<Tracks<TrainRequest>>read("src/test/resources/example-target-state.json");
        var infraTracks = List.of("41 42 43 44 45".split(" "));
        var result = validator.validateTask(given, target, infraTracks);
        assertThat(result).isEmpty();
    }

}