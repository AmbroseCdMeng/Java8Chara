package com.mcd.java8.design_patterns;

import java.util.function.Consumer;

/**
 * 使用 Lambda 重构面向对象的设计模式
 * 1、策略模式
 * ☆ 2、模板方法
 * 3、观察者模式
 * 4、责任链模式
 * 5、工厂模式
 */
public class TemplateFunction {
    public void test() {
        //基本使用
        new OnLineBanking() {
            @Override
            void makeCustomerHappy(Customer c) {
                System.out.println("Hello " + c.getName());
            }
        }.processCustomer(1111);

        // Lambda 表达式
        new OnLineBankingLambda().processCustomer(1024, c-> System.out.println("Hello " + c.getName()));
    }
}


/*************************** 2、模板方法 ***************************/

/**
 * 如果需要采用某个算法的框架，同时又希望有一定的灵活度，能对它的某些部分进行改进，
 * 那么采用模板方法设计模式是比较通用的方案。
 * <p>
 * 例：银行应用。
 * 用户输入账号，从数据库中获取信息，完成让用户满意的操作。
 * 但是，不同分行的功能不同，让用户满意的操作方式不同。
 */

abstract class OnLineBanking {
    public void processCustomer(int id) {
        Customer c = new DataBase().getCustomerWithId(id);
        makeCustomerHappy(c);
    }

    abstract void makeCustomerHappy(Customer c);
}

class OnLineBankingLambda{
    /* 使用 Lambda 表达式 */
    //我们向 processCustomer 方法引入第二个参数，Consumer<Customer> 类型的参数，与前文定义的 makeCustomerHappy 的特征保持一致
    public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
        Customer customer = new DataBase().getCustomerWithId(id);
        makeCustomerHappy.accept(customer);
    }
    //现在，就可以通过传递 Lambda 表达式直接插入不同的行为而不需要集成 OnlineBanking 类了
}

class Customer {
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public String getName(){
        return name;
    }
}

class DataBase {
    public Customer getCustomerWithId(int id) {
        return new Customer();
    }
}