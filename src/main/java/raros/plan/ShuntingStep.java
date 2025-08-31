package raros.plan;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Pick.class, name = "PICK"),
        @JsonSubTypes.Type(value = Drop.class, name = "DROP")
})
public interface ShuntingStep {
    String track();

    List<String> cars();
}
