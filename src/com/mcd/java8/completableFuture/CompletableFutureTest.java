package com.mcd.java8.completableFuture;

import org.junit.Test;

import java.sql.SQLOutput;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * CompletableFuture 构建异步应用
 */
public class CompletableFutureTest {
    @Test
    public void test() {
        //使用异步 API
        Shop shop = new Shop("BestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product");
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        //执行其他任务
        doSomethingElse();

        //在计算商品价格的同时
        try {
            double price = futurePrice.get();
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");
    }

    private void doSomethingElse() {
        System.out.println("doSomethingElse ...");
    }
}

class Shop {

    private String product;
    private String name;

    public Shop(String product) {
        this.product = product;
    }

    /**
     * 该方法会被阻塞。因为其需要等待同步事件完成而等待 1s
     *
     * @param product
     * @return
     */
    public double getPrice(String product) {
        return calculatePrice(product);
    }

    public Future<Double> getPriceAsync(String product) {
        //创建 CompletableFuture 对象，包含计算结果
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        //新线程异步执行计算
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                //需长时间计算的任务结束并得到结果时，设置 Future 的返回值
                futurePrice.complete(price);//如果计算正常，返回 Future 操作并设置商品价格
            } catch (Exception ex) {
                futurePrice.completeExceptionally(ex);//否则就抛出导致失败的异常，完成这次 Future 操作
            }

        }).start();
        //无需等待还没结束的计算，直接返回 Future 对象
        return futurePrice;
    }

    /**
     * 工厂方法创建 CompletableFuture 对象
     *
     * @param product
     * @return
     */
    public Future<Double> getPirceAsync2(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }


    private double calculatePrice(String product) {
        delay();
        return new Random().nextDouble() * product.charAt(0) + product.charAt(1);//根据名称随机生成一个价格
    }


    /**
     * 模拟 1s 延迟
     */
    public static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getName() {
        return name;
    }
}
