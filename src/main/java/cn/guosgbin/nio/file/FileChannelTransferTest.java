package cn.guosgbin.nio.file;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * 测试FIleChannel的文件传输
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 23:20
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class FileChannelTransferTest {
    public static void main(String[] args) throws Exception {

        FileChannel readChannel = new RandomAccessFile("data.txt", "r").getChannel();
        FileChannel writeChannel = new RandomAccessFile("to.txt", "rw").getChannel();

        // position 传输的开始位置
        // count 传输多少个字节
        // target 传到的目标channel

        // 效率高，底层会利用操作系统的零拷贝进行优化， 但是一次最多传输2g
//        readChannel.transferTo(0, readChannel.size(), writeChannel);

        // 优化传输 因为文件可能大于2g
        long size = readChannel.size();
        // left变量表示剩余多少字节没读
        for (long left = size; left > 0; ) {
            // 返回实际传输的字节数，可能为零
            System.out.println("position:" + (size - left) + " left:" + left);
            long readNum = readChannel.transferTo((size - left), left, writeChannel);
            left = left - readNum;
        }


    }
}
