package com.mcd.java8.default_function;

public class Monster implements Rotatable, Moveable, Resizable{

    /**
     * 解决冲突的三项原则：
     *  1、类中的方法优先级最高；
     *  2、依据 1 无法判断时，子接口的优先级更高；如果函数签名相同时，优先选择更具体的实现默认方法的接口
     *  3、依据 1/2 无法判断时，必须通过显式覆盖和调用期望方法
     *
     * @return
     */

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public void setX(int x) {

    }

    @Override
    public void setY(int y) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void setWidth(int width) {

    }

    @Override
    public void setHeight(int height) {

    }

    @Override
    public void setAbsoluteSize(int width, int height) {

    }

    @Override
    public void setRotationAngle(int angleInDegress) {

    }

    @Override
    public int getRotationAngle() {
        return 0;
    }
}
