package com.mcd.java8.design_patterns;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Lambda 重构面向对象的设计模式
 *      1、策略模式
 *      2、模板方法
 *    ☆ 3、观察者模式
 *      4、责任链模式
 *      5、工厂模式
 */
public class ObserverMode {

    public void test(){
        /* 基本调用 */
        Feed feed = new Feed();
        feed.registerObserver(new NYTimes());
        feed.registerObserver(new Guardian());
        feed.registerObserver(new LeMonde());

        feed.notifyObservers("The queen said her favourite book is Java 8 in Action ! ");

        /* 使用 Lambda 表达式 */
        feed.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("queen")) System.out.println("Yet another news in London ... " + tweet);
        });
    }
}

/*************************** 3、观察者模式 ***************************/

/**
 * 观察者模式是一种比较常见的方案。
 *
 * 某些事件发生时，如果一个对象（通常称之为主题）需要自动通知其他多个对象（称之为观察者），就会采用该方案。
 *
 * 例：设计并实现一个定制化通知系统。
 *      几家报纸机构，都订阅了新闻，希望接收的新闻中包含某些关键字时，能够得到特别通知。
 *
 *      1、首先，需要一个观察者接口
 *      2、其次，需要声明不同的观察者
 *      3、接下来，定义包含注册观察者、通知观察者两个方法的 Subject 接口
 *      4、最后，创建一个类实现 Subject 接口，在内部维护观察者列表
 */

/**
 * 声明观察者接口
 * */
interface  Observer{
    void notify(String tweet);
}

/**
 * 声明三个观察者
 */
class NYTimes implements Observer{
    @Override
    public void notify(String tweet){
        if (tweet!= null && tweet.contains("money"))
            System.out.println("Breaking news in NY! " + tweet);
    }
}

class Guardian implements Observer{
    @Override
    public void notify(String tweet){
        if (tweet != null && tweet.contains("queen"))
            System.out.println("Yet another news in London... " + tweet);
    }
}

class LeMonde implements Observer{
    @Override
    public void notify(String tweet) {
        if (tweet != null && tweet.contains("wine"))
            System.out.println("Today cheese, wine and news! " + tweet);
    }
}

/**
 * Subject 接口
 */
interface  Subject{
    void registerObserver(Observer o);
    void notifyObservers(String tweet);
}

/**
 * Feed 类实现 Subject 接口，在内维护观察者列表
 */
class Feed implements Subject{
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyObservers(String tweet) {
        observers.forEach(observer -> observer.notify(tweet));
    }
}