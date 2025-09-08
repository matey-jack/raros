package raros.plan;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlannerTest {
    final PlanSerde serde = new PlanSerde();

    TrackTrains track(Train... trains) {
        return new TrackTrains(Arrays.asList(trains));
    }

    Train train(int... ids) {
        return new Train(Arrays.stream(ids).mapToObj(Integer::toString).toList());
    }

    @Test
    void testRemovableCars() {
        var given = new Tracks(Map.of(
                "A", track(train(1, 2, 3, 4)),
                "B", track(train(11, 12, 13, 14, 15)),
                "C", track(train(21))
        ));
        var task = new ShuntingTask(5, Map.of(
                "A", track(train(11, 12, 21)),
                "B", track(train(1, 2, 3, 4, 5)),
                "C", track(train(13, 14, 15))
        ));
        var planner = new Planner(given, task);
        var cappa = planner.getCapacityPerTrack();
        assertThat(planner.removableCars("A", cappa)).isEqualTo(0);
        assertThat(planner.removableCars("B", cappa)).isEqualTo(4);
        assertThat(planner.removableCars("C", cappa)).isEqualTo(1);
    }

    @Test
    void testPlanAndValidate() {
        var folderPath = Paths.get("src/test/resources/simple/");
        var given = serde.read(folderPath.resolve("given.json"), Tracks.class);
        var target = serde.read(folderPath.resolve("target.json"), ShuntingTask.class);
        new Planner(given, target).createPlan();
    }

    void testFolder(String folderName) {
        var folderPath = Paths.get("src/test/resources/").resolve(folderName);
        var given = serde.read(folderPath.resolve("given.json"), Tracks.class);
        var target = serde.read(folderPath.resolve("target.json"), ShuntingTask.class);
//        var expectedPlan = serde.read(folderPath.resolve("shunting-plan.json"), ShuntingPlan.class);

        var actualPlan = new Planner(given, target).createPlan();
        serde.write(actualPlan, folderPath.resolve("generated-plan.json"));

        var result = Simulator.simulate(given, actualPlan);
        serde.write(result, folderPath.resolve("generated-result.json"));

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

    @Test
    void testHard() {
        testFolder("hard");
    }

    /**
     * This example shows that the first move is really stupid (in the old version, not any more),
     * but after that, the plan is puts at least one additional car on the target track for every Pick.
     */
    @Test
    void testDegenerateCase() throws IOException {
        var given = new Tracks(Map.of(
                "10", track(train(21, 31)),
                "20", track(train(10, 40)),
                "30", track(train(11, 50)),
                "40", track(train(20)),
                "50", track(train(30))
        ));
        var task = new ShuntingTask(
                2,
                Map.of(
                        "10", track(train(10, 11)),
                        "20", track(train(20, 21)),
                        "30", track(train(30, 31)),
                        "40", track(train(40)),
                        "50", track(train(50))
                ));
        var result = new Planner(given, task).planAndValidate();
        Path targetDir = Paths.get("src/test/resources/degenerate/");
        if (!Files.exists(targetDir)) {
            Files.createDirectory(targetDir);
        }
        serde.write(result.plan(), targetDir.resolve("generated-plan.json"));
        serde.write(result.resultingTracks(), targetDir.resolve("generated-result.json"));
    }

    @Test
    void testDeadLock() throws IOException {
        var given = new Tracks(Map.of(
                "10", track(train(2)),
                "20", track(train(1)),
                "30", track(train(4)),
                "40", track(train(3)),
                "50", track()
        ));
        var task = new ShuntingTask(
                1,
                Map.of(
                        "10", track(train(1)),
                        "20", track(train(2)),
                        "30", track(train(3)),
                        "40", track(train(4)),
                        "50", track()
                ));
        var result = new Planner(given, task).planAndValidate();
        Path targetDir = Paths.get("src/test/resources/deadlock");
        if (!Files.exists(targetDir)) {
            Files.createDirectory(targetDir);
        }
        serde.write(result.plan(), targetDir.resolve("generated-plan.json"));
        serde.write(result.resultingTracks(), targetDir.resolve("generated-result.json"));
    }
}