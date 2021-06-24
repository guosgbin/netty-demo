package cn.guosgbin.nio.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 测试walk方法
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/24 8:37
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class WalkTest {
    public static void main(String[] args) throws IOException {
        // 拷贝文件夹


        long start = System.currentTimeMillis();
        // 旧目录
        String oldDir = "D:\\IdeaWorkspace\\demo\\netty-demo - 副本";
        // 新目录
        String newDir = "D:\\IdeaWorkspace\\demo\\netty-demo - 副本222222222";


        Files.walk(Paths.get("D:\\IdeaWorkspace\\demo\\netty-demo - 副本")).forEach(
                path -> {
                    try {
                        String newPath = path.toString().replace(oldDir, newDir);

                        // 假如是目录
                        if (Files.isDirectory(path)) {
                            // 创建目录
                            Files.createDirectory(Paths.get(newPath));
                        }
                        // 假如是文件
                        else if(Files.isRegularFile(path)) {
                            // 拷贝文件
                            Files.copy(path, Paths.get(newPath));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        );
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }
}
