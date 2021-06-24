package cn.guosgbin.nio.file;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static cn.guosgbin.nio.ByteBufferUtil.debugAll;

/**
 * 测试FileChannel
 * <p>
 * FileChannel 只能工作在阻塞模式下
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 22:42
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class FileChannelTest {
    /**
     * 测试创建FileChannel
     */
    private static void testCreate() throws FileNotFoundException {
        // 获取FileChannel
        // 1. FileInputStream获得的FileChannel只能用于    读
        FileChannel channel1 = new FileInputStream("data.txt").getChannel();
        // 2. FileOutputStream获得的FileChannel只能用于   写
        FileChannel channel2 = new FileOutputStream("data.txt").getChannel();
        // 3. RandomAccessFile 是否能读写根据构造 RandomAccessFile 时的读写模式决定
        FileChannel channel3 = new RandomAccessFile("data.txt", "rw").getChannel();
    }

    /**
     * FileChannel读取
     */
    private static void testRead() throws IOException {
        FileChannel channel = new FileInputStream("data.txt").getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        // 返回读到了几个字节，-1表示读完了
        while (true) {
            // 从FileChannel中读取
            int read = channel.read(buffer);
            log.debug("channel此次读到的字节个数为: " + read);
            if (read == -1) {
                break;
            }

            // 切到读模式
            buffer.flip();
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                log.debug("此次读到的数据为: " + (char) b);
            }

            // buffer切换至写模式
            buffer.clear();
        }
    }


    /**
     * FileChannel读取
     * <p>
     * 需要知道的是   channel的大小是有限的 channel.write()并不能保证一次将buffer的内容全部写入到channel
     */
    private static void testWrite() throws IOException {
        FileChannel channel = new FileOutputStream("data.txt").getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{97, 98, 99});

        // 切换到读模式
        buffer.flip();
        // 循环读取，因为channel大小是有限的
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
    }

    /**
     * channel的关闭
     *
     * channel 必须关闭，
     * 不过调用了 FileInputStream、FileOutputStream 或者 RandomAccessFile
     * 的 close 方法会间接地调用 channel 的 close 方法
     */


    /**
     * channel的位置
     */
    public static void testPosition() throws IOException {
        FileChannel channel = new RandomAccessFile("data.txt", "rw").getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(2);

        long position = channel.position();

//        int read = channel.read(buffer);
//        long position2 = channel.position();
//        System.out.println("读取字节的个数 " + read);
//        System.out.println("读取前的位置 " + position);
//        System.out.println("读取后的位置 " + position2);

        channel.position(100);
        int read = channel.read(buffer);
        long position2 = channel.position();
        System.out.println("读取字节的个数 " + read);
        System.out.println("读取前的位置 " + position);
        System.out.println("读取后的位置 " + position2);

        // 数据空洞
        buffer.put(new byte[]{120, 121});
        buffer.clear();
        int write = channel.write(buffer);
        System.out.println(write);

    }

    /**
     * 获取文件的大小
     */
    public static void testSize() throws IOException {
        FileChannel channel = new RandomAccessFile("data.txt", "rw").getChannel();
        long size = channel.size();
        // 字节数
        System.out.println("文件大小: " + size);
    }

    public static void main(String[] args) throws Exception {
        testSize();
    }
}
