package raros.plan;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

// Serializer/Deserializer = writing and reading Json from the in-program data structures.
public class PlanSerde {
    ObjectMapper objectMapper = new ObjectMapper();

    <T> T read(String filename, Class<T> dataClass) {
        try {
            var envelope = objectMapper.readValue(new File(filename), Envelope.class);
            Class foundClass = RarosDataType.getClassFor(envelope.datatype());
            if (foundClass == null) {
                throw new RuntimeException(
                        "Data type '" + envelope.datatype() + "' in file is not recognized."
                );
            }
            if (foundClass != dataClass) {
                throw new RuntimeException(
                        "Data type '" + envelope.datatype() + "' in file " +
                                "is for class '" + foundClass.getName() + "' " +
                                "which does not match requested " + dataClass.getName()
                );
            }
            return objectMapper.convertValue(envelope.payload(), dataClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
