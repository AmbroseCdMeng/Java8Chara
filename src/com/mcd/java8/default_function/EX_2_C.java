package com.mcd.java8.default_function;

import org.junit.Test;

/**
 * 冲突情景
 *      C 实现 B 和 A
 *      A、B 都定义了 hello 的默认方法
 *      D 实现 A 并复写 hello
 *      B 继承 A
 *
 */
public class EX_2_C extends EX_2_D implements EX_2_A, EX_2_B{
    @Test
    public void printHello(){
        new EX_1_C().hello();
    }
}

interface EX_2_A{
    default void hello(){
        System.out.println("Hello from A ");
    }
}

interface EX_2_B extends EX_2_A{
    default void hello(){
        System.out.println("Hello from B ");
    }
}
abstract class EX_2_D implements EX_2_A{
    public void hello(){
        System.out.println("Hello from D ");
    }
}

