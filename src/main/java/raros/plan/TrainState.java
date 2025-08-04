package raros.plan;

import java.util.ArrayList;
import java.util.List;

public record TrainState(
    List<String> carIds
) implements Train {

    @Override
    public List<String> getAllCarIds() {
        return new ArrayList<>(carIds);
    }
}
