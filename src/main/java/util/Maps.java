package util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Maps {
    /**
     * Kotlin-style mapValues for Java.
     *
     * @return a new Map with the same keys and transformed values.
     */
    public static <K, V, R> Map<K, R> mapValues(
            Map<K, V> map,
            Function<? super Map.Entry<K, V>, ? extends R> transform
    ) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, transform));
    }

    /**
     * Kotlin-style mapValues for Java.
     *
     * @return a new Map with the same keys and transformed values.
     */
    public static <K, V, R> Map<K, R> mapValuesOnly(
            Map<K, V> map,
            Function<? super V, ? extends R> transform
    ) {
        return mapValues(map, e -> transform.apply(e.getValue()));
    }

    public static <K, V extends Comparable<V>> Optional<Map.Entry<K,V>> max(
            Map<K, V> map
    ) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue());
    }

    public static <T> Map<String, T> createMap(Collection<String> keys, Function<String, T> valueMapper) {
        return keys.stream().collect(Collectors.toMap(Function.identity(), valueMapper));
    }
}
