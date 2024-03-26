package com.cs2212group9.typinggame.utils;

import java.security.NoSuchAlgorithmException;

// from https://github.com/szsascha/libgdx-multiplayer-authentication-flow
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v) throws NoSuchAlgorithmException;
}
