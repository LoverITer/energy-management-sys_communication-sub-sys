package cn.edu.xust.communication.server;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.AmmeterAutoReader;
import cn.edu.xust.communication.config.ApplicationContextHolder;
import cn.edu.xust.communication.exception.NotFoundDeviceException;
import cn.edu.xust.communication.model.Result;
import cn.edu.xust.communication.server.cache.ChannelMap;
import cn.edu.xust.communication.server.handler.NettyServerDefaultHandler;
import cn.edu.xust.communication.util.HexConverter;
import cn.edu.xust.communication.util.RedisUtils;
import cn.edu.xust.mapper.AmmeterParameterMapper;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;


/**
 * Netty服务器端，在SpringBoot上须异步执行，否者会阻塞main主线程导致其他服务不可用
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 16:12
 */
@Component(value = "NettyServer")
@Slf4j
public class NettyServer implements NettyAsyncService,
        ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    @Autowired
    private NettyServerDefaultHandler nettyServerDefaultHandler;

    /**
     * 监听的端口:可以在SpringBoot配置文件中配置
     */
    @Value("${netty.server.port}")
    private int port;
    /**
     * bossGroup：负责处理连接请求的线程组
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /**
     * worker：负责处理具体I/O时间的线程组
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup();


    @Async(value = "asyncServiceExecutor")
    @Override
    public void connect() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**
                         * 每一个客户端连接上来后都会执行这个方法，主要用于设置IO流的编码、解码以及IO事件的处理器等
                         * @param socketChannel
                         * @throws Exception
                         */
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(nettyServerDefaultHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if (channelFuture.isSuccess()) {
                log.info("Netty Server started successful, listening on " + channelFuture.channel().localAddress());
            }
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //出现异常时关闭两个线程组
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }


    @Override
    public void destroy() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        System.out.println("Netty has been closed!");
    }


    /**
     * SpringBoot启动的时候启动Netty服务端开始监听
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.connect();
    }

    /**
     * SpringBoot关闭的时候销毁Netty的两个线程组
     *
     * @param contextClosedEvent
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        this.destroy();
    }


    /**
     * 执行设备控制，向设备发送命令
     *
     * @param deviceIp 设备ID
     * @param cmd      下发的命令
     */
    public static Result writeCommand(String deviceIp, String cmd, String uuid) {
        if (Objects.isNull(deviceIp) || Objects.isNull(ChannelMap.getDeviceFromMap(deviceIp))) {
            //没有找到对应的设备,抛出异常
            throw new NotFoundDeviceException();
        }
        if (Objects.isNull(cmd)) {
            throw new IllegalArgumentException("param cmd can not be null.");
        }
        ChannelHandlerContext ctx = ChannelMap.getDeviceFromMap(deviceIp);
        if (Objects.nonNull(ctx)) {
            return new NettyServer().writeMessage2Client(ctx, cmd, uuid);
        }
        return new Result(0, "Client is closed!!", null);
    }

    /**
     * 向客户端写数据,并且同步等待客户端返回消息
     *
     * @param ctx    服务器和设备建立的通道
     * @param hexMsg 命令信息
     * @param uuid   消息的唯一编号
     */
    private Result writeMessage2Client(ChannelHandlerContext ctx, String hexMsg, String uuid) {
        Channel channel = ctx.channel();
        Lock lock = ChannelMap.getChannelLock(channel.remoteAddress().toString());
        lock.lock();
        try {
            if (channel.isOpen()) {
                //设置同步
                CountDownLatch latch = new CountDownLatch(1);
                ChannelHandler channelHandler = ctx.handler();
                if (channelHandler instanceof NettyServerDefaultHandler) {
                    ((NettyServerDefaultHandler) channelHandler).resetSync(latch, 1);
                    ((NettyServerDefaultHandler) channelHandler).setUnidId(uuid);
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.writeBytes(HexConverter.hexString2ByteArray(hexMsg));
                    channel.writeAndFlush(byteBuf).addListener((ChannelFutureListener) channelFuture -> {
                        String remoteAddress = channel.remoteAddress().toString();
                        if (channelFuture.isSuccess()) {
                            System.out.println("SEND HEX TO " + remoteAddress + ">\n" + hexMsg);
                        } else {
                            System.err.println("SEND HEX TO " + remoteAddress + "FAILURE");
                        }
                    });
                    if (latch.await(30, TimeUnit.SECONDS)) {
                        System.out.println(((NettyServerDefaultHandler) channelHandler).getResult().getMessage());
                        return ((NettyServerDefaultHandler) channelHandler).getResult();
                    }
                    //如果超时，将超时标志设置为1
                    log.error("Request timeout 60s");
                }
                return new Result(2, "Request timeout!", null);
            } else {
                return new Result(0, "Client is closed!!", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(0, "Internal server error!", null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 把数据刷新到数据库
     *
     * @param ammeterParameter 参数对象
     */
    public void flushData2DataBase(AmmeterParameter ammeterParameter) {
        Object mapper = ApplicationContextHolder.getBean("ammeterParamMapper");
        if (mapper instanceof AmmeterParameterMapper) {
            AmmeterParameterMapper ammeterParamMapper = (AmmeterParameterMapper) mapper;
            int ret = ammeterParamMapper.updateSelective(ammeterParameter);
            if (ret <= 0) {
                ammeterParamMapper.insertSelective(ammeterParameter);
            }
        }
    }

    /**
     * 把数据刷新到Redis
     *
     * @param ammeterParameter 参数对象
     */
    public void flushData2Redis(AmmeterParameter ammeterParameter) {
        Object redisUtils = ApplicationContextHolder.getBean("redisUtils");
        if (redisUtils instanceof RedisUtils) {
            String methodName = AmmeterAutoReader.getExecutedMethodQueue().poll();
            if (Objects.nonNull(methodName)) {
                //key:接口名
                ((RedisUtils) redisUtils).set(methodName, JSON.toJSON(ammeterParameter), RedisUtils.DB_0);
            }
        }
    }

}
