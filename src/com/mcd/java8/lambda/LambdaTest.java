package com.mcd.java8.lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class LambdaTest {

    List<Apple> list = new ArrayList<>();

    {
        list.add(new Apple("red", Math.random() * 100));
        list.add(new Apple("green", Math.random() * 100));
        list.add(new Apple("red", Math.random() * 100));
        list.add(new Apple("green", Math.random() * 100));
        list.add(new Apple("green", Math.random() * 100));
        list.add(new Apple("red", Math.random() * 100));
        list.add(new Apple("red", Math.random() * 100));
    }

    /**
     * Lambda 表达式的基本使用
     * 使用方法引用简写 Lambda 表达式
     */
    @Test
    public void compareTo() {
        //方法一： Lambda 常规写法
        list.sort((Apple a, Apple b) -> a.getWeight().compareTo(b.getWeight()));

        //方法二： Lambda Comparator.comparing 写法
        list.sort(Comparator.comparing(apple -> apple.getWeight()));

        //方法三： 方法引用写法。方法二的语法糖
        list.sort(Comparator.comparing(Apple::getWeight));

        printList(list);
    }

    /**
     * 构造函数引用
     */
    @Test
    public void createNew() {
        //空参构造
        Supplier<Apple> appleSupplier = Apple::new;
        appleSupplier.get();

        //单参构造
        Function<String, Apple> stringAppleFunction = Apple::new;
        stringAppleFunction.apply("red");

        //双参构造
        BiFunction<String, Double, Apple> stringDoubleAppleBiFunction = Apple::new;
        stringDoubleAppleBiFunction.apply("green", 49.6203d);
    }

    /**
     * 比较器复合使用
     */
    @Test
    public void comparingComplex() {
        list.sort(Comparator.comparing(Apple::getColor)
                .reversed()
                .thenComparing(Apple::getWeight));
        printList(list);
    }

    /**
     * 谓词复合使用。优先级，从左到右
     */
    @Test
    public void predicateComplex() {
        Predicate<Apple> red = apple -> "red".equals(apple.getColor());

        Predicate<Apple> noRed = red.negate();

        Predicate<Apple> redAndHeavy = red.and(apple -> apple.getWeight() >= 150d);

        Predicate<Apple> redAndHeavyOrGreen = redAndHeavy.or(apple -> "green".equals(apple.getColor()));

        boolean b = redAndHeavyOrGreen.test(new Apple("red", Math.random() * 100));

        list.forEach(apple -> redAndHeavyOrGreen.test(apple));
    }

    /**
     * 函数复合使用
     */
    @Test
    public void functionComplex() {
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        Function<Integer, Integer> h = f.andThen(g);

        int result = h.apply(2);
    }


    private void printList(List list) {
        System.out.println(" ------------- print start ----------------- ");
        for (Object item : list) {
            System.out.println(item.toString());
        }
        System.out.println(" -------------- print end ------------------ ");
    }

    public class Apple {
        public Apple() {
        }

        public Apple(String color) {
            this.color = color;
        }

        public Apple(String color, Double weight) {
            this.color = color;
            this.weight = weight;
        }

        private String color;
        private Double weight;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Apple{" +
                    "color='" + color + '\'' +
                    ", weight=" + weight +
                    '}';
        }
    }
}
