package cn.guosgbin.cast.netty.component.future;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 测试netty的Promise
 *
 * @author: Dylan kwok GSGB
 * @date: 2021/7/5 8:39
 * <p>
 * 古之立大事者，不惟有 超世之才，亦必有坚忍不拔之志——苏轼
 */
@Slf4j
public class TestPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        test06();
    }

    /**
     * await 死锁检查 ???
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void test06() throws ExecutionException, InterruptedException {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        eventExecutors.submit(() -> {
            System.out.println("1");
            try {
                promise.await();
                // 注意不能仅捕获 InterruptedException 异常
                // 否则 死锁检查抛出的 BlockingOperationException 会继续向上传播
                // 而提交的任务会被包装为 PromiseTask，它的 run 方法中会 catch 所有异常然后设置为 Promise 的失败结果而不会抛出
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("2");
        });
        eventExecutors.submit(() -> {
            System.out.println("3");
            try {
                promise.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("4");
        });
    }

    /**
     * 异步处理任务失败
     */
    private static void test05() throws ExecutionException, InterruptedException {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        promise.addListener(future -> {
            log.debug("result {}", (promise.isSuccess() ? promise.getNow() : promise.cause()).toString());
        });

        eventExecutors.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RuntimeException e = new RuntimeException("error...");
            log.debug("set failure, {}", e.toString());
            promise.setFailure(e);
        });

        log.debug("start...");
    }


    /**
     * 同步处理任务失败 - await
     */
    private static void test04() throws ExecutionException, InterruptedException {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        eventExecutors.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RuntimeException e = new RuntimeException("error...");
            log.debug("set failure, {}", e.toString());
            promise.setFailure(e);
        });

        log.debug("start...");
        log.debug("{}", promise.getNow());
        promise.await(); // 与 sync 和 get 区别在于，不会抛异常
        log.debug("result {}", (promise.isSuccess() ? promise.getNow() : promise.cause()).toString());
    }


    /**
     * 同步处理任务失败 - sync & get
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void test03() throws ExecutionException, InterruptedException {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        eventExecutors.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            RuntimeException e = new RuntimeException("error...");
            log.debug("set failure, {}", e.toString());
            promise.setFailure(e);
        });

        log.debug("start...");
        log.debug("{}", promise.getNow());
        promise.get(); // sync() 也会出现异常，只是 get 会再用 ExecutionException 包一层异常
    }

    /**
     * 异步获取结果
     */
    private static void test02() {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        // 设置回调，异步接收结果
        promise.addListener(future -> {
            // 这里的 future 就是上面的 promise
            log.debug("{}", future.getNow());
        });

        // 等待 1000 后设置成功结果
        eventExecutors.execute(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("set success, {}", 10);
            promise.setSuccess(10);
        });

        log.debug("start...");
    }


    /**
     * 同步获取结果
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private static void test01() throws InterruptedException, ExecutionException {
        DefaultEventLoop eventExecutors = new DefaultEventLoop();
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventExecutors);

        eventExecutors.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {

            }
            log.debug("set success, {}", 10);
            promise.setSuccess(10);
        });

        log.debug("开始...");
        log.debug("{}", promise.getNow());
        log.debug("{}", promise.get());
    }
}
