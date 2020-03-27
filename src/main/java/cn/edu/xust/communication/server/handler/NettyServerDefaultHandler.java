package cn.edu.xust.communication.server.handler;


import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.config.ApplicationContextHolder;
import cn.edu.xust.communication.model.Result;
import cn.edu.xust.communication.protocol.Dlt645Frame;
import cn.edu.xust.communication.server.HashedWheelReader;
import cn.edu.xust.communication.server.NettyServer;
import cn.edu.xust.communication.server.cache.ChannelMap;
import cn.edu.xust.communication.util.Dlt645FrameUtils;
import cn.edu.xust.communication.util.HexConverter;
import cn.edu.xust.mapper.AmmeterParameterMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
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
@Slf4j
public class NettyServerDefaultHandler extends ChannelInboundHandlerAdapter {


    /**
     * 电表号
     */
    private String ammeterId;
    /**
     * 开启定时采集任务标志
     */
    private boolean timingTaskLunch = true;
    /**
     * 同步锁
     */
    private CountDownLatch latch;

    /**
     * 消息的唯一ID
     */
    private String unidId = "";
    /**
     * 同步标志
     */
    private int rec;
    /**
     * 客户端返回的结果
     */
    private Result result = new Result();


    public NettyServerDefaultHandler() {

    }


    /**
     * 在客户端和服务器首次建立通信通道的时候触发次方法
     *
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        new Thread(()->{
            Channel socketChannel = ctx.channel();
            /*将设备remoteAddress当作 map 的key*/
            String remoteAddress = socketChannel.remoteAddress().toString();
            if (ChannelMap.getDeviceFromMap(remoteAddress) == null) {
                ChannelMap.setChannelLock(remoteAddress, new ReentrantLock());
                ChannelMap.setDevicesMap(remoteAddress, ctx);
                log.info("client" + socketChannel.remoteAddress().toString() + " connected successful！Online: " + ChannelMap.currentOnlineDevicesNum());
                NettyServer.writeCommand(ctx.channel().remoteAddress().toString(), Dlt645Frame.BROADCAST_FRAME, UUID.randomUUID().toString());
            }
        }).start();

    }

    /**
     * 通道有读取事件时触发这个方法
     *
     * @param ctx 上下文对象，内部封装了许多有用的信息
     * @param msg 客户端发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        new Thread(()->{
            try {
                ByteBuf buffer = (ByteBuf) msg;
                //客户端IP
                String remoteAddress = ctx.channel().remoteAddress().toString();
                byte[] bytes = new byte[buffer.readableBytes()];
                //复制内容到字节数组
                buffer.readBytes(bytes);
                String hexString = HexConverter.receiveHexToString(bytes);
                System.out.println("RECV HEX FROM：" + remoteAddress + ">\n" + HexConverter.fillBlank(hexString));
                result.setMessage(hexString);
                if (timingTaskLunch) {
                    //解析第一次建立连接时的数据解析出ammeterId
                    String ammeterId = Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString);
                    //AmmeterParameter ammeterParameter = new Dlt645Frame().analysis(hexString);
                    ChannelMap.bindDeviceNumAndDeviceSocket(ammeterId, remoteAddress);
                    //5分钟自动执行一次采集操作
                    new HashedWheelReader().executePer5Min(remoteAddress, ammeterId);
                    timingTaskLunch = false;
                }
            } catch (Exception e) {
                log.error("server error:" + e.getMessage());
            } finally {
                if (Objects.nonNull(latch)) {
                    latch.countDown();
                }
                rec = 0;
            }
        }).start();

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

    public void resetSync(CountDownLatch latch, int rec) {
        this.latch = latch;
        this.rec = rec;
    }

    public void setUnidId(String s) {
        this.unidId = s;
    }

    public Result getResult() {
        return result;
    }

}
