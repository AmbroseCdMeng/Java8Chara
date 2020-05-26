package com.mcd.java8.default_function;

public interface Sized {
    int size();
    //默认方法
    default boolean isEmpty(){
        return size() == 0;
    }
}
