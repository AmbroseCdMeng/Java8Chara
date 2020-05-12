package com.mcd.java8.stream;

import org.junit.Test;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.joining;

public class StreamTest {
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
    public void filterCalories_8() {
        List<String> lowCaloricDishesName = dishes.stream() // 如果需要利用多核架构并行执行，只需要将 stream 换为 parallelStream
                .filter(dish -> dish.getCalories() < 400d)  //流筛选：calories < 400
                .sorted(comparing(Dish::getCalories))       //流排序：calories
                .map(Dish::getName)                         //流提取：name
                .limit(3)                                   //流截断：3 个
                .collect(toList());                         //流执行/流转换：list  || collect 终端操作触发流水线执行并关闭

        /*
         * 流的使用一般包含 3 件事：
         *   1、一个数据源来执行查询
         *   2、n 个中间操作形成流的流水线    filter/map/limit/sorted/distinct
         *   3、一个终端操作执行流并返回结果    forEach/count/collect
         * */

        /* 可以利用 print 查看流的执行过程 */
        List<String> low = dishes.stream()
                .filter(dish -> {
                    System.out.println("filtering" + dish.getName());
                    return dish.getCalories() < 400d;
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
    public void filterCalories_8_groupBy() {
        Map<String, List<Dish>> dishesByType = dishes.stream().collect(groupingBy(Dish::getType));
    }

    //Java8 流的常用方法
    @Test
    public void test() {
        //1、谓词筛选 filter
        List<Dish> dishes1 = dishes.stream().filter(Dish::isVegetarian).collect(toList());

        //2、去重 distinct
        List<String> types = dishes.stream().map(Dish::getName).distinct().collect(toList());

        //3、截断流 limit
        List<Dish> dishes2 = dishes.stream().limit(3).collect(toList());

        //4、跳过流 skip
        List<Dish> dishes3 = dishes.stream().skip(2).collect(toList());

        //5、映射
        //5.1 map
        List<String> dishes4 = dishes.stream().map(Dish::getName).collect(toList());
        List<Integer> dishes5 = dishes.stream().map(Dish::getName).map(String::length).collect(toList());

        //5.2 flatMap
        List<String> words = Arrays.asList("Hello", "World");//给定 ['Hello', 'World']  返回里面不同字符 ['H', 'e', 'l', 'o', 'W', 'r', 'd']
        List<String[]> chars1 = words.stream()
                .map(word -> word.split(" "))
                .distinct()
                .collect(toList());
        // flatMap 方法让你把一个流中的每个值都换成另一个流，然后把所有流连接起来成为一个流
        List<String> chars2 = words.stream()
                .map(word -> word.split(" "))
                .flatMap(Arrays::stream).distinct()
                .collect(toList());

        //6、谓词
        //6.1 至少匹配一个 anyMatch       短路匹配
        boolean match1 = dishes.stream().anyMatch(Dish::isVegetarian);
        //6.2 匹配所有 allMatch           短路匹配
        boolean match2 = dishes.stream().allMatch(dish -> dish.getCalories() < 1000);
        //6.3 不匹配                      短路匹配
        boolean match3 = dishes.stream().noneMatch(dish -> dish.getCalories() < 1000);

        //7、查找元素 findAny             短路匹配，找到结果时立即结束
        Optional<Dish> dishes6 = dishes.stream().filter(Dish::isVegetarian).findAny();
        //  Optional 容器类。避免 null 检查
        //      isPresent() 在 Optional 包含值时返回 true 否则返回 false
        //      isPresent(Consumer<T> block) 在 Optional 包含值时执行 block 内的相关代码
        //      T get() 在值存在时返回值，否则抛出 NoSuchElement 异常
        //      T.orElse(T other) 在值存在时返回值，否则返回默认值

        //8、查找第一个元素 findFirst()
        Optional<Dish> first = dishes.stream().findFirst();

        //9、forEach     void
        dishes.stream().forEach(dish -> System.out.println(dish.getCalories()));
        //10、 count     long
        long count = dishes.stream().count();
        //11、 collect   void

        //12、规约 reduce
        double sum1 = dishes.stream().map(Dish::getCalories).reduce(0d, Double::sum);// 有初始值
        Optional<Double> sum2 = dishes.stream().map(Dish::getCalories).reduce((a, b) -> a * b);// 无初始值
        Optional<Double> sum3 = dishes.stream().map(Dish::getCalories).reduce(Double::max);// 无初始值

        //13、数值流
//        int sum4 = dishes.stream().map(Dish::getCalories).sum();// Streams 接口没有定义 sum 方法，因为 dished.stream 返回 Stream<Dish> 而将 Dish 求和是没有意义的
//        但是 stream 提供了其他的数值流方法

        // Stream 转换为数值流
        double sum4 = dishes.stream().mapToDouble(Dish::getCalories).sum();
        // 数值流转换为 Stream
        DoubleStream ds = dishes.stream().mapToDouble(Dish::getCalories);
        Stream<Double> box1 = ds.boxed();

        //14、数值范围 Java8 引入了 IntStream 和 LongStream 静态方法，生成范围，第一个参数接收起始值，第二个参数接收结束值
        IntStream even1 = IntStream.range(1, 100).filter(n -> (n & 1) == 0);//不含尾
        IntStream even2 = IntStream.range(1, 100).filter(n -> (n & 1) == 0);//含尾

        //13、例：筛选指定范围的勾股数
        Stream<double[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed()
                .flatMap(a ->
                        IntStream.rangeClosed(a, 100).mapToObj(b ->
                                new double[]{a, b, Math.sqrt(a * a + b * b)}
                        )
                )
                .filter(t -> t[2] % 1 == 0);//第三个计算出来的值为整数

        //14、创建流

        //14.1 由值创建
        Stream<String> stream1 = Stream.of("Java 8", "Stream", "Create");
        stream1.map(String::toUpperCase).forEach(System.out::println);

        //14.2 由数组创建
        int[] num = {1, 3, 4, 5, 7, 9, 10};
        IntStream stream2 = Arrays.stream(num);
        int sum = stream2.sum();

        //14.3 由文件创建
        long uniqueWords = 0;
        try (
                Stream<String> lines = Files.lines(Paths.get("datas.txt"), Charset.defaultCharset())) {
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" ")))
                    .distinct()
                    .count();
        } catch (IOException e) {

        }

        //14.3 由函数生成
        Stream.iterate(0, n -> n + 2).limit(10).forEach(System.out::println);//该操作将生成无限流，即没有结尾，所以我们使用了 limit 限制流的大小
        Stream.generate(Math::random).limit(10).forEach(System.out::println);//该操作将同样生成无线流，所以使用 limit 来限制了大小

        //14 例：斐波那契数列
        IntStream twos = IntStream.generate(new IntSupplier() {
            private int prev = 0;
            private int curr = 1;

            @Override
            public int getAsInt() {
                int oldPrev = this.prev;
                int next = this.prev + this.curr;
                this.prev = this.curr;
                this.curr = next;
                return oldPrev;
            }
        });
        twos.limit(20).forEach(System.out::print);

        //15、Collectors 收集器方法
        //15.1 groupingBy
        //一级分组
        Map<String, List<Dish>> map1 = dishes.stream().collect(groupingBy(Dish::getType));
        //多级分组 - 1
        Map<String, Map<String, List<Dish>>> map2 = dishes.stream().collect(groupingBy(Dish::getType
                , groupingBy(dish ->
                        dish.getCalories() < 400d ? "LOW" :
                                dish.getCalories() < 600d ? "NORMAL" :
                                        "HIGHT"
                )));
        //多级分组 - 2
        Map<String, Long> map3 = dishes.stream().collect(groupingBy(Dish::getType
                , counting()));

        /* * 实际上， groupingBy (f) 就是 groupingBy(f, toList()) 的缩写 * */
        //多级分组 - 3
        Map<String, Optional<Dish>> map4 = dishes.stream().collect(groupingBy(Dish::getType
                , maxBy(comparingDouble(Dish::getCalories))));

        //15.2 toList
        List<Dish> list1 = dishes.stream().collect(toList());
        //15.3 counting
        long long1 = dishes.stream().collect(counting());
        //15.4 maxBy
        Optional<Dish> max = dishes.stream().collect(maxBy(Comparator.comparingDouble(Dish::getCalories)));
        //15.5 minBy
        Optional<Dish> min = dishes.stream().collect(minBy(Comparator.comparing(Dish::getCalories)));
        //15.6 summingInt summingDouble ...
        double total = dishes.stream().collect(summingDouble(Dish::getCalories));//求和
        double avg = dishes.stream().collect(averagingDouble(Dish::getCalories));//平均
        // 想到得到多个结果时，可以使用 *SummaryStatistics 收集器
        DoubleSummaryStatistics iss = dishes.stream().collect(summarizingDouble(Dish::getCalories));
        //DoubleSummaryStatistics{count = 1, sum = 1, min = 1, average = 1, max = 1}
        double count0 = iss.getCount();
        double sum0 = iss.getSum();

        //16、字符串连接 joining
        String names0 = dishes.stream().map(Dish::getName).collect(joining());
        String names1 = dishes.stream().map(Dish::getName).collect(joining(", "));//可以重载分隔符


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

    //素食
    public boolean isVegetarian() {
        return !"meat".equals(this.getType());
    }

    @Override
    public String toString() {
        return "Dish{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", calories=" + calories +
                '}';
    }
}
