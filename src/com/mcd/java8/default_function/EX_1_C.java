package com.mcd.java8.default_function;

import org.junit.Test;

/**
 * 冲突情景
 *      C 实现 B 和 A
 *      A、B 都定义了 hello 的默认方法
 *      B 继承 A
 *
 */
public class EX_1_C implements EX_1_A, EX_1_B{
    @Test
    public void printHello(){
        new EX_1_C().hello();
    }
}

interface EX_1_A{
    default void hello(){
        System.out.println("Hello from A ");
    }
}

interface EX_1_B extends EX_1_A{
    default void hello(){
        System.out.println("Hello from B ");
    }
}

