package cn.edu.xust.service.netty.handler;


import cn.edu.xust.bean.ElectricMeter;
import cn.edu.xust.service.ElectricMeterService;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
     * 将当前客户端连接存入map实现控制设备下发参数
     */
    private static ConcurrentHashMap<String, ChannelHandlerContext> map = new ConcurrentHashMap<>();
    /**
     * 成功失败标志的16进制码
     */
    private static final String SUCCESS_FLAG_HEX = "73 75 63 63 65 73 73 ";
    private static final String FAILURE_FLAG_HEX = "66 61 69 6c 75 72 65 ";
    @Autowired
    private ElectricMeterService electricMeterServiceImpl;


    /**
     * 通道有读取事件时触发这个方法
     *
     * @param ctx 上下文对象，内部封装了许多有用的信息
     * @param msg 客户端发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        ByteBuf readMessage = (ByteBuf) msg;
        //解析客户端json 数据为电表对象
        ElectricMeter electricMeter = JSONObject.parseObject(readMessage.toString(CharsetUtil.UTF_8), ElectricMeter.class);
        System.out.println(electricMeter.toString());

        //设备请求地址（将设备的请求地址当作 map 的key），取到的值为客户端的 ip+端口号
        String url = ctx.channel().remoteAddress().toString();
        System.out.println(url + ": " + readMessage.toString(CharsetUtil.UTF_8));

        //当url为空的时候将当前的设备ip+端口存进map，当做下发设备的标识的key
        if (Objects.isNull(url)) {
            map.put(url, ctx);
        }
        //设备请求的服务器端的地址用作监听设备请求的那个端口
        String servicePort = ctx.channel().localAddress().toString();
        //向数据库写数据
        ///autoGenerationTable.create();
        //尝试根据电表Id更新电表数据
        int result = electricMeterServiceImpl.updateByElectricMeterIdSelective(electricMeter);

        if (result <= 0) {
            //更新失败就新增一条记录
            result = electricMeterServiceImpl.add(electricMeter);
        }
        System.out.println("向：" + servicePort.substring(servicePort.length() - 4, servicePort.length()) + " 端口写入数据");
        if (result > 0) {
            //返回成功的信息
            writeCallBackMessage(ctx, SUCCESS_FLAG_HEX);
            log.debug("写入数据成功");
        } else {
            //返回失败的信息
            writeCallBackMessage(ctx, FAILURE_FLAG_HEX);
            log.error("写入数据失败");
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
        //异步的关闭和客户端的通信通道，以免浪费资源
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
        cause.printStackTrace();
        ctx.close();
    }


}
