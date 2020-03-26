package cn.edu.xust.communication.server.handler;


import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.config.ApplicationContextHolder;
import cn.edu.xust.communication.config.executor.ServiceThreadPool;
import cn.edu.xust.communication.model.Result;
import cn.edu.xust.communication.protocol.Dlt645Frame;
import cn.edu.xust.communication.server.HashedWheelReader;
import cn.edu.xust.communication.server.NettyServer;
import cn.edu.xust.communication.server.cache.ChannelMap;
import cn.edu.xust.communication.util.HexConverter;
import cn.edu.xust.mapper.AmmeterParameterMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 服务器逻辑，负责用于监听、处理各种I/O事件，并将设备传上来的数据写进数据库
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 16:54
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class NettyServerDefaultHandler extends ChannelInboundHandlerAdapter {

    /**
     * 闭锁，用于异步等待客户端的消息
     */
    private CountDownLatch latch;
    /**
     * 消息唯一标志
     */
    private String messageUuid = "";
    /**
     * 同步标志
     */
    private int rec;
    /**
     * 客户端返回的结果
     */
    private Result result;


    public NettyServerDefaultHandler() {

    }

    public void resetSync(CountDownLatch latch, int rec) {
        this.latch = latch;
        this.rec = rec;
    }

    public void setUuidId(String s) {
        this.messageUuid = s;
    }

    public Result getResult() {
        return result;
    }

    /**
     * 在客户端和服务器首次建立通信通道的时候触发次方法
     *
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel socketChannel = ctx.channel();
        /*将设备remoteAddress当作 map 的key*/
        String remoteAddress = socketChannel.remoteAddress().toString();
        if (ChannelMap.getDevicesMap(remoteAddress) != null) {
            ChannelMap.setDevicesMap(remoteAddress, ctx.channel());
            log.info("client" + socketChannel.remoteAddress().toString() + " connected successful！Online: " + ChannelMap.currentOnlineDevicesNum());
            ChannelMap.setChannelLock(remoteAddress,new ReentrantLock());
            NettyServer.writeCommand(ctx.channel().remoteAddress().toString(), Dlt645Frame.BROADCAST_FRAME);
        }
    }

    /**
     * 通道有读取事件时触发这个方法
     *
     * @param ctx 上下文对象，内部封装了许多有用的信息
     * @param msg 客户端发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Object executor = ApplicationContextHolder.getBean("asyncServiceExecutor");
        if (executor instanceof ServiceThreadPool) {
            ((ServiceThreadPool) executor).executor().execute(new DataAnalyzeTask(ctx,msg));
        }
    }


    /**
     * 数据解析任务 DataAnalyzeTask
     */
    static class DataAnalyzeTask implements Runnable {

        /**
         * 通达上下文对象
         */
        private ChannelHandlerContext ctx;
        /**
         * 消息
         */
        private Object msg;
        /**
         * 电表号
         */
        private String ammeterId;
        /**
         * 开启定时采集任务标志
         */
        private boolean timingTaskLunch = true;

        private DataAnalyzeTask(ChannelHandlerContext ctx, Object msg) {
            this.ctx = ctx;
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                ByteBuf buffer = (ByteBuf) msg;
                //客户端IP
                String remoteAddress = ctx.channel().remoteAddress().toString();
                byte[] bytes = new byte[buffer.readableBytes()];
                //复制内容到字节数组
                buffer.readBytes(bytes);
                String hexString = HexConverter.receiveHexToString(bytes);
                //解析帧结构
                System.out.println("RECV HEX FROM：" + remoteAddress + ">\n" + HexConverter.fillBlank(hexString));
                AmmeterParameter ammeterParameter = new Dlt645Frame().analysis(hexString);
                if (ammeterParameter != null && timingTaskLunch) {
                    ammeterId = ammeterParameter.getDeviceNumber();
                    //5分钟自动执行一次采集操作
                    new HashedWheelReader().executePer5Min(remoteAddress, ammeterParameter.getDeviceNumber());
                    timingTaskLunch = false;
                }
            } catch (Exception e) {
                log.error("server error:" + e.getMessage());
            }
        }
    }


    /**
     * 通道的数据读取完毕后就会触发此方法
     *
     * @param ctx 上下文对象，内部封装了许多有用的信息
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //刷新缓存区
        ctx.flush();
    }


    /**
     * 通道断开事件触发此方法
     *
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端 " + ctx.channel().remoteAddress() + "断开了连接");
        ChannelMap.removeDeviceFormMap(ctx.channel().remoteAddress().toString());
        /*异步的关闭和客户端的通信通道，以免浪费资源*/
        ctx.channel().closeFuture().sync();
    }

    /**
     * 发生异常时触发此方法
     *
     * @param ctx   上下文对象，内部封装了许多有用的信息
     * @param cause 异常信息
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ChannelMap.removeDeviceFormMap(ctx.channel().remoteAddress().toString());
        ctx.close();
    }

    public static void logException(Exception e, String ammeterId, String ammeterStatus) {
        Object mapper = ApplicationContextHolder.getBean("ammeterParamMapper");
        if (mapper instanceof AmmeterParameterMapper) {
            AmmeterParameterMapper ammeterParameterMapper = (AmmeterParameterMapper) mapper;
            AmmeterParameter ammeterParameter = new AmmeterParameter();
            ammeterParameter.setDeviceNumber(ammeterId);
            ammeterParameter.setAmmeterStatus(ammeterStatus);
            int ret = ammeterParameterMapper.updateSelective(ammeterParameter);
            if (ret <= 0) {
                ammeterParameterMapper.insertSelective(ammeterParameter);
            }
        }
        e.printStackTrace();
    }

}
