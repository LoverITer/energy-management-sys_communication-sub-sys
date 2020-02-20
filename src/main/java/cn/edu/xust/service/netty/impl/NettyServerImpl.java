package cn.edu.xust.service.netty.impl;

import cn.edu.xust.service.netty.NettyAsyncService;
import cn.edu.xust.service.netty.handler.NettyServerDefaultHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;


/**
 * Netty服务器端，在SpringBoot上必须异步执行，否者会阻塞main主线程导致其他服务不可用
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 16:12
 */
@Service
@Slf4j
public class NettyServerImpl implements NettyAsyncService, ApplicationRunner {

    @Autowired
    private NettyServerDefaultHandler nettyServerDefaultHandler;
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    @Value("${netty.server.port}")
    private  int port;


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
                         * 每一个客户端连接上门来后都会执行这个方法，主要用于设置IO流的编码、解码以及IO事件的处理器等
                         * @param socketChannel
                         * @throws Exception
                         */
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(nettyServerDefaultHandler);
                            log.info("client" + socketChannel.remoteAddress() + " connected successfully！");
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            if (channelFuture.isSuccess()) {
                log.info("Netty Server started successfully, listening on " + channelFuture.channel().localAddress());
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


    @PreDestroy
    @Override
    public void destroy() {
        bossGroup.shutdownGracefully().syncUninterruptibly();
        workerGroup.shutdownGracefully().syncUninterruptibly();
        System.out.println("Netty has been closed!");
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.connect();
    }
}
