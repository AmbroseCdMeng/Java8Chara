package com.mcd.java8.lambda;

import java.util.function.DoubleFunction;

/**
 * Lambda - 数学中的思想
 */
public class MathAndLambda {
    /**
     * 积分
     *
     * 20 ↑                        ／
     *    │                      ／
     * 18 │                    ／    f(x) = x + 10
     *    │                  ／│
     * 16 │                ／**│
     *    │              ／****│
     * 15 │            ／******│
     *    │          ／********│
     * 14 │        ／**********│
     *    │      ／************│
     * 12 │    ／ │************│             ∫7
     *    │  ／   │************│  ←--------- |   f(x) dx
     * 10 │／     │************│             ∫3
     *    │       │************│
     *  8 │       │************│
     *    │       │************│
     *  6 │       │************│
     *    │       │************│
     *  4 │       │************│
     *    │       │************│
     *  2 │       │************│
     *    │       │************│
     *  0 ┼───────│────────────│───────────────────────────────────→
     *         2      4      6     8     10     12     14
     *
     *  该例子中， f(x) 是条直线，因此很容易短处阴影部分面积 60
     *
     *  在 Java 中，如果想表达上述函数，就需要一个方法。
     *      比如：需要一个方法 integrate， 接收 3 个参数： 函数 f ，上限， 下限。
     *      如此使用： integrate(f, 3.0, 7.0)
     *
     *  需要注意：
     *      不能简单的传递为 integrate(x + 10, 3, 7)
     *      原因：
     *          1、x 的作用域不明确
     *          2、x + 10 的类型异议。这会将 x + 10 的结果值传递，而不是 f(x) = x + 10 这个方法
     *
     *  结合 Java 8 的 Lambda 表达式：
     *      我们需要的方法有可能如下传递：
     *          1、 integrate((double x) -> x + 10, 3.0, 7.0)
     *          2、 integrate((double x) -> f(x), 3.0, 7.0)
     *          3、 integrate(C :: f, 3.0, 7.0)
     *
     *  接下来考虑如何实现 integrate 本身：
     *
     *
     */

    //数学公式
    //public  double integrate((double -> double) f, double a, double b){
    //    return (f(a) + f(b)) * (b - a) / 2.0;
    //}

    //
    public double integrate(DoubleFunction<Double> function, double a, double b) {
        return (function.apply(a) + function.apply(b)) * (b - a) / 2.0;
    }
}
