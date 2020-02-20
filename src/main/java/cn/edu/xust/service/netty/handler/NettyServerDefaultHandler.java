package cn.edu.xust.service.netty.handler;


import cn.edu.xust.bean.ElectricMeter;
import cn.edu.xust.service.ElectricMeterService;
import cn.edu.xust.service.netty.NettyServer;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.SocketHandler;

/**
 * Netty服务端业务处理handler，用于监听、处理各种I/O事件，并将设备传上来的数据写进数据库
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/02/18 16:54
 */
@Component
@Sharable
@Slf4j
public class NettyServerDefaultHandler extends ChannelInboundHandlerAdapter {


    /**
     * 成功失败标志的16进制码
     */
    private static final String SUCCESS_FLAG_HEX = "73 75 63 63 65 73 73 ";
    private static final String FAILURE_FLAG_HEX = "66 61 69 6c 75 72 65 ";
    @Autowired
    private ElectricMeterService electricMeterServiceImpl;
    /**
     * 将当前连接上的客户端连接存入map实现控制设备下发控制参数
     */
    private static ConcurrentHashMap<String, Channel> devices = new ConcurrentHashMap<>();


    /**
     * 在客户端和服务器首次建立通信通道的时候触发次方法
     *
     * @param ctx 上下文
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel socketChannel = ctx.channel();
        /*将设备的请求地址当作 map 的key*/
        String url = socketChannel.remoteAddress().toString();
        if (!devices.containsKey(url)) {
            devices.put(url, ctx.channel());
            log.info("client" + socketChannel.remoteAddress().toString() + " connected successful！Current connections "+devices.size());
            //连接后和设备进行首次通信确认身份
            writeCallBackMessage(ctx,"Identity confirmation");
        }
    }

    /**
     * 通道有读取事件时触发这个方法
     *
     * @param ctx 上下文对象，内部封装了许多有用的信息
     * @param msg 客户端发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf readMessage = (ByteBuf) msg;
        /*解析客户端json数据生成电表数据对象*/
        ElectricMeter electricMeter = JSONObject.parseObject(readMessage.toString(CharsetUtil.UTF_8), ElectricMeter.class);
        if (Objects.nonNull(electricMeter)) {
            electricMeter.setElectricityIp(ctx.channel().remoteAddress().toString());
            /*尝试根据电表Id更新电表数据*/
            int result = electricMeterServiceImpl.updateByElectricMeterIdSelective(electricMeter);
            if (result <= 0) {
                /*更新没有结果就新增一条记录*/
                result = electricMeterServiceImpl.add(electricMeter);
            }
            String serverPort = ctx.channel().localAddress().toString();
            log.info("向：" + serverPort.substring(serverPort.length() - 4) + " 端口写入数据");
            if (result > 0) {
                log.debug("Data writing to the database successfully.");
            } else {
                log.error("Data writing to the database failed.");
            }
        }
    }


    private void writeCallBackMessage(ChannelHandlerContext ctx, String rmsg) {
        ByteBuf message = Unpooled.copiedBuffer(rmsg.getBytes());
        ctx.writeAndFlush(message);
        ctx.flush();
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
        devices.remove(ctx.channel().remoteAddress().toString());
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
        devices.remove(ctx.channel().remoteAddress().toString().replace('/', ' '));
        ctx.close();
    }


    public static ConcurrentHashMap<String, Channel> getDevicesMap() {
        return devices;
    }


}
