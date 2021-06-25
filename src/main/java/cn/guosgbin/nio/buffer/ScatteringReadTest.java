package cn.guosgbin.nio.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static cn.guosgbin.nio.buffer.ByteBufferUtil.debugAll;

/**
 * 测试分散读取
 * 分散读取（ Scattering Reads）是指从 Channel 中读取的数据“分散” 到多个 Buffer 中。
 * 照缓冲区的顺序，从 Channel 中读取的数据依次将 Buffer 填满。
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 8:26
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ScatteringReadTest {
    public static void main(String[] args) {

        // 文件内容 wohaocai 依次拿wo hao cai
        try (FileChannel channel = new RandomAccessFile("data.txt", "r").getChannel()) {
            ByteBuffer buffer1 = ByteBuffer.allocate(2);
            ByteBuffer buffer2 = ByteBuffer.allocate(3);
            ByteBuffer buffer3 = ByteBuffer.allocate(3);

            channel.read(new ByteBuffer[]{buffer1, buffer2, buffer3});

            debugAll(buffer1);
            debugAll(buffer2);
            debugAll(buffer3);

        } catch (IOException e) {
        }

    }
}
