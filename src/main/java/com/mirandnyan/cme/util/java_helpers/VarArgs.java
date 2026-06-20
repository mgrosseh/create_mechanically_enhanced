package com.mirandnyan.cme.util.java_helpers;

import java.util.Arrays;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class VarArgs<T> { // fucking java
    Stream<T> values;
    IntFunction<T[]> generator = null;

    private static <T> IntFunction<T[]> autogenerator(T[] values) {
        return size -> Arrays.copyOf(Arrays.copyOf(values, 0), size);
    }
    @SafeVarargs
    public VarArgs(T... values) {
        this(autogenerator(values), values);
        this.values =  Stream.of(values);
    }
    @SafeVarargs
    public VarArgs(IntFunction<T[]> generator, T... values) {
        this.generator = generator;
        this.values =  Stream.of(values);
    }

    @SafeVarargs
    public static <T> VarArgs<T> of(T... values) {
        return new VarArgs<>(values);
    }

    @SafeVarargs
    public final VarArgs<T> and(T... values) {
        this.values = Stream.concat(this.values, Stream.of(values));
        return this;
    }

    public Stream<T> toStream() {
        return values;
    }
    public T[] toArray() {
        return values.toArray(generator);
    }
}
