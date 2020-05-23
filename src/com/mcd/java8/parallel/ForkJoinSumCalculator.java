package com.mcd.java8.parallel;

import org.junit.Test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.LongStream;

/**
 * 利用分支/合并框架执行并行求和
 */
public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> {

    private final long[] numbers;//要求和的数组
    private final int start;//子任务处理的数组的起始和终止位置
    private final int end;//子任务处理的数组的起始和终止位置

    public static final long THRESHOLD = 10_000;//不再将任务分解为子任务的数组大小

    /**
     * 公共构造函数用于创建主任务
     *
     * @param numbers
     */
    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    /**
     * 私有构造函数用于以递归方式为主任务创建子任务
     *
     * @param numbers
     * @param start
     * @param end
     */
    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    /**
     * 覆盖 RecursiveTask 的抽象方法
     *
     * @return
     */
    @Override
    protected Long compute() {
        int length = end - start;//负责求和的部分的大小
        if (length <= THRESHOLD)//如果大小小于等于阈值，顺序计算结果
            return computeSequentially();
        ForkJoinSumCalculator leftTask
                = new ForkJoinSumCalculator(numbers, start, start + length / 2);//创建一个子任务来为数组的另一半求和
        leftTask.fork();//利用另一个ForkJoinPool线程异步执行新创建的子任务

        ForkJoinSumCalculator rightTask
                = new ForkJoinSumCalculator(numbers, start + length / 2, end);//创建一个任务为数组的后一半求和
        Long rightResult = rightTask.compute();//同步执行第二个子任务，有可能允许进一步递归划分
        Long leftResult = leftTask.join();//读取第一个子任务的结果，如果尚未完成就等待

        return leftResult + rightResult;//该任务的结果就是两个子任务结果的组合
    }

    /**
     * 在子任务不再可分时计算结果的简单算法
     *
     * @return
     */
    private long computeSequentially() {
        long sum = 0;
        for (int i = 0; i < end; i++)
            sum += numbers[i];
        return sum;
    }
}

/**
 * 测试
 */
class ForkJoinSumCalculatorTest {
    @Test
    public long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        return new ForkJoinPool().invoke(task);
    }
}

/* 使用分支/并行框架的最佳做法 */
/**
 * 1、有必要在子任务的计算都开始后再调用 join 方法。
 *      对一个任务调用 join 方法会阻塞调用方，直至该任务得到结果
 *      如果调用 join 方法的时机不正确，可能会得到比原始顺序算法更慢更复杂的版本，因为每个子任务都必须等待另一个子任务完成才会启动
 *
 * 2、不应该在 RecursiveTask 内部使用 ForkJoinPool 的 invoke 方法。
 *      相反，应该始终直接调用 compute 和 fork 方法，只有顺序代码才应该使用 invoke 来启动并行计算
 *
 * 3、对子任务调用 fork 方法可以把它排进 ForkJoinPool。可以为其中一个子任务重用同一线程，从而避免在线程池中多分配一个任务造成的开销
 *      但这样做的效率要比直接对其中一个调用 computed 低
 *
 * 4、调试使用分支/合并框架的并行计算有点棘手。
 *      平常的程序可以通过栈跟踪来找问题，
 *      但是在分支/合并计算上因为调用 compute 的线程并不是概念上的调用方，后者是调用 fork 的那个，所以无法进行栈跟踪来找问题
 *
 * 5、和并行流一样。不能理所应当的认为在多核处理器上使用分支/并行框架就一定会比顺序计算快。
 *      一个任务分解成多个独立的子任务虽然可以让性能在并行化时有所提升，但是这个分解的过程同样需要消耗时间。
 *      此外，分支/合并框架需要“预热”（或者说需要执行几遍才会被JIT编译器优化）
 */