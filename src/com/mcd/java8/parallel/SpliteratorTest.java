package com.mcd.java8.parallel;

import org.junit.Test;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SpliteratorTest {

    final String SENTENCE = "The memories about home begin with a old tale, which is warm and fortunate. ";

    @Test
    public void test() {
        //迭代版本
        System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");//14

        //规约版本
        Stream<Character> stream0 = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        System.out.println("Found " + countWords(stream0) + " words");//14

        //规约版本并行输出
        Stream<Character> stream1 = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        System.out.println("Found " + countWords(stream1.parallel()) + " words");//23 ?
        /**
         * 问题根源在于：
         *      并行版本的拆分会在原始的 String 的任意位置拆分，有可能会将一个单词拆分成两个词，就会影响计数结果
         *
         *      如何解决这个问题呢？
         *
         *      自定义拆分规则：只能在词尾拆分！
         */

        //规约版本并行输出（自定义拆分规则）
        Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
        Stream<Character> stream2 = StreamSupport.stream(spliterator, true);
        System.out.println("Found " + countWords(stream2) + " words");
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

    /**
     * 规约 Character 流统计字数
     *
     * @param stream
     * @return
     */
    private int countWords(Stream<Character> stream) {
        WordCounter wordCounter = stream.reduce(new WordCounter(0, true), WordCounter::accumulate, WordCounter::combine);
        return wordCounter.getCounter();
    }

    /**
     * 以函数风格重写单词计数器
     */
    //原始类型的流仅限于 int、long 和 double，所以只能使用 Stream<Character>
    public void countWordsFunction() {
        Stream<Character> stream = IntStream.range(0, SENTENCE.length()).mapToObj(SENTENCE::charAt);
        //可以对这个流做规约。规约时，保留两个变量组成的状态。
        //一个 int 用来计算字数，一个 boolean 用来记得上一个遇到的 Character 是否空格
        //因为 Java 中没有 tuple （元组），所以必须创建一个新类（WordCounter）封装这个状态
    }
}

class WordCounter {
    private final int counter;
    private final boolean lastSpace;

    public WordCounter(int counter, boolean lastSpace) {
        this.counter = counter;
        this.lastSpace = lastSpace;
    }

    /**
     * 遍历 Character， 和迭代算法相同
     *
     * @param c
     * @return
     */
    public WordCounter accumulate(Character c) {
        if (Character.isWhitespace(c))
            return lastSpace ? this : new WordCounter(counter, true);
        else
            return lastSpace ? new WordCounter(counter + 1, false) : this;
    }

    /**
     * 合并两个 WordCounter，把其他计数器加起来
     *
     * @param wordCounter
     * @return
     */
    public WordCounter combine(WordCounter wordCounter) {
        //仅需计数器的总和，无需关心 lastSpace
        return new WordCounter(counter + wordCounter.counter, wordCounter.lastSpace);
    }

    public int getCounter() {
        return counter;
    }
}

/**
 * 并行规约自定义拆分规则
 */
class WordCounterSpliterator implements Spliterator<Character> {
    private final String string;
    private int currentChar = 0;

    public WordCounterSpliterator(String string) {
        this.string = string;
    }

    /**
     * 传递 String 中当前位置的 Character 给 Consumer，并让位置 +1。
     * 作为参数传递的 Consumer 是一个 Java 内部类，在遍历流时将要处理的 Character 传给一系列要对其执行的函数。
     * 这里只有一个规约函数，即 WordCounter 类的 accumulate 方法。
     * 如果新的指针位置小于 String 的总长，且还有要遍历的 Character，则 tryAdvance 返回 true
     *
     * @param consumer
     * @return
     */
    @Override
    public boolean tryAdvance(Consumer<? super Character> consumer) {
        consumer.accept(string.charAt(currentChar++));//处理当前字符
        return currentChar < string.length();
    }

    /**
     * Spliterator 中的核心方法
     * 定义拆分要遍历的数据结构的逻辑
     *
     * @return
     */
    @Override
    public Spliterator<Character> trySplit() {
        int currentSize = string.length() - currentChar;
        if (currentSize < 10)
            //返回 null 表示解析的 string 已经足够小，无需再拆分，可以顺序处理
            return null;
        // 尝试拆分的位置设定为要解析的 string 的中间
        for (int splitPos = currentSize / 2 + currentChar; splitPos < string.length(); splitPos++) {
            //让拆分位置直接前进到下一个空格
            if (Character.isWhitespace(string.charAt(splitPos))){
                //创建一个新 WordCounterSpliterator 来解析 String 从开始到拆分位置的部分
                Spliterator<Character> spliterator = new WordCounterSpliterator(string.substring(currentChar, splitPos));
                //将这个 WordCounterSpliterator 的起始位置设为拆分位置
                currentChar = splitPos;
                return spliterator;
            }
        }
        return null;
    }

    /**
     * 还需要遍历的元素的 estimatedSize 就是这个 Spliterator 解析的 string 的总长度和当前遍历的位置的差
     * @return
     */
    @Override
    public long estimateSize() {
        return string.length() - currentChar;
    }

    /**
     * 该方法告诉框架这个 Spliterator 是
     *      ORDERED （顺序就是 String 中的各个 Character 次序）、
     *      SIZED（estimatedSize 方法的返回值是精确的）、
     *      SUBSIZED（trySplit 方法创建的其他 Spliterator 也有确切大小）、
     *      NONNULL（String 中不能有 null 的 Character）、
     *      IMMUTABLE（在解析 String 时不能再添加 Character， 因为 String 本身是不可变类）
     * @return
     */
    @Override
    public int characteristics() {
        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
    }
}