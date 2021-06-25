package cn.guosgbin.nio.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * 测试聚集写入
 * 聚集写入（ Gathering Writes）是指将多个 Buffer 中的数据“聚集”到 Channel。
 * 按照缓冲区的顺序，写入 position 和 limit 之间的数据到 Channel 。
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 8:33
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class GatheringWriteTest {
    public static void main(String[] args) {

        ByteBuffer buffer1 = ByteBuffer.allocate(4);
        ByteBuffer buffer2 = ByteBuffer.allocate(5);
        ByteBuffer buffer3 = ByteBuffer.allocate(6);

        buffer1.put("halo".getBytes());
        buffer2.put("hello".getBytes());
        buffer3.put("你好".getBytes());


        debugAll(buffer1);
        debugAll(buffer2);
        debugAll(buffer3);

        // 切换读模式
        buffer1.flip();
        buffer2.flip();
        buffer3.flip();
        debugAll(buffer1);


        try (FileChannel channel = new RandomAccessFile("data.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{buffer1, buffer2, buffer3});
        } catch (IOException e) {
        }

    }
}
