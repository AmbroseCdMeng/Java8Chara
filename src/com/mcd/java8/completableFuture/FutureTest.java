package com.mcd.java8.completableFuture;

import org.junit.Test;

import java.util.concurrent.*;

public class FutureTest {
    @Test
    public void test() {

    }

    /**
     * Future 接口在 Java 5 中被进入。
     * 设计初衷是对将来某个时刻会发生的结果进行建模。它建模了一种异步计算，返回一个执行运算结果的引用，当运算结束后，这个引用被返回给调用方
     */
    @Test
    public void futureTest() {
        ExecutorService executor = Executors.newCachedThreadPool();
        /* 向 ExecutorService 提交一个 Callable 对象 */
        Future<Double> future = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                /* 以异步方式在新的线程中执行耗时的操作 */
                return doSomeLongComputation();
            }
        });

        /* 异步操作进行的同时可以做其他的事情 */
        doSomethingElse();

        /* 获取异步操作的结果，如果最终被阻塞，无法得到结果，name在最多等待 1 SECONDS 后退出 */
        try {
            Double result = future.get(1, TimeUnit.SECONDS);
        }catch (ExecutionException e){

        }catch (InterruptedException e){

        }catch (TimeoutException e){

        }
    }

    private void doSomethingElse() {

    }

    private Double doSomeLongComputation() {
        return 0.0;
    }
}
