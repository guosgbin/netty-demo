package cn.guosgbin.cast.netty.component.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;

import static cn.guosgbin.cast.netty.component.buf.BufferLogUtil.logBuf;

/**
 * 测试ByteBuf
 * <p>
 * 是对字节数据的封装
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/5 23:32
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class ByteBufTest {

    /**
     * 测试创建Buffer
     */
    private static void createTest01() {
        // 不给初始值的话，默认值就是256
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        logBuf(buffer);
    }

    /**
     * 测试创建Buffer  直接内存和堆内存
     * <p>
     * 直接内存创建和销毁的代价昂贵，但读写性能高（少一次内存复制），适合配合池化功能一起用
     * 直接内存对 GC 压力小，因为这部分内存不受 JVM 垃圾回收的管理，但也要注意及时主动释放
     */
    private static void createTest02() {
        // 创建池化基于堆的ByteBuf
        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
        // 创建池化基于直接内存的ByteBuf
        ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer();

        System.out.println(heapBuffer);
        System.out.println(directBuffer);

    }

    /**
     * 测试创建Buffer 池化和非池化
     * 池化的最大意义在于可以重用 ByteBuf，优点有
     * <p>
     * 1 没有池化，则每次都得创建新的 ByteBuf 实例，这个操作对直接内存代价昂贵，就算是堆内存，也会增加 GC 压力
     * 2 有了池化，则可以重用池中 ByteBuf 实例，并且采用了与 jemalloc 类似的内存分配算法提升分配效率
     * 3 高并发时，池化功能更节约内存，减少内存溢出的可能
     * <p>
     * 池化功能是否开启，可以通过下面的系统环境变量来设置 -Dio.netty.allocator.type={unpooled|pooled}
     */
    private static void createTest03() {
        // 创建池化基于堆的ByteBuf
        ByteBuf heapBuffer = ByteBufAllocator.DEFAULT.heapBuffer();
        // 创建池化基于直接内存的ByteBuf
        ByteBuf directBuffer = ByteBufAllocator.DEFAULT.directBuffer();

        System.out.println(heapBuffer);
        System.out.println(directBuffer);

    }

    /**
     * 测试 写入
     */
    private static void writeTest() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        logBuf(buffer);

        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        logBuf(buffer);

        buffer.writeInt(5); // int占用四个字节
        logBuf(buffer);

        buffer.setByte(4,12); // set方法不会改变指针位置
        logBuf(buffer);
    }

    /**
     * 测试 扩容
     *
     * 扩容规则
     */
    private static void resizeTest() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        logBuf(buffer);

        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        logBuf(buffer);

        buffer.writeInt(5); // int占用四个字节
        logBuf(buffer);

        buffer.writeInt(5);
        buffer.writeInt(5);
        logBuf(buffer);

        buffer.writeInt(5);
        buffer.writeInt(5);
        buffer.writeInt(5);
        logBuf(buffer);
    }

    /**
     * 测试 读取
     * 读过的内容 就属于废弃部分了，再读只能读取那些尚未读取的部分
     */
    public static void readTest() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeByte(1);
        buffer.writeByte(2);
        buffer.writeByte(3);
        buffer.writeByte(4);
        buffer.writeByte(5);
        buffer.writeByte(6);
        logBuf(buffer);

        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
    }

    /**
     * 重复读
     *
     * 前面说到了 读取后就成了废弃的部分，但是可以使用mark和reset来重复读取
     */
    public static void repeatedRead() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeByte(1);
        buffer.writeByte(2);
        buffer.writeByte(3);
        buffer.writeByte(4);
        buffer.writeByte(5);
        buffer.writeByte(6);
        logBuf(buffer);

        // 读了四个
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        // 在读第二个元素后mark标记一下
        buffer.markReaderIndex();

        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());

        System.out.println("===============");

        buffer.resetReaderIndex();
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
        System.out.println(buffer.readByte());
    }

    /**
     * 测试buffer切片
     * 并没有发生内存复制，还是原来的内存
     */
    public static void sliceTest() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{1,2,3,4,5,6});
        logBuf(buffer);

        // 无参 slice 是从原始 ByteBuf 的 read index 到 write index 之间的内容进行切片，
        // 切片后的 max capacity 被固定为这个区间的大小，因此不能追加 write
//        System.out.println(buffer.readByte());
//        ByteBuf slice = buffer.slice();
//        logBuf(slice);
//        slice.writeByte(1); // 会报错

        ByteBuf buffer01 = buffer.slice(0, 5);
        ByteBuf buffer02 = buffer.slice(5, 5);

        logBuf(buffer01);
        logBuf(buffer02);

        // 修改buffer01的数据
        buffer01.setByte(2,100);
        logBuf(buffer01);
        logBuf(buffer);
    }

    /**
     * duplicate
     * 【零拷贝】的体现之一，就好比截取了原始 ByteBuf 所有内容，
     * 并且没有 max capacity 的限制，也是与原始 ByteBuf 使用同一块底层内存，只是读写指针是独立的
     */
    public static void duplicateTest() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes(new byte[]{1,2,3,4,5,6});
        logBuf(buffer);

        ByteBuf duplicate = buffer.duplicate();
        logBuf(duplicate);

        System.out.println(buffer);
        System.out.println(duplicate);
    }

    /**
     * CompositeByteBuf
     * 【零拷贝】的体现之一，可以将多个 ByteBuf 合并为一个逻辑上的 ByteBuf，避免拷贝
     */
    public static void compositeByteBufTest() {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer(5);
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});
        System.out.println(ByteBufUtil.prettyHexDump(buf1));
        System.out.println(ByteBufUtil.prettyHexDump(buf2));

        CompositeByteBuf buf3 = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf3.addComponents(true, buf1, buf2);

        logBuf(buf3);
    }


    public static void main(String[] args) {
//        createTest01();
//        createTest02();
//        createTest03();
//        writeTest();
//        resizeTest();
//        readTest();
//        repeatedRead();
//        sliceTest();
//        duplicateTest();
        compositeByteBufTest();
    }

}
