package cn.guosgbin.nio.buffer;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * 测试缓存区的分配
 *
 * HeapByteBuffer：Java堆中的内存，会受到垃圾回收的影响，读写效率较低
 * DirectByteBuffer：直接内存，读写效率较高（少一次拷贝），不会受到垃圾回收的影响， 缺点是分配内存的效率较低
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/22 23:07
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ByteBufferAllocateTest {
    public static void main(String[] args) {
        // class java.nio.HeapByteBuffer
        System.out.println(ByteBuffer.allocate(10).getClass());
        // class java.nio.DirectByteBuffer
        System.out.println(ByteBuffer.allocateDirect(10).getClass());
    }
}
