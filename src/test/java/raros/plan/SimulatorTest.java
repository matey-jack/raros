package raros.plan;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorTest {
    PlanSerde serde = new PlanSerde();

    @Test
    void test() {
        var given = serde.<Tracks<TrainState>>read("src/test/resources/example-given-state.json");
        var target = serde.<Tracks<TrainRequest>>read("src/test/resources/example-target-state.json");
        var plan = serde.<ShuntingPlan>read("src/test/resources/example-shunting-plan.json");
        var result = Simulator.simulate(given, plan);

        var report = new Validator().checkResult(result, target);
        for (var line : report) {
            System.out.println(line);
        }
        assertThat(report).isEmpty();
    }
}