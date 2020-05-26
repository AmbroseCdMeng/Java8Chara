package com.mcd.java8.default_function;

public interface Iterator<T> {
    boolean hasNext();
    T next();
    default void remove(){
        throw new UnsupportedOperationException();
    }
}
