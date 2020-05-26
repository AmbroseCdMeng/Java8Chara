package com.mcd.java8.design_patterns;

import org.junit.Test;

import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 使用 Lambda 重构面向对象的设计模式
 *      1、策略模式
 *      2、模板方法
 *      3、观察者模式
 *      4、责任链模式
 *    ☆ 5、工厂模式
 */
public class FactoryMode {
    @Test
    public void test(){
        /* 常规使用 */
        Product product = ProductFactory.createProduct("load");

        /* Lambda 表达式使用 */
        Supplier<Loan> loadSupplier = Loan::new;
        Loan loan = loadSupplier.get();

        /* Map 方式创建的工厂模式使用 */
    }
}

/*************************** 5、工厂模式 ***************************/

/**
 * 工厂模式，即无需向客户暴露实例化的逻辑就能完成对象的创建
 *
 * 通常，你会创建一个工厂类，它包含一个负责实现不同对象的方法
 */

class ProductFactory{
    public static Product createProduct(String name){
        switch (name){
            case "loan" : return new Loan();
            case "stock" : return new Stock();
            default:throw new RuntimeException("No such product " + name);
        }
    }
}

class Product{

}

class Loan extends Product{

}

class Stock extends Product{

}


/**
 * 也可以将上面的 switch 重构为 Map
 */
class ProductFactoryMap{
    final static Map<String, Supplier<Product>> MAP = new HashMap<>();
    static {
        MAP.put("loan", Loan::new);
        MAP.put("stock", Stock::new);
    }

    //利用 Map 来实例化不同的产品
    public static Product createProduct(String name){
        Supplier<Product> p = MAP.get(name);
        if (p != null)
            return p.get();
        throw new RuntimeException("No such product " + name);
    }
}
