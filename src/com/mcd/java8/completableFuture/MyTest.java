package com.mcd.java8.completableFuture;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.Collectors;

public class MyTest {

    private List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("ButItAll"),
            new Shop("1ButItAll"));


    /**
     * 创建一个线程池
     */
    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 400), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true); //使用守护线程 —— 这种方式不会阻止程序的关停
            return thread;
        }
    });

    {
        // https://blog.csdn.net/lijw_csdn/article/details/80528636
//        shops = new ArrayList(shops);
//        for (int i = 0; i < 46; i++) {
//            shops.add(new Shop("Test" + i));
//        }
    }

    public void test1() {

    }

    /**
     * 验证 findPrices 的正确性和执行性能
     */
    @Test
    public void validatePrices() {

        long start = System.nanoTime();
        System.out.println(findPrices("myPhone27S"));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs");//4004
        /* 这说明，对这四条数据的查询时顺序进行的，并且一个查询会阻碍另一个查询，每一个操作大概消费 1s */

        start = System.nanoTime();
        System.out.println(findPricesParallel("myPhone27S"));
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Parallel Done in " + duration + " msecs");//1004
        /* 很明显，时间缩短到了 1s，这是因为对四条数据的查询实现了并行 */

//        start = System.nanoTime();
//        System.out.println(findPricesAsyncError("myPhone27S"));
//        duration = (System.nanoTime() - start) / 1_000_000;
//        System.out.println("Async Done in " + duration + " msecs");//2
        /* Oh My God !~~ 2s ??? 看上去很快，实际上，没有任何的意义，因为没有等待异步的结果返回， 虽然方法执行结束了，但是并没有得到结果 */

        start = System.nanoTime();
        System.out.println(findPricesAsync("myPhone27S"));
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Async Done in " + duration + " msecs");//2001
        /* 这才是正确的做法。但是 2s ? 虽然比顺序执行快，但是耗时依然是并行的 2 倍。这个结果难免有点让人失望~~ */

        /**
         * Here 分析一波：
         *
         * 异步的执行时间是并行的 2 倍，是为什么呢？是否就说明 CompletableFuture 的效率不如并行呢？
         *
         * 必须 NO
         *
         * 首先，为什么并行执行如此的快，这与我们电脑本身的配置相关。（办公室电脑为 4核4线程）
         *
         *  如果电脑是 4 线程，那么，并行执行 4 个任务，几乎可以为每一个查询分配一个线程，每一个查询需要 1s，所以总共耗时也是 1s
         *  但是如果此时有 5-8 条数据，那么最后需要的时间就会为 2s 左右，因为第 5 个查询只能等待前面的某一个查询执行完毕释放出空闲线程才能继续
         *
         * 我们继续记录测试数据
         *      数据条数        Parallel        CompletableFuture
         *       4              1004            2001
         *       5              2004            3999
         *       6              2003            4005
         *       7              2003            3001
         *       8              2003            3001
         *       9              3003            3001
         *       ...            ...             ...
         *       13             4003            5001
         *       ...            ...             ...
         *       15             5003            5001
         *       ...            ...             ...
         *       50             15009           17002
         *       ...            ...             ...
         *       94             24004           32001
         *
         *  看上去二者的速度伯仲之间，整体 CompletableFuture 还要略逊一些。
         *  因为二者采用同样的线程池，默认的都是固定数目的线程，具体数量取决于 RunTime.getRuntime().availableProcessors() 的值
         *
         *  那么它的优势在哪里呢？
         *  CompletableFuture 允许对执行器进行配置，尤其线程池大小，让其更适应应用需求的方法进行配置，满足程序的需求，这是并行 API 无法提供的
         *
         * 该如何选择合适的线程数目呢？
         *      线程数目过少，会造成处理器的内核无法充分被利用，造成资源浪费
         *      线程数目过多，会造成竞争稀缺处理器和内存资源，浪费大量时间在上下文切换上
         *
         *      实际使用中，比如有 400 条数据
         *      如果线程池的线程大于 400，则会造成明显的浪费
         *      如果线城池的线程小于 400，则不会有什么致命影响，但有可能会让资源得不到充分利用
         *
         *      所以，建议设置，但是同时要考虑到服务器的负荷，所以也要设置一个上限，比如，数据量 400 以内，则线程数与数据数相同，400 以上，则最多包含 100 个线程
         *
         */


        /* 在下面创建了一个自定义线程数的线程池 */
        start = System.nanoTime();
        System.out.println(findPricesAsyncWithExecutor("myPhone27S"));
        duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Async With Executor Done in " + duration + " msecs");//1003
        /* 这个就腻害了，只要数据量少于阈值，一直都会保持最多线程以最快的速度处理 */
    }


    /**
     * 顺序查询所有商店的方式实现 findPrices 方法
     *
     * @param product
     * @return
     */
    public List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    /**
     * 对 findPrices 进行并行操作
     *
     * @param product
     * @return
     */
    public List<String> findPricesParallel(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(Collectors.toList());
    }

    /**
     * 这种写法是 不正确的 ！！！
     * 使用 CompletableFuture 发起异步请求
     * 工厂方法 supplyAsync 创建 CompletableFuture 对象
     *
     * @param product
     * @return
     */
    public List<CompletableFuture<String>> findPricesAsyncError(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
                .collect(Collectors.toList());
    }

    /**
     * 使用 CompletableFuture 发起异步请求
     * 工厂方法 supplyAsync 创建 CompletableFuture 对象
     *
     * @param product
     * @return
     */
    public List<String> findPricesAsync(String product) {
        /* 使用 CompletableFuture 以异步方式计算商品价格 */
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
                .collect(Collectors.toList());
        /* 等待所有异步操作结束后返回结果 */
        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * 使用 CompletableFuture 发起异步请求 （自定义数量线程池）
     * @param product
     * @return
     */
    public List<String> findPricesAsyncWithExecutor(String product) {
        /* 使用 CompletableFuture 以异步方式计算商品价格 */
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)), executor))
                .collect(Collectors.toList());
        /* 等待所有异步操作结束后返回结果 */
        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
