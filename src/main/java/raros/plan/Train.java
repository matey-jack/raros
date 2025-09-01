package raros.plan;

import java.util.ArrayList;
import java.util.List;

public record Train(
        // In a shunting request (description of desired result), the order of cars is insignificant.
        // It's basically treated as a set.
        // But in a description of the actual state (before or after the shunting is done), the order matches reality.
        List<String> carIds
) {
    public Train() {
        this(new ArrayList<>());
    }

    public int size() {
        return carIds.size();
    }

    public Train copy() {
        return new Train(new ArrayList<>(carIds));
    }
}
