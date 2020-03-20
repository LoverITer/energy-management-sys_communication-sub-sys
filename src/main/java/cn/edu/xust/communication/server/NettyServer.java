package cn.edu.xust.communication.server;

import cn.edu.xust.communication.exception.NotFoundDeviceException;
import cn.edu.xust.communication.server.handler.NettyServerDefaultHandler;
import cn.edu.xust.communication.util.HexConverter;
import cn.edu.xust.service.ElectricMeterService;
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
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Netty服务器端，在SpringBoot上须异步执行，否者会阻塞main主线程导致其他服务不可用
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 16:12
 */
@Component
@Slf4j
public class NettyServer implements NettyAsyncService,
        ApplicationRunner, ApplicationListener<ContextClosedEvent> {

    @Autowired
    private NettyServerDefaultHandler nettyServerDefaultHandler;
    @Autowired
    private ElectricMeterService electricMeterServiceImpl;
    /**监听的端口:可以在SpringBoot配置文件中配置*/
    @Value("${netty.server.port}")
    private int port;
    /** bossGroup：负责处理连接请求的线程组*/
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    /** worker：负责处理具体I/O时间的线程组*/
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
    public static boolean writeCommand(String deviceIp, String cmd) {
        if (Objects.isNull(deviceIp) || Objects.isNull(NettyServerDefaultHandler.getDevicesMap())) {
            //没有找到对应的设备,抛出异常
            throw new NotFoundDeviceException();
        }
        if(Objects.isNull(cmd)){
            throw new IllegalArgumentException("param cmd can not be null.");
        }
        Channel channel = NettyServerDefaultHandler.getDevicesMap().get(deviceIp);
        if (Objects.nonNull(channel)) {
           return new NettyServer().writeMessage2Client(channel, cmd);
        } else {
            throw new NotFoundDeviceException();
        }

    }

    /**
     * 向客户端写数据
     *
     * @param channel 服务器和设备建立的通道
     * @param hexMsg  命令信息
     */
    private boolean writeMessage2Client(Channel channel, String hexMsg) {
        ByteBuf byteBuf = Unpooled.buffer();
        AtomicBoolean isSuccess= new AtomicBoolean(false);
        byteBuf.writeBytes(HexConverter.hexString2ByteArray(hexMsg));
        channel.writeAndFlush(byteBuf).addListener((ChannelFutureListener) channelFuture -> {
            String remoteAddress = channel.remoteAddress().toString();
            if (channelFuture.isSuccess()) {
                isSuccess.set(true);
                System.out.println("SEND HEX TO " + remoteAddress + ">\n" + hexMsg);
            } else {
                System.err.println("SEND HEX TO " + remoteAddress + "FAILURE");
            }
        });
        return isSuccess.get();
    }

}
