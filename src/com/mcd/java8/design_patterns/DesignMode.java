package com.mcd.java8.design_patterns;

/**
 * 使用 Lambda 重构面向对象的设计模式
 *    ☆ 1、策略模式
 *      2、模板方法
 *      3、观察者模式
 *      4、责任链模式
 *      5、工厂模式
 */
public class DesignMode {

    /* 1、策略模式 -- 验证策略 -- 测试 */
    Validator numericValidator = new Validator(new IsNumeric());
    boolean b1 = numericValidator.validate("apple");
    Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
    boolean b2 = lowerCaseValidator.validate("496203");

    Validator numericValidator1 = new Validator(s -> s.matches("[a-z]+"));
    boolean b3 = numericValidator.validate("orange");
    Validator lowerCaseValidator1 = new Validator(s -> s.matches("\\d+"));
    boolean b4 = lowerCaseValidator.validate("4399xiaoyouxi");
}

/*************************** 1、策略模式 ***************************/

/**
 * 策略模式代表解决一类算法的通用解决方案。
 * 策略模式包含三部分：
 * 1、一个接口。（策略模式的接口， 代表某个算法）
 * 2、一个或多个该接口的实现。（代表算法的多种实现）
 * 3、一个或多个使用策略对象的客户。
 * <p>
 * 例：验证输入内容是否进行恰当的格式化
 */

/* 定义接口 */
interface ValidationStrategy {
    boolean execute(String s);
}

/* 定义接口的一个或多个实现 */
class IsAllLowerCase implements ValidationStrategy {
    @Override
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}

class IsNumeric implements ValidationStrategy {
    @Override
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}

/* 使用验证策略 */
class Validator {
    private final ValidationStrategy strategy;

    public Validator(ValidationStrategy v) {
        this.strategy = v;
    }

    public boolean validate(String s){
        return strategy.execute(s);
    }
}


