package com.mcd.java8.design_patterns;

import org.junit.Test;
import org.w3c.dom.Text;

import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * 使用 Lambda 重构面向对象的设计模式
 *      1、策略模式
 *      2、模板方法
 *      3、观察者模式
 *    ☆ 4、责任链模式
 *      5、工厂模式
 */
public class ChainOfResponsibilityMode {
    @Test
    public void test(){
        /* 基本使用 */
        /* 创建两个处理对象 */
        ProcessingObject<String> p1 = new HeaderTextProcessing();
        ProcessingObject<String> p2 = new SpellCheckProcessing();

        /* 将两个处理对象链接起来 */
        p1.setSuccessor(p2);

        String result1 = p1.handle("Aren't labda really sexy? ");
        System.out.println(result1);

        /* Lambda 表达式调用 */
        UnaryOperator<String> headerProcessing = text -> "From Raoul, Mario and Alan: " + text;
        UnaryOperator<String> speelCheckerProcessing = text -> text.replaceAll("labda", "lambda");
        Function<String, String> pipeline = headerProcessing.andThen(speelCheckerProcessing);

        String result2 = pipeline.apply("Aren't labda really sexy? ");
        System.out.println(result2);
    }
}

/*************************** 4、责任链模式 ***************************/

/**
 * 责任链模式是一种创建对象序列（如：操作序列）的通用方案。
 *
 * 一个处理对象可能需要在完成工作之后，将结果传递给另一个对象，这个对象接着做一些工作，再转交给下一个处理对象。
 *
 * 通常，这种模式是通过定义一个代表处理对象的抽象类来实现的。
 * 在抽象类中会定义一个字段来记录后续对象。
 * 一旦对象完成它的工作，处理对象就会将它的工作转交给它的后继。
 */

abstract class ProcessingObject<T>{

    protected ProcessingObject<T> successor;
    public void setSuccessor(ProcessingObject<T> successor){
        this.successor = successor;
    }

    public T handle (T input){
        T r = handleWork(input);
        if(successor != null)
            return successor.handle(r);
        return r;
    }

    abstract protected T handleWork(T work);
}

/* handle 方法提供了如何进行工作处理的框架，不同的处理对象可以通过继承 ProcessingObject 类，提供 handleWork 方法来进行创建 */

/**
 * 使用该设计模式。
 *
 * 创建两个处理对象，它们的功能是进行一些文本处理工作
 */
class HeaderTextProcessing extends ProcessingObject<String>{
    @Override
    protected String handleWork(String text){
        return "From Raoul, Mario and Alan: " + text;
    }
}

class SpellCheckProcessing extends ProcessingObject<String>{
    @Override
    protected String handleWork(String text) {
        return text.replaceAll("labda", "lambda");//拼写检查
    }
}
