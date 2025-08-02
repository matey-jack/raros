package raros.plan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanSerdeTest {

    @Test
    void testReadState() {
        // Arrange
        var planner = new PlanSerde();
        String filePath = "src/test/resources/example-given-state.json";

        // Act
        var result = planner.readState(filePath).tracks();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should have 2 tracks");

        // Check track 45
        assertTrue(result.containsKey("45"), "Should contain track 45");
        TrackTrains track45 = result.get("45");
        assertNotNull(track45, "Track 45 should not be null");
        assertEquals(2, track45.trains().length, "Track 45 should have 2 trains");

        // Check track 44
        assertTrue(result.containsKey("44"), "Should contain track 44");
        TrackTrains track44 = result.get("44");
        assertNotNull(track44, "Track 44 should not be null");
        assertEquals(1, track44.trains().length, "Track 44 should have 1 train");
    }
}