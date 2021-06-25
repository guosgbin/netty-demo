package cn.guosgbin.nio.exercises;

import java.nio.ByteBuffer;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * 测试黏包和半包
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 21:34
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class StickyAndHalfTest {

    /**
     * 网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
     * 但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为
     *
     * 1. Hello,world\n
     * 2. I'm zhangsan\n
     * 3. How are you?\n
     *
     * 变成了下面的两个 byteBuffer (黏包，半包)
     *
     * 1. Hello,world\nI'm zhangsan\nHo
     * 2. w are you?\n
     *
     * 现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
     * @param args
     */
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        //                     11            24
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
        split(source);

        source.put("w are you?\nhaha!\n".getBytes());
        split(source);

    }

    /**
     * 处理
     * @param source 每次处理的数据
     */
    private static void split(ByteBuffer source) {
        // 切换到读模式
        source.flip();
        int oldLimit = source.limit();
        for (int i = 0; i < oldLimit; i++) {
            if (source.get(i) == '\n') {
                // 找到了一个\n符
                // 第一次开始位置为0，结束位置为i
                // 第二次开始位置为上一次读取的位置，也就是position，结束位置为 i
                int length = i + 1 - source.position();
                ByteBuffer tempBuffer = ByteBuffer.allocate(length);
                // 服务buffer到临时buffer
                for (int j = 0; j < length; j++) {
                    tempBuffer.put(source.get());
                }
                // 打印临时buffer
                debugAll(tempBuffer);
            }
        }
        // 将未读完的数据存着下一次读
        source.compact();
    }
}
