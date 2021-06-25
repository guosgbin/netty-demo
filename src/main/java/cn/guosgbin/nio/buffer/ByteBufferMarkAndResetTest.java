package cn.guosgbin.nio.buffer;

import java.nio.ByteBuffer;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * 测试mark和reset方法
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/22 23:27
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ByteBufferMarkAndResetTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{(byte)97, (byte)98, (byte)99, (byte)100});

        // 切换至读模式
        buffer.flip();
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        debugAll(buffer);

        // mark方法标记当前读取的位置
        buffer.mark();
        // 再读两次
        System.out.println(buffer.get());
        System.out.println(buffer.get());
        debugAll(buffer);

        // reset方法回到mark的位置
        buffer.reset();
        debugAll(buffer);

        System.out.println(buffer.get());
        System.out.println(buffer.get());
        debugAll(buffer);
    }
}
