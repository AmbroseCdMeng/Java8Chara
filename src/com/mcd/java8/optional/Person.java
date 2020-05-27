package com.mcd.java8.optional;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * 人 -- 车 -- 保险
 */
public class Person {
    private Optional<Car> car;
    private OptionalInt age;

    public Optional<Car> getCar() {
        return car;
    }

    public OptionalInt getAge() {
        return age;
    }
}
