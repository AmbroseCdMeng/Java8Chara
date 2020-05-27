package com.mcd.java8.optional;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * 用 Optional 替代 null
 */
public class OptionalTest {
    public void test(){
        //1、声明空 Optional
            //Optional<Car> optCar = Optional.empty();
        //2、声明非空 Optional
            //Optional<Car> optCar = Optional.of(new Car());    //如果 car 为 null 则会抛出 NullPointerException
        //3、声明可空 Optional
            Optional<Car> optCar = Optional.ofNullable(new Car());    //如果 car 为 null 则会得到空 Optional 对象


    }

    public void mapTest(){
        //2、从 Optional 对象提取值
        //1、使用 map 从 Optional 对象提取值
        Optional<Insurance> optInsurance = Optional.ofNullable(new Insurance());
        Optional<String> optName = optInsurance.map(Insurance::getName);
    }

    public void flatMapTest(){
        Optional<Person> person = Optional.ofNullable(new Person());
        String name = getCarInsuranceName(person);
    }

    /**
     * flatMap 提取值
     * @param person
     * @return
     */
    public String getCarInsuranceName(Optional<Person> person){
        return person.flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }

    /**
     *
     */
    public Insurance findCheapestInsurance(Person person, Car car){
        //不同的保险公司提供的查询服务
        //对比所有数据
        Insurance cheapestInsurance = Optional.ofNullable(new Insurance()).orElse(new Insurance());
        return cheapestInsurance;
    }

    public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person,  Optional<Car> car){
        //常规操作
//        if (person.isPresent() && car.isPresent())
//            return Optional.of(findCheapestInsurance(person.get(), car.get()));
//        else
//            return Optional.empty();

        //或者以不解包的方式组合两个 Optional 对象
        return person.flatMap( p -> car.map(c->findCheapestInsurance(p, c)));
    }

    /**
     * 使用 filter 剔除特定值
     */
    public String getCarInsuranceName(Optional<Person> person,  int minAge){
        return person.filter(p -> p.getAge().getAsInt() >= 18)
                .flatMap(Person::getCar)
                .flatMap(Car::getInsurance)
                .map(Insurance::getName)
                .orElse("Unknown");
    }
}

/**
 * 如果为缺省值建模
 *      Java 8 以前
 *          1、防御式检查减少 NullPointerException
 *              深层质疑（判断不为 null 时继续，会产生很多层嵌套）
 *              质疑退出（判断为 null 时退出，会产生很多退出语句）
 *
 *      Java 8
 *          1、Optional 类
 *
 * 使用 Optional 对象
 *      1、创建 Optional 对象
 *          1、声明空 Optional
 *              Optional<Car> optCar = Optional.empty();
 *          2、声明非空 Optional
 *              Optional<Car> optCar = Optional.of(car);    //如果 car 为 null 则会抛出 NullPointerException
 *          3、声明可空 Optional
 *              Optional<Car> optCar = Optional.ofNullable(car);    //如果 car 为 null 则会得到空 Optional 对象
 *
 *      2、从 Optional 对象提取值
 *          1、使用 map 从 Optional 对象提取值
 *              Optional<Insurance> optInsurance = Optional.ofNullable(new Insurance());
 *              Optional<String> optName = optInsurance.map(Insurance::getName);
 *
 *              ★★★★★★★★★★★★
 *              ★★  不幸的是  ★★
 *              ★★★★★★★★★★★★
 *
 *              Optional<String> optName = optPerson.map(Person::getCar)                //optPerson 是 Optional<Person> 类型，调用 map OK
 *                                                  .map(Car::getInsurance)             //Person::getCar 返回 Optional<Car> 类型，这意味着这里的 map 操作的是 Optional<Option<Car>> 类型，所以调用 getInsurance 是非法的
 *                                                  .map(Insurance::getName);           //Error
 *
 *              这段代码是无法通过编译的。也就是说 map 提取值得方法不能实现链式编程
 *
 *
 *          2、使用 flatMap 从 Optional 对象提取值
 *              person.flatMap(Person::getCar)
 *                 .flatMap(Car::getInsurance)
 *                 .map(Insurance::getName)
 *                 .orElse("Unknown");
 *
 *     3、默认行为及解引用 Optional 对象
 *          □ get()
 *              最简单但最不安全的方法。如果变量存在，则返回，如不存在，则抛出 NoSuchElementException 异常。
 *              所以，除非确定 Optional 有值，否则不要使用该方法
 *          □ orElse(T other)
 *              允许在 Optional 对象不包含值时提供一个默认值
 *          □ orElseGet(Supplier< ? extends T > other)
 *              orElse 方法的延伸版。Supplier 只有在 Optional 对象不包含值时才执行调用。
 *              √ 创建默认值是件费时费力的工作，可以考虑这种方式（提升性能）
 *              √ 需要非常确定某个方法仅在 Optional 为空的时候才调用（有严格限制条件）
 *          □ ifPresent(Consumer< ? super T)
 *              在变量值存在时执行一个作为参数传入的方法，否则就不进行任何操作
 *
 *      4、两个 Optional 对象的结合
 *
 *      5、使用 filter 剔除特定值
 *
 */