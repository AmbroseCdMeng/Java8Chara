package com.mcd.java8.parallel;

import org.junit.Test;

public class SpliteratorTest {

    @Test
    public void test() {
        final String SENTENCE = "The memories about home begin with a old tale, which is warm and fortunate. ";
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
    }


    /**
     * 迭代式字数（单词）统计
     *
     * @param s
     * @return
     */
    public int countWordsIteratively(String s) {
        int counter = 0;
        boolean lastSpace = true;//上一个字符是空格
        for (char c : s.toCharArray()) {
            if (Character.isWhitespace(c))
                lastSpace = true;
            else {
                if (lastSpace)//上一个是空格，而当前位置字符不是空格时， +1
                    counter++;
                lastSpace = false;
            }
        }
        return counter;
    }
}
