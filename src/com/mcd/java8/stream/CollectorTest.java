package com.mcd.java8.stream;

import java.util.*;

import static java.util.stream.Collectors.*;

public class CollectorTest {
    private List<Dish> dishes = new ArrayList<>();

    {
        dishes.add(new Dish("potato", "vegetables", Math.random() * 100));
        dishes.add(new Dish("banana", "fruit", Math.random() * 100));
        dishes.add(new Dish("carrot", "vegetables", Math.random() * 100));
        dishes.add(new Dish("onion", "vegetables", Math.random() * 100));
        dishes.add(new Dish("peach", "fruit", Math.random() * 100));
        dishes.add(new Dish("mango", "fruit", Math.random() * 100));
        dishes.add(new Dish("beef", "meat", Math.random() * 100));
        dishes.add(new Dish("meat", "meat", Math.random() * 100));
    }
    //收集器结果转换

    /**
     * 分组操作的 Map 结果中的每个值上包装 Optional 几乎没用。
     * 所以，可能需要把收集器返回的结果转换为另一种类型 Collectors.collectingAndThen 工厂方法
     */
    public void typeConvertTest() {
        Map<String, Dish> mostCaloricByType =
                dishes.stream()
                        .collect(groupingBy(Dish::getType   //分类
                                , collectingAndThen(        //该工厂方法接收两个参数：要转换的收集器， 转换函数。 并返回一个收集器
                                        maxBy(Comparator.comparingDouble(Dish::getCalories))    //包装后的收集器
                                        , Optional::get)));                                     //转换函数
    }

    /**
     * groupingBy 联合使用的其他收集器的例子
     */
    public void groupingByExample() {
        Map<String, Double> totalCaloriesByTypt =
                dishes.stream().collect(groupingBy(Dish::getType
                        , summingDouble(Dish::getCalories)));

        Map<String, Set<String>> caloricLevelsByType =
                dishes.stream().collect(groupingBy(Dish::getType
                        , mapping(dish -> dish.getCalories() < 400d ? "LOW" :
                                dish.getCalories() < 600d ? "NORMAL" :
                                        "HEIGHT", toSet())));
    }

    /**
     * 分区。分区是分组的特殊情况。由一个 boolean 型的谓词作为分类依据的分组，叫做分区
     */
    public void partitioningByTest(){
        Map<Boolean, List<Dish>> partitionedDishes = dishes.stream().collect(partitioningBy(Dish::isVegetarian));
        List<Dish> vegetarianDishes = partitionedDishes.get(true);
    }


}
