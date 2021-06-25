package cn.guosgbin.nio.buffer;

import java.nio.ByteBuffer;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * @author: Dylan kwok GSGB
 * @date: 2021/6/22 22:44
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ByteBufferReadWriteTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        debugAll(buffer);

        buffer.put((byte) 97);
        debugAll(buffer);

        buffer.put(new byte[]{98, 99, 100});
        debugAll(buffer);

        // 切换到读模式
        buffer.flip();
        debugAll(buffer);

        // 读一个数据 观察position
        System.out.println(buffer.get());
        debugAll(buffer);

        // compact方法 未读取的数据会
        buffer.compact();
        debugAll(buffer);

        // 在写几个数
        buffer.put(new byte[]{101, 102, 103});
        debugAll(buffer);

        // clear方法
        buffer.clear();
        debugAll(buffer);
    }
}
