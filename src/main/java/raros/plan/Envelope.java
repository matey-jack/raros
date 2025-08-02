package raros.plan;

import com.fasterxml.jackson.databind.JsonNode;

public record Envelope(
        String datatype,
        String version,
        JsonNode payload
) { }
