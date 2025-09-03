package raros.plan;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class SimulatorTest {
    PlanSerde serde = new PlanSerde();

    void testFolder(String folderName) {
        var folderPath = Paths.get("src/test/resources/").resolve(folderName);
        var given = serde.read(folderPath.resolve("given.json"), Tracks.class);
        var target = serde.read(folderPath.resolve("target.json"), ShuntingTask.class);
        var plan = serde.read(folderPath.resolve("shunting-plan.json"), ShuntingPlan.class);
        var result = Simulator.simulate(given, plan);

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
        testFolder("medium");
    }
}