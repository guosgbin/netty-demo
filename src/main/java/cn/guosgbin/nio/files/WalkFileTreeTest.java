package cn.guosgbin.nio.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 测试walkFileTree
 *
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/6/24 8:15
 * <p>
 * 古之立大事者，不惟有超世之才，亦必有坚忍不拔之志——苏轼
 */
public class WalkFileTreeTest {
    /**
     * 遍历文件夹
     */
    private static void foreachDir() throws IOException {
        AtomicInteger adder1 = new AtomicInteger();
        AtomicInteger adder2 = new AtomicInteger();

        Files.walkFileTree(Paths.get("D:\\IdeaWorkspace\\demo\\netty-demo - 副本"), new SimpleFileVisitor<Path>(){

            /**
             * 进入文件夹前
             */
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println("访问到的文件夹前----> " + dir);
                adder1.incrementAndGet();
                return super.preVisitDirectory(dir, attrs);
            }

            /**
             * 访问文件
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                System.out.println(file);
                adder2.incrementAndGet();
                return super.visitFile(file, attrs);
            }
        });

        System.out.println("文件夹个数为: " + adder1);
        System.out.println("文件个数为: " + adder2);
    }

    /**
     * 查找.class文件的个数
     */
    public static void queryClassCount() throws IOException {
        AtomicInteger adder1 = new AtomicInteger();
        Files.walkFileTree(Paths.get("D:\\IdeaWorkspace\\demo\\netty-demo - 副本"), new SimpleFileVisitor<Path>() {
            /**
             * 访问文件
             */
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileStr = file.toString();
                if (fileStr.endsWith(".class")) {
                    adder1.incrementAndGet();
                    System.out.println(file);
                }
                return super.visitFile(file, attrs);
            }
        });
        System.out.println("文件个数为: " + adder1);
    }


    /**
     * 删除多级目录
     */
    public static void deleteDirectories() throws IOException {
        Files.walkFileTree(Paths.get("D:\\IdeaWorkspace\\demo\\netty-demo - 副本"), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                System.out.println("文件 ---> " + file);
                // 删除每次访问的文件
                Files.delete(file);
                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                System.out.println("退出文件夹 ---> " + dir);
                // 退出文件夹时删除文件夹
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        });
    }

    public static void main(String[] args) throws IOException {

        deleteDirectories();
    }
}
