package org.imusic.netty.util;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private static final ExecutorService executor;
    
    static {
        // 创建有界线程池（避免无限制创建线程）
        executor = new ThreadPoolExecutor(
            4, // 核心线程数
            16, // 最大线程数
            60, TimeUnit.SECONDS, // 空闲线程存活时间
            new LinkedBlockingQueue<>(100), // 任务队列容量
            new ThreadPoolExecutor.CallerRunsPolicy() // 拒绝策略（由调用线程直接运行）
        );
    }
    
    public static void execute(Runnable task) {
        executor.execute(task);
    }
    
    public static void shutdown() {
        executor.shutdown();
    }

    /**
     * 提交 Runnable 任务（无返回值）
     */
    public static Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    /**
     * 提交 Runnable 任务（可指定返回结果）
     */
    public static <T> Future<T> submit(Runnable task, T result) {
        return executor.submit(task, result);
    }

    /**
     * 提交 Callable 任务（有返回值）
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
}