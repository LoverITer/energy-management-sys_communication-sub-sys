package cn.edu.xust.communication.server;

/**
 * Netty异步任务接口，Netty任务必须异步的执行，不然会阻塞SpringBoot主线程
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/19 10:05
 */
public interface NettyAsyncService {

    /**
     * 在9000端口启动Netty服务端并监听以及处理客户端的连接请求和I/O事件
     */
    void connect();

    /**
     * 关闭应用的时候关闭Netty
     */
    void destroy();
}
