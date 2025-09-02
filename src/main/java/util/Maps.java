package util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
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

    public static <K, V> Map<K, V> filterValues(
            Map<K, V> map,
            Predicate<V> condition
    ) {
        return map.entrySet().stream()
                .filter(e -> condition.test(e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    public static <K, V extends Comparable<V>> Optional<K> max(
            Map<K, V> map
    ) {
        return map.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey);
    }

    public static <T> Map<String, T> createMap(Collection<String> keys, Function<String, T> valueMapper) {
        return keys.stream().collect(Collectors.toMap(Function.identity(), valueMapper));
    }
}
