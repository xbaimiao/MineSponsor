package com.xbaimiao.mine.sponsor.mirai;

import java.util.Random;

public class Test {

    static Random random = new Random();

    public static void main(String[] args) throws Exception {
        int index = 0;
        while (true) {
            index++;
            if (random.nextDouble() < 0.0005) {
                System.out.println("0.0005概率成立，本次计算共运算" + index + "次");
                index = 0;
            }
        }
    }

}
