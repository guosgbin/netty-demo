package cn.guosgbin.nio.files;

import java.io.IOException;
import java.nio.file.*;

/**
 * 测试Files
 *
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 23:45
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class FilesTest {
    public static void main(String[] args) throws IOException {
        // 文件是否存在
//        Path path = Paths.get("helloword/data.txt");
//        System.out.println(Files.exists(path));

        // 创建一级目录
        // 如果目录已存在，会抛异常 FileAlreadyExistsException
        // 不能一次创建多级目录，否则会抛异常 NoSuchFileException
//        Path path = Paths.get("helloword/d1");
//        Path path = Paths.get("helloword");
//        Files.createDirectory(path);

        // 创建多级目录
//        Path path = Paths.get("helloword/d1/d2");
//        Files.createDirectories(path);

        // 拷贝文件
        // 如果文件已存在，会抛异常 FileAlreadyExistsException
        // 如果希望用 source 覆盖掉 target，需要用 StandardCopyOption 来控制
//        Path source = Paths.get("data.txt");
//        Path target = Paths.get("toto.txt");

//        Files.copy(source, target);
//        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        // 移动文件
        // StandardCopyOption.ATOMIC_MOVE 保证文件移动的原子性
//        Path source = Paths.get("data.txt");
//        Path target = Paths.get("helloword/data.txt");
//
//        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);

        // 删除文件和目录
        // 如果目录还有内容，会抛异常 DirectoryNotEmptyException
        // 如果文件不存在，会抛异常 NoSuchFileException
//        Path target = Paths.get("helloword");
//
//        Files.delete(target);



    }
}
