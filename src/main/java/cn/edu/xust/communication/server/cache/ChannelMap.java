package cn.edu.xust.communication.server.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

/**
 * 管理通道和服务器映射关系
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/26 12:24
 */
public class ChannelMap {

    /**
     * 将当前连接上的客户端连接存入map实现控制设备下发控制参数
     * key 存通道的ip , value 是服务器和客户端之间的通信通道
     */
    private static ConcurrentHashMap<String, ChannelHandlerContext> devicesMap = new ConcurrentHashMap<>();

    /**
     * 设备锁标志
     */
    private static ConcurrentHashMap<String, Lock> channelLockMap = new ConcurrentHashMap<>();

    /**电表号映射到通信通道socket*/
    private static ConcurrentHashMap<String,String> deviceNumMap2DeviceIp=new ConcurrentHashMap<>();

    public static void setDevicesMap(String socketAddress,ChannelHandlerContext ctx){
        devicesMap.put(socketAddress,ctx);
    }

    public static ChannelHandlerContext getDeviceFromMap(String socketAddress){
        return devicesMap.get(socketAddress);
    }

    public static int currentOnlineDevicesNum(){
        return devicesMap.size();
    }

    public static void removeDeviceFormMap(String socketAddress){
       devicesMap.remove(socketAddress);
    }

    public static void setChannelLock(String socketAddress,Lock lock){
        channelLockMap.put(socketAddress,lock);
    }

    public static Lock getChannelLock(String socketAddress){
        return channelLockMap.get(socketAddress);
    }

    public static String getDeviceIp(String deviceNum) {
        return deviceNumMap2DeviceIp.get(deviceNum);
    }

    /***
     * 绑定建立连接的设备ID和设备socket ip
     * @param deviceNum
     * @param deviceSocket
     */
    public static void bindDeviceNumAndDeviceSocket(String deviceNum,String deviceSocket) {
        if(Objects.nonNull(deviceNum) &&Objects.nonNull(deviceSocket)) {
            deviceNumMap2DeviceIp.put(deviceNum, deviceSocket);
        }
    }
}
