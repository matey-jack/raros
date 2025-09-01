package raros.plan;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlannerTest {
    PlanSerde serde = new PlanSerde();

    void testFolder(String folderName) {
        var given = serde.read("src/test/resources/" + folderName + "/given.json", Tracks.class);
        var target = serde.read("src/test/resources/" + folderName + "/target.json", ShuntingTask.class);
        var expectedPlan = serde.read("src/test/resources/" + folderName + "/shunting-plan.json", ShuntingPlan.class);

        var actualPlan = new Planner(given, target).createPlan();
        serde.write(actualPlan, "src/test/resources/" + folderName + "/generated-plan.json");

        var result = Simulator.simulate(given, actualPlan);
        serde.write(result, "src/test/resources/" + folderName + "/generated-result.json");

        var report = Validator.checkResult(result, target);
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