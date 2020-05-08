package com.mcd.java8.stream;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class StreamTest {
    private List<Dish> dishes = new ArrayList<>();

    {
        dishes.add(new Dish("potato", "vegetables",Math.random() * 100));
        dishes.add(new Dish("banana","fruit",Math.random() * 100));
        dishes.add(new Dish("carrot","vegetables",Math.random() * 100));
        dishes.add(new Dish("onion", "vegetables",Math.random() * 100));
        dishes.add(new Dish("peach", "fruit",Math.random() * 100));
        dishes.add(new Dish("mango", "fruit",Math.random() * 100));
    }
    /*
    筛选 List<Dish> 中 calories 低于 400 的集合
     */

    //Java 7 写法
    @Test
    public void filterCalories_7() {
        List<Dish> lowCaloricDishes = new ArrayList<>();
        //筛选
        for (Dish dish : dishes) {
            if (dish.getCalories() < 400d)
                lowCaloricDishes.add(dish);
        }
        //排序
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish d1, Dish d2) {
                return Double.compare(d1.getCalories(), d2.getCalories());
            }
        });
        //排序后组合，返回 name
        List<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish dish : lowCaloricDishes)
            lowCaloricDishesName.add(dish.getName());
    }

    //Java 8 写法
    @Test
    public void filterCalories_8(){
        List<String> lowCaloricDishesName = dishes.stream() // 如果需要利用多核架构并行执行，只需要将 stream 换为 parallelStream
                .filter(dish -> dish.getCalories() < 400d)  //流筛选：calories < 400
                .sorted(comparing(Dish::getCalories))       //流排序：calories
                .map(Dish::getName)                         //流提取：name
                .limit(3)                                   //流截断：3 个
                .collect(toList());                         //流执行/流转换：list  || collect 触发流水线执行并关闭

        /* 可以利用 print 查看流的执行过程 */
        List<String> low = dishes.stream()
                .filter(dish -> {
                    System.out.println("filtering" + dish.getName());
                    return dish .getCalories() < 400d;
                })
                .map(dish -> {
                    System.out.println("mapping" + dish.getName());
                    return dish.getName();
                })
                .limit(3)
                .collect(Collectors.toList());

        /* 上述执行过程可以看出：
            打印出来的只有三条记录
                是因为其中有好几种优化利用了流的延迟性质。
                    1、limit 操作和短路技巧
                    2、filter 和 map 是两个独立操作，但是他们合并到同一次遍历中，该操作称之为循环合并。
        */
    }

    @Test
    public void filterCalories_8_groupBy(){
        Map<String, List<Dish>> dishesByType = dishes.stream().collect(groupingBy(Dish::getType));
    }
}

class Dish {
    private String name;
    private String type;
    private Double calories;

    public Dish(String name, String type, Double calories) {
        this.name = name;
        this.type = type;
        this.calories = calories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }
}
