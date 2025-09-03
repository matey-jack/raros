package raros.plan;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PlanSerdeTest {
    private PlanSerde serde = new PlanSerde();
    private Path basePath = Path.of("src/test/resources/simple/");

    @Test
    void testReadGivenState() {
        var given = serde.read(basePath.resolve("given.json"), Tracks.class).tracks();

        assertNotNull(given, "Result should not be null");
        assertEquals(5, given.size(), "Should have 5 tracks");

        // Check track 45
        assertTrue(given.containsKey("45"), "Should contain track 45");
        TrackTrains track45 = given.get("45");
        assertNotNull(track45, "Track 45 should not be null");
        assertEquals(2, track45.trains().size(), "Track 45 should have 2 trains");

        // Check track 44
        assertTrue(given.containsKey("44"), "Should contain track 44");
        TrackTrains track44 = given.get("44");
        assertNotNull(track44, "Track 44 should not be null");
        assertEquals(1, track44.trains().size(), "Track 44 should have 1 train");
    }

    @Test
    void testReadTargetState() {
        var target = serde.read(basePath.resolve("target.json"), ShuntingTask.class).targetTracks();
        assertTrue(target.containsKey("41"));
        assertTrue(target.containsKey("42"));
        // TODO: check correct reading of trains
    }

    @Test
    void readPlan() {
        var plan = serde.read(basePath.resolve("shunting-plan.json"), ShuntingPlan.class);
        assertThat(plan.steps().getFirst()).isInstanceOf(Pick.class);
        assertThat(plan.steps().get(1)).isInstanceOf(Drop.class);
        Drop coupledDrop = (Drop) plan.steps().get(5);
        assertThat(coupledDrop.couple()).isTrue();

        assertEquals(10, plan.steps().size());
    }
}