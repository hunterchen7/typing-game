package com.cs2212group9.typinggame.utils;

import java.security.NoSuchAlgorithmException;

// from https://github.com/szsascha/libgdx-multiplayer-authentication-flow
/**
 * Functional interface for a consumer that takes three arguments and returns nothing, with a possible exception
 * @param <T> - the first argument type
 * @param <U> - the second argument type
 * @param <V> - the third argument type
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    /**
     * Accepts the three arguments and returns nothing
     * @param t - the first argument
     * @param u - the second argument
     * @param v - the third argument
     * @throws NoSuchAlgorithmException - if an exception occurs
     */
    void accept(T t, U u, V v) throws NoSuchAlgorithmException;
}
