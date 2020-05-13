package com.mcd.java8.parallel;

import java.util.stream.Stream;

/**
 * 并行数据处理与性能
 */
public class ParallelTest {
    /**
     * 接收数字 n 作为参数，返回 1 到 n 的所有数字和。
     *
     * @param n
     * @return
     */
    public static long sequentialSum(long n) {
        return Stream.iterate(1L, i -> i + 1)   //生成自然数无线流
                .limit(n)                             //限制流
                .reduce(0L, Long::sum);             //对所有数字求和来归纳流
    }

    public static long parallelSum(long n) {
        return Stream.iterate(1L, i -> i + 1)   //
                .limit(n)
                .parallel()                           //顺序流转换为并行流
                .reduce(1L, Long::sum);

        /* 不要以为可以通过 parallel 和 sequential 方法可以细化的控制遍历流时的操作。 最后一个 parallel 或 sequential 将会影响整个流水线 */

        /*
         * 并行流内部使用了默认的 ForkJoinPool， 它的线程数量就是处理器数量，该值由 Runtime.getRuntime().availableProcessors() 获取的
         * 可以通过系统属性 java.util.concurrent.ForkJoinPool.common.parallelism 来改变线程池大小
         * 如下：
         * System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "12")
         *
         * 但是，这是一个全局设置，它将影响代码中所有并行流。
         *
         * 换句话说，目前，还没有办法专门为某个并行流执行这个值。
         */
    }
}
