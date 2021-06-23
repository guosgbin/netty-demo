package cn.guosgbin.nio;

import java.nio.ByteBuffer;

import static cn.guosgbin.nio.ByteBufferUtil.debugAll;

/**
 * 测试rewind方法
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/22 23:20
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ByteBufferRewindTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{(byte)97, (byte)98, (byte)99, (byte)100});

        // 切换到读模式
        buffer.flip();

        byte[] bytes = new byte[4];
        // 读取buffer的数据到byte数组中
        buffer.get(bytes);
        debugAll(buffer);

        // 调用rewind方法重新读取
        // rewind方法其实就是将position置0了
        buffer.rewind();
        debugAll(buffer);

        // 再读一次
        buffer.get(bytes);
        debugAll(buffer);

    }
}
