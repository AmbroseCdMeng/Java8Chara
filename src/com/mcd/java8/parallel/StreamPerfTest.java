package com.mcd.java8.parallel;

import org.junit.Test;

import static com.mcd.java8.Utils.measureSumPerf;

/**
 * 流性能测试
 */
public class StreamPerfTest {

    @Test
    public void streamPerf() {
        //顺序
        System.out.println("Sequential sum done in: " + measureSumPerf(ParallelTest::sequentialSum, 10_000_000) + " msecs");
        //底层 for
        System.out.println("Iterative sum done in: " + measureSumPerf(ParallelTest::iterativeSum, 10_000_000) + "msecs");
        //并行
        System.out.println("Parallel sum done in: " + measureSumPerf(ParallelTest::parallelSum, 10_000_000) + " msecs");

        /**
         * 运行时间：
         *  并行 > 顺序 > for
         *
         *  并行版本比顺序版本的速度要慢很多， 是因为：
         *      1、iterate 生成的时装箱对象，拆箱成数字才能求和，拆箱装箱浪费了很多的时间
         *      2、iterate 很难被分成多个独立块来并行执行
         *          我们必须意识到哪些流操作更容易并行化，之所以说上面 iterate 很难分割成独立执行的块，是因为每次应用这个函数都要依赖前一次应用的结果，
         *          整张数字列表在归纳的过程开始时还没有准备好，因而无法有效的把流划分成小块来并行处理。
         *          所以，像这种流，标记成并行，反而给顺序处理增加了开销，还要把每次求和的操作分到一个不同的线程上
         */

        /** 接下来使用 LongStream.rangeClosed 方法生成数测试。
         *      该方法与 iterate 相比有两个优点：
         *          1、生成原始 long 类型数字，没有拆箱装箱操作
         *          2、生成数字范围，容易拆分成独立小块。如（1~5, 6~10, 等）
         *  */
        System.out.println("Range sum done in: " + measureSumPerf(ParallelTest::rangedSum, 10_000_000) + " msecs");
        System.out.println("Parallel range sum done in: " + measureSumPerf(ParallelTest::parallelRangedSum, 10_000_000) + " msecs");

        /**
         * 并行化并不是没有代价的，它本身是要对流做递归划分，把每个子流的归纳操作分配到不同的线程，然后把这些操作的记过合并成一个值。
         * 但是在多核之间移动数据的代价是相当大的。
         *
         * 所以，一定要保证在内核中并行执行工作的时间比在内核之间传输数据的时间长。
         *
         * 总而言之，很多情况下不可能或不方便进行并行化。
         *
         * 如果用错了，即便算的快，也会变得毫无意义，因为得到的结果可能是错误的。
         *
         * 接下来，看一个常见得陷阱：
         */

        System.out.println("SideEffect sum done in: " + measureSumPerf(ParallelTest::sideEffectSum, 10_000_000) + "msecs");
        // You can see 一堆乱七八糟的数据 ！！！~
        System.out.println("SideEffect parallel sum done in: " + measureSumPerf(ParallelTest::parallelSideEffectSum, 10_000_000) + "msecs");
        /**
         * 这个方法的性能好坏已经无关紧要了，因为它每次返回的结果都是错误的！！！
         *
         * 这是因为，多个线程在同时访问累加器，执行 total += value， 而且一句并非原子操作，问题的根源在于，forEach 中调用的方法有副作用，它会改变多个线程共享的对象的可变状态。
         */

        /* 如何高效使用并行流 */
        /**
         *  1、适当的基准检测其性能。并行流转换简单，但却不一定总比顺序流快，需要实际检测其性能
         *  2、留意装箱。自动拆装箱会大大降低性能。Java8 中可以使用 （IntStream、DoubleStream、LongStream）等原始流避免自动装箱
         *  3、留意顺序依赖。有些流（如：limit、findFirst）等依赖顺序的操作，本身在并行流上的性能就比顺序流差，它们在并行流上的代价是非常大的
         *  4、考虑操作流水线总成本。一般单个流水线的处理成本越高，并行流有优势的可能性更大一点
         *  5、考虑数据量。对于较小的数据量，很可能并行节约的时间远远抵不上并行化的开销
         *  6、考虑数据结构是否易分解。（如：ArrayList 拆分效率远高于 LinkedList， range 工厂创建的原始类型流更易分解，等）
         *  7、考虑流自身的特点及流水线中间操作修改流的方式。这些都可能改变分解过程的性能。（如 SIZED 流可以分成大小相等的两部分，但筛选操作可能丢弃的元素无法预测，导致流的本身大小未知）
         *  8、考虑终端合并代价。（如：Collector.combiner 方法）合并代价太大，就会导致组合每个子流的结果时付出的代价超过通过并行化获得到的性能提升。
         */
    }
}
