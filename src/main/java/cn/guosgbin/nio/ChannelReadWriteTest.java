package cn.guosgbin.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static cn.guosgbin.nio.ByteBufferUtil.debugAll;

/**
 * 测试channel 和 buffer 的read和write
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/22 23:32
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ChannelReadWriteTest {
    public static void main(String[] args) {

        try (FileChannel channel = new FileInputStream("data.txt").getChannel();
             FileChannel outChannel = new FileOutputStream("data.txt").getChannel();
        ) {
            ByteBuffer buffer = ByteBuffer.allocate(10);
            // buffer添加数据 - (1)
//            channel.read(buffer);
//            debugAll(buffer);

            // buffer添加数据 - (2)
            buffer.put(new byte[]{(byte)97, (byte)98, (byte)99, (byte)100});
            debugAll(buffer);

            buffer.flip();

            // 读取buffer的数据 - (1)
            int write = outChannel.write(buffer);
            System.out.println(write);
            debugAll(buffer);


            // 读取buffer的数据 - (2)
//            System.out.println(buffer.get());
//            debugAll(buffer);

        } catch (IOException ignored) {
        }

    }
}
