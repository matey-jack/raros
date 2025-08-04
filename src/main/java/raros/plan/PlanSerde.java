package raros.plan;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

// Serializer/Deserializer = writing and reading Json from the in-program data structures.
public class PlanSerde {
    ObjectMapper objectMapper = new ObjectMapper();

    <T> T read(String filename) {
        try {
            var envelope = objectMapper.readValue(new File(filename), Envelope.class);
            TypeReference typeRef = RarosDataType.getClassFor(envelope.datatype());
            if (typeRef == null) {
                throw new RuntimeException(
                        "Data type '" + envelope.datatype() + "' in file is not recognized."
                );
            }
            // TODO: fix this, if possible
            // maybe we need to change the code to use "JavaType" instead
            // like JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Tracks.class, TrainState.class);
//            if (foundClass != dataClass) {
//                throw new RuntimeException(
//                        "Data type '" + envelope.datatype() + "' in file " +
//                                "is for class '" + foundClass.getName() + "' " +
//                                "which does not match requested " + dataClass.getName()
//                );
//            }
            return (T) objectMapper.convertValue(envelope.payload(), typeRef);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
