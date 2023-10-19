package org.thelidia.demo.utils;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Utils {
   static <T> void ifNonNull(T nullableValue, Consumer<T> consumer) {
        Optional.ofNullable(nullableValue)
                .ifPresent(value -> consumer.accept(value));
    }
   static <T,R> R mapIfNonNull(T nullableValue, Function<T,R> mapper) {
        return Optional.ofNullable(nullableValue)
                .map(value -> mapper.apply(value))
                .orElse(null);
    }
}
