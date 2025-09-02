package raros.plan;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PlannerTest {
    PlanSerde serde = new PlanSerde();

    TrackTrains track(Train ...trains) {
        return new TrackTrains(Arrays.asList(trains));
    }

    Train train(int ...ids) {
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
        var given = serde.read("src/test/resources/simple/given.json", Tracks.class);
        var target = serde.read("src/test/resources/simple/target.json", ShuntingTask.class);
        new Planner(given, target).createPlan();
    }

    void testFolder(String folderName) {
        var given = serde.read("src/test/resources/" + folderName + "/given.json", Tracks.class);
        var target = serde.read("src/test/resources/" + folderName + "/target.json", ShuntingTask.class);
//        var expectedPlan = serde.read("src/test/resources/" + folderName + "/shunting-plan.json", ShuntingPlan.class);

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
        testFolder("medium");
    }

    @Test
    void testHard() {
        testFolder("hard");
    }

}