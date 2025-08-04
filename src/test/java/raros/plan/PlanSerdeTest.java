package raros.plan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanSerdeTest {
    PlanSerde serde = new PlanSerde();

    @Test
    void testReadGivenState() {
        String filePath = "src/test/resources/example-given-state.json";
        var result = serde.<Tracks<TrainState>>read(filePath).tracks();

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should have 2 tracks");

        // Check track 45
        assertTrue(result.containsKey("45"), "Should contain track 45");
        TrackTrains<TrainState> track45 = result.get("45");
        assertNotNull(track45, "Track 45 should not be null");
        assertEquals(2, track45.trains().size(), "Track 45 should have 2 trains");

        // Check track 44
        assertTrue(result.containsKey("44"), "Should contain track 44");
        TrackTrains<TrainState> track44 = result.get("44");
        assertNotNull(track44, "Track 44 should not be null");
        assertEquals(1, track44.trains().size(), "Track 44 should have 1 train");
    }

    @Test
    void testReadTargetState() {
        String filePath = "src/test/resources/example-target-state.json";
        var result = serde.<Tracks<TrainRequest>>read(filePath).tracks();
        assertTrue(result.containsKey("41"));
        assertTrue(result.containsKey("42"));
        // TODO: check correct reading of car packets
    }

    @Test
    void readPlan() {
        var result = (ShuntingPlan) serde.read("src/test/resources/example-shunting-plan.json");
        assertEquals(9, result.steps().size());
    }
}