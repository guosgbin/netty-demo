package cn.guosgbin.nio.path;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 测试Path
 *
 * Path代表文件和文件夹的路径
 * Paths是工具类，用来获取Path实例
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/23 23:40
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class PathTest {
    public static void main(String[] args) {
        /*
        d:
            |- data
                |- projects
                    |- a
                    |- b
         */

        Path path = Paths.get("d:\\data\\projects\\a\\..\\b");
        System.out.println(path);
        System.out.println(path.normalize()); // 正常化路径
    }
}
