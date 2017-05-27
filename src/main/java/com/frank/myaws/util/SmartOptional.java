package com.frank.myaws.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author ftorriani
 */
public final class SmartOptional<T> {

    /**
     * Common instance for {@code empty()}.
     */
    private static final SmartOptional<?> EMPTY = new SmartOptional<>();

    private final T value;

    private SmartOptional() {
        this.value = null;
    }

    public static <T> SmartOptional<T> empty() {
        @SuppressWarnings("unchecked")
        SmartOptional<T> t = (SmartOptional<T>) EMPTY;
        return t;
    }

    private SmartOptional( T value ) {
        this.value = Objects.requireNonNull( value );
    }

    public static <T> SmartOptional<T> of( T value ) {
        return new SmartOptional<>( value );
    }

    public static <T> SmartOptional<T> ofNullable( T value ) {
        return value == null ? empty() : of( value );
    }

    public T get() {
        if ( value == null ) {
            throw new NoSuchElementException( "No value present" );
        }
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    public SmartOptional<T> ifPresent( Consumer<? super T> consumer ) {
        if ( isPresent() ) {
            consumer.accept( value );
            return this;
        }
        else {
            return empty();
        }
    }

    public SmartOptional<T> orElse( Runnable r ) {
        if ( isPresent() ) {
            return this;
        }
        else {
            r.run();
            return empty();
        }
    }

    public T orElseGet( Supplier<T> supplier ) {
        return supplier.get();
    }
}
