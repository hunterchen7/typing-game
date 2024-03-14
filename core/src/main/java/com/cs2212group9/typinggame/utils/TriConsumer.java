package com.cs2212group9.typinggame.utils;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

// from https://github.com/szsascha/libgdx-multiplayer-authentication-flow
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v) throws NoSuchAlgorithmException;

    /**
     * Returns a composed {@code TriConsumer} that performs, in sequence, this operation followed by the {@code after}
     * @param after - the operation to perform after this operation
     * @return - a composed {@code TriConsumer} that performs in sequence this operation followed by the {@code after}
     */
    default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}
