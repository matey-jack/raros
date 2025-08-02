package raros.plan;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

// Serializer/Deserializer = writing and reading Json from the in-program data structures.
public class PlanSerde {
    ObjectMapper objectMapper = new ObjectMapper();

    Tracks readState(String filename) {
        try {
            var envelope = objectMapper.readValue(new File(filename), Envelope.class);
            return objectMapper.convertValue(envelope.payload(), Tracks.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
