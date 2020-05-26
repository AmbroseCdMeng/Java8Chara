package com.mcd.java8.default_function;

import java.util.function.Supplier;

/**
 * 冲突情景
 *      C 实现 B 和 A
 *      A、B 都定义了 hello 的默认方法
 */
public class EX_3_C implements EX_3_B, EX_3_A{
    public void hello(){
        EX_3_B.super.hello();//显式调用
    }
}

interface EX_3_A{
    default void hello(){
        System.out.println("Hello from A ");
    }
}

interface EX_3_B{
    default void hello(){
        System.out.println("Hello from B ");
    }
}