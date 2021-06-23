package cn.guosgbin.nio;


import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 测试缓冲区
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/22 22:19
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class ByteBufferTest {

    public static void main(String[] args) {
        // 获得缓冲区
        // 1.输入输出流
        // 2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 分配缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 读取通道的数据到buffer
                int len = channel.read(buffer);
                log.debug("读取通道的数据个数: {}", len);
                if (len == -1) {
                    // 通道的数据已经读完了
                    break;
                }
                // 切换到读模式
                buffer.flip();
                // 打印buffer的数据
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.debug("每次读到的数据: {}", (char) b);
                }
                // 切换到写模式
//                buffer.clear();
            }
        } catch (IOException ignored) {
        }
    }
}
