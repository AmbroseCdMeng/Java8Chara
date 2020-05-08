package com.mcd.java8.lambda;

import org.junit.Test;

import java.util.function.Function;

public class FunctionComplexTest {

    /**
     * 测试线：先抬头，再拼写检查，最后落款
     */
    @Test
    public void test_line_1() {
        Function<String, String> addHeader = Letter::addHeader;
        Function<String, String> transformationPipeline = addHeader.andThen(Letter::checkSpelling).andThen(Letter::addFooter);
    }

    /**
     * 测试线：先抬头，再落款
     */
    @Test
    public void test_line_2(){
        Function<String, String> addHeader = Letter ::addHeader;
        Function<String, String> transformationPipeline = addHeader.andThen(Letter::addFooter);
    }
}

/**
 * 拼写检查类
 */
class Letter {
    public static String addHeader(String text) {
        return "From XiaoYuer :" + text;
    }

    public static String addFooter(String text) {
        return text + " Kind regards";
    }

    public static String checkSpelling(String text) {
        return text.replaceAll("labda", "lambda");
    }
}