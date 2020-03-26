package cn.edu.xust.communication.config.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

/**
 * 线程池
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/27 08:58
 */
@SpringBootConfiguration
@Slf4j
public class ServiceThreadPool {

    /**获取神设备CPU个数*/
    private static final int AVAILABLE_CPU_COUNT = Runtime.getRuntime().availableProcessors();
    /**核心线程数*/
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(AVAILABLE_CPU_COUNT - 1, 4));
    /**最大线程数*/
    private static final int MAX_POOL_SIZE = AVAILABLE_CPU_COUNT * 2 + 1;
    /**工作线程空闲后，保持存活时间*/
    private static final Long TTL = 30L;
    /**任务过多后，存储任务的一个阻塞队列*/
    private BlockingQueue<Runnable> workQueue = new SynchronousQueue<>();
    /**线程池任务满载后采取的任务拒绝策略*/
    private RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.CallerRunsPolicy();

    @Bean
    public Executor executor() {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("service-task-%d").build();
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                TTL,
                TimeUnit.SECONDS,
                workQueue,
                factory,
                rejectHandler);
    }

}
