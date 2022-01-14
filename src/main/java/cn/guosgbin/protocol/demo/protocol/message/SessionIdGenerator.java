package cn.guosgbin.protocol.demo.protocol.message;

import java.util.UUID;

/**
 * @author: Dylan kwok GSGB
 * @date: 2022/1/9 18:00
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class SessionIdGenerator {

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().length());
    }
}
