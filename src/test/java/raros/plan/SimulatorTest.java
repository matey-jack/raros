package raros.plan;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorTest {
    PlanSerde serde = new PlanSerde();

    void testFolder(String folderName) {
        var given = serde.read("src/test/resources/" + folderName + "/given.json", Tracks.class);
        var target = serde.read("src/test/resources/" + folderName + "/target.json", ShuntingTask.class);
        var plan = serde.read("src/test/resources/" + folderName + "/shunting-plan.json", ShuntingPlan.class);
        var result = Simulator.simulate(given, plan);

        var report = new Validator().checkResult(result, target);
        for (var line : report) {
            System.out.println(line);
        }
        assertThat(report).isEmpty();
    }

    @Test
    void testSimple() {
        testFolder("simple");
    }

    @Test
    void testMedium() {
        // TODO: fix shunting-plan.json
        testFolder("medium");
    }
}