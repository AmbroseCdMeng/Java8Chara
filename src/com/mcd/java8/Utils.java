package com.mcd.java8;

import java.util.function.Function;

public class Utils {

    /**
     * 测量函数性能
     *  对传递的函数执行 10 次，返回最快一次的执行时间。单位： ms
     * @param adder
     * @param n
     * @return
     */
    public static long measureSumPerf(Function<Long, Long> adder, long n){
        long fastest = Long.MAX_VALUE;
        for (int i = 0; i < 10; i  ++){
            long start = System.nanoTime();
            long sum = adder.apply(n);
            long duration = (System.nanoTime() - start) / 1_000_000;
            System.out.println("Result:  " + sum);
            if (duration < fastest) fastest = duration;
        }
        return fastest;
    }
}
