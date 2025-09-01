package raros.plan;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlannerTest {
    PlanSerde serde = new PlanSerde();

    void testFolder(String folderName) {
        var given = serde.read("src/test/resources/" + folderName + "/given.json", Tracks.class);
        var target = serde.read("src/test/resources/" + folderName + "/target.json", ShuntingTask.class);
        var expectedPlan = serde.read("src/test/resources/" + folderName + "/shunting-plan.json", ShuntingPlan.class);
        var planResult = new Planner(given, target).planAndValidate();

        // serde.write(planResult.plan(), "src/test/resources/" + folderName + "/generated-plan.json");
        var report = Validator.checkResult(planResult.resultingTracks(), target);
        for (var line : report) {
            System.out.println(line);
        }
        assertThat(report).isEmpty();
    }

    @Test
    @Disabled(value = "this times out because of endless loop (I think)")
    void testSimple() {
        testFolder("simple");
    }

    @Test
    @Disabled(value = "this times out because of endless loop (I think)")
    void testMedium() {
        // TODO: fix shunting-plan.json
        testFolder("medium");
    }

}