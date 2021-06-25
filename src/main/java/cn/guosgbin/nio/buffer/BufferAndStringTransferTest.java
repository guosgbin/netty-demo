package cn.guosgbin.nio.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 测试Buffer和String的转换
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 8:15
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class BufferAndStringTransferTest {
    public static void main(String[] args) {

        // String -> buffer
        // 1.StandardCharsets
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("halo");
        // 2.wrap方法
        ByteBuffer buffer2 = ByteBuffer.wrap("halo".getBytes());
        // 3.Charset
        ByteBuffer buffer3 = Charset.defaultCharset().encode("halo");

        // 4.直接buffer的put方法
        ByteBuffer buffer4 = ByteBuffer.allocate(10);
        buffer4.put("halo".getBytes());

        // buffer -> String
        // 1.StandardCharsets
        String halo1 = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(halo1);

        // 2 Charset
        String halo3 = Charset.defaultCharset().decode(buffer3).toString();
        System.out.println(halo3);

        //
        buffer4.flip();
        String halo4 = StandardCharsets.UTF_8.decode(buffer4).toString();
        System.out.println(halo4);


    }
}
