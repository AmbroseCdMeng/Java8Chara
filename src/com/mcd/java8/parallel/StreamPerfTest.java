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
        System.out.println("Sequential sum done in: " + measureSumPerf(ParallelTest::sequentialSum, 10_000_000) + " msesc");
        //底层 for
        System.out.println("Iterative sum done in: " + measureSumPerf((n)->{
            long result = 0;
            for (long i = 1L; i <= n; i++)
                result += i;
            return result;
        }, 10_000_000) + "msesc");
        //并行
        System.out.println("Parallel sum done in: " + measureSumPerf(ParallelTest::parallelSum, 10_000_000) + " msesc");
    }
}
