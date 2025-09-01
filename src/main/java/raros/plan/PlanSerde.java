package raros.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;

// Serializer/Deserializer = writing and reading Json from the in-program data structures.
public class PlanSerde {
    ObjectMapper objectMapper = new ObjectMapper();

    PlanSerde() {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

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

    public void write(ShuntingPlan plan, String pathName) {
        var payload = objectMapper.valueToTree(plan);
        var envelope = new Envelope(RarosDataType.SHUNTING_PLAN.value, "beta-1", payload);
        write(envelope, pathName);
    }

    public void write(Tracks state, String pathName) {
        var payload = objectMapper.valueToTree(state);
        var envelope = new Envelope(RarosDataType.YARD_TRACKS_CAR_ORDER_GIVEN.value, "beta-1", payload);
        write(envelope, pathName);
    }

    private void write(Envelope envelope, String pathName) {
        try {
            objectMapper.writeValue(new File(pathName), envelope);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
