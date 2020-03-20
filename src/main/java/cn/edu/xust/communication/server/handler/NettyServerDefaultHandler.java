package cn.edu.xust.communication.server.handler;


import cn.edu.xust.bean.ElectricMeter;
import cn.edu.xust.communication.Dlt6452007AmmeterReader;
import cn.edu.xust.communication.protocol.Dlt645Frame;
import cn.edu.xust.communication.server.NettyServer;
import cn.edu.xust.communication.util.Dlt645FrameUtils;
import cn.edu.xust.communication.util.HexConverter;
import cn.edu.xust.service.ElectricMeterService;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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


    @Autowired
    private ElectricMeterService electricMeterServiceImpl;

    private boolean sechdelTaskLunch = true;

    /**
     * 将当前连接上的客户端连接存入map实现控制设备下发控制参数
     * key 存通道的ip , value 是服务器和客户端之间的通信通道
     */
    private static ConcurrentHashMap<String, Channel> devices = new ConcurrentHashMap<>();

    /**
     * 当通道读取电表的数据后首先将原始数据放入到此Map中，等待程序的后续程序
     * key 存通道的ip ,value 存电表当前的读取到的最新数据
     */
    private static ConcurrentHashMap<String, ElectricMeter> ammeters = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Channel> getDevicesMap() {
        return devices;
    }

    public static ConcurrentHashMap<String, ElectricMeter> getAmmeterMap() {
        return ammeters;
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
        /*将设备的请求地址当作 map 的key*/
        String remoteAddress = socketChannel.remoteAddress().toString();
        if (!devices.containsKey(remoteAddress)) {
            devices.put(remoteAddress, ctx.channel());
            log.info("client" + socketChannel.remoteAddress().toString() + " connected successful！Current connections " + devices.size());
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
        try {
            ByteBuf buffer = (ByteBuf) msg;
            //客户端IP
            String remoteAddress = ctx.channel().remoteAddress().toString();
            byte[] bytes = new byte[buffer.readableBytes()];
            //复制内容到字节数组
            buffer.readBytes(bytes);
            String hexString = HexConverter.receiveHexToString(bytes);
            System.out.println("hexString:" + hexString);
            System.out.println("RECV HEX FROM：" + remoteAddress + ">\n" + HexConverter.fillBlank(hexString));
            ElectricMeter ammeter = new ElectricMeter();
            //从响应数据中解析出设备的ID
            String ammeterId = Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString);
            ammeter.setElectricMeterId(ammeterId);
            ammeter.setElectricityIp(remoteAddress);
            if (sechdelTaskLunch) {
                sechdelTaskLunch = false;
                //15分钟自动执行一次采集操作
                ctx.channel().eventLoop().schedule(() -> {
                    Dlt6452007AmmeterReader reader = new Dlt6452007AmmeterReader(remoteAddress, ammeterId);
                    //解析数据
                    reader.start();
                }, 15, TimeUnit.MINUTES);
            }
            ammeters.put(remoteAddress, ammeter);
            //及时更新数据库中对应表的消息
            int ret = electricMeterServiceImpl.updateByElectricMeterIdSelective(ammeter);
            if (ret == 0) {
                //尝试更新发现在数据库没有该设别的记录时，新增一个设备
                electricMeterServiceImpl.add(ammeter);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
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

}
