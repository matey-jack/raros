package raros.plan;

import java.util.List;
import java.util.stream.Collectors;

public record TrainRequest(
        // The order of carPackets is part of the request, but the order of cars within each package does not matter.
        List<List<String>> carPackets
) implements Train {

    @Override
    public List<String> getAllCarIds() {
        return carPackets.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
