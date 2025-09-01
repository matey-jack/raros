package util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Maps {
    /**
     * Kotlin-style mapValues for Java.
     *
     * @param map       the input map
     * @param transform function to transform each value
     * @param <K>       key type
     * @param <V>       input value type
     * @param <R>       result value type
     * @return a new Map with the same keys and transformed values.
     */
    public static <K, V, R> Map<K, R> mapValues(
            Map<K, V> map,
            Function<? super V, ? extends R> transform
    ) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> transform.apply(e.getValue())
                ));
    }
}
