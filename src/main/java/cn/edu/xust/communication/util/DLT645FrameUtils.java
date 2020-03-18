package cn.edu.xust.communication.util;

import cn.edu.xust.communication.protocol.DLT645Frame;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.Stack;

/**
 * DLT645-2007协议帧结构解析工具类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/04 12:33
 */
@Slf4j
public final class DLT645FrameUtils {

    private DLT645FrameUtils() {
    }

    /**
     * 读取数据
     *
     * @param meterNumber        电表编号
     * @param controlCode        控制码
     * @param dataLength         数据长度
     * @param dataIdentification 数据标识
     */
    public static void readData(String meterNumber, String controlCode, String dataLength, String dataIdentification) {
        String addressField = getAddressFiled(meterNumber);
        StringBuilder sb = new StringBuilder().append(DLT645Frame.FRAME_STARTER).append(" ")
                .append(addressField).append(" ")
                .append(DLT645Frame.FRAME_STARTER).append(" ")
                .append(controlCode).append(" ")
                .append(dataLength).append(" ")
                .append(dataIdentification).append(" ");
        DLT645Frame frame = new DLT645Frame(addressField, controlCode, dataLength, dataIdentification, checkSum(sb.toString()));
        System.out.println(frame.toString());
    }


    /**
     * 输入电表的编号，得到数据帧中的地址域
     *
     * @param meterNumber 电表编号
     * @return
     */
    public static String getAddressFiled(String meterNumber) {
        if (Objects.isNull(meterNumber) || meterNumber.length() == 0) {
            throw new IllegalArgumentException("meter number must not be empty");
        }
        char[] meterNum = meterNumber.toCharArray();
        String[] addressField = new String[6];
        Arrays.fill(addressField, "00");
        int indexOfArray = 0;
        for (int i = meterNum.length - 1; i >= 0 && indexOfArray < addressField.length; i--) {
            addressField[indexOfArray++] = String.valueOf(meterNum[i - 1]).concat(String.valueOf(meterNum[i]));
            i -= 1;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : addressField) {
            sb.append(str).append(" ");
        }
        return sb.toString();
    }

    /**
     * 从电表响应的16进制帧数据中解析出电表的表号
     * <pre>
     * 电表正常回应数据格式：
     * 68[370300928139]68910833333433373333338C16"
     *   |<--地址码-->|
     * <pre/>
     *
     * @param hexString 16进制字符串
     * @return 电表的表号
     */
    public static String getAmmeterIdFromResponseFrame(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() < DLT645Frame.MIN_FRAME_LEN * 2 || hexString.length() > DLT645Frame.MAX_FRAME_LEN * 2) {
            throw new NullPointerException("Illegal frame , cannot be parsed!");
        }
        StringBuilder eleMeterId = new StringBuilder();
        String responseFrame = HexConverter.fillBlank(hexString);
        String[] messageArray = Objects.requireNonNull(responseFrame).split(" ");
        Stack<String> stack = new Stack<>();
        for (int i = 1; i <= 6; i++) {
            stack.push(messageArray[i]);
        }
        while (!stack.isEmpty()) {
            String bit = stack.pop();
            eleMeterId.append(bit);
        }
        return eleMeterId.toString();
    }

    /**
     * 计算DL/T645帧数据校验和
     *
     * @param hexString 16进制字符串
     * @return 校验和
     */
    public static String checkSum(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() == 0) {
            throw new IllegalArgumentException("Hex frame string must not be empty");
        }
        String[] hexs = hexString.split(" ");
        int oct = 0;
        for (String str : hexs) {
            //每个字节转为10进制后取模256
            if (!"".equals(str) && !" ".equals(str)) {
                oct += Integer.parseInt(str, 16) % 256;
            }
        }
        String hex = Integer.toHexString(oct);
        return hex.substring(hex.length() - 2);
    }

    /**
     * 根据16进制数据解析出控制码
     * <pre>
     * 电表正常回应数据格式：
     * 6837030092813968[91]0833333433373333338C16"
     *                  /\
     *                  ||
     *                控制码
     * <pre/>
     * @param hexString 16进制字符串
     * @return 控制码
     */
    public static String getControlBit(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() < DLT645Frame.MIN_FRAME_LEN * 2 || hexString.length() > DLT645Frame.MAX_FRAME_LEN * 2) {
            return null;
        }
        String[] commands = HexConverter.getCommandStringArray(hexString);
        if (commands.length >= DLT645Frame.MIN_FRAME_LEN && commands.length <= DLT645Frame.MAX_FRAME_LEN) {
            return commands[8];
        }
        return null;
    }

    /**
     * 根据16进制字符串解析出数据域的长度
     * <pre>
     * 电表正常回应数据格式：
     * 683703009281396891[08]33333433373333338C16"
     *                    /\
     *                    ||
     *                数据域的长度
     * <pre/>
     * @param hexString 16进制字符串
     * @return 数据域的长度
     */
    public static String getDataLength(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() < DLT645Frame.MIN_FRAME_LEN * 2 || hexString.length() > DLT645Frame.MAX_FRAME_LEN * 2) {
            return null;
        }
        String[] commands = HexConverter.getCommandStringArray(hexString);
        if (commands.length >= DLT645Frame.MIN_FRAME_LEN && commands.length <= DLT645Frame.MAX_FRAME_LEN) {
            return commands[9];
        }
        return null;
    }

    /**
     * 解析出停止位
     * <pre>
     * 电表正常回应数据格式：
     * 6837030092813968910833333433373333338C[16]"
     *                                        /\
     *                                        ||
     *                                       停止位
     * <pre/>
     * @param hexString 16进制字符串
     * @return
     */
    public static String getStopBit(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() < DLT645Frame.MIN_FRAME_LEN * 2 || hexString.length() > DLT645Frame.MAX_FRAME_LEN * 2) {
            return null;
        }
        String[] commands = HexConverter.getCommandStringArray(hexString);
        if (commands.length >= DLT645Frame.MIN_FRAME_LEN && commands.length <= DLT645Frame.MAX_FRAME_LEN) {
            return commands[commands.length - 1];
        }
        return null;
    }

    /**
     * 直接从接收到的16进制字符串中获得传输过来的校验和
     * <pre>
     * 电表正常回应数据格式：
     * 683703009281396891083333343337333333[8C]16"
     *                                      /\
     *                                      ||
     *                                     校验和
     * <pre/>
     * @param hexString 16进制字符串
     * @return 校验和
     */
    public static String getCheckSumOfRecv(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() < DLT645Frame.MIN_FRAME_LEN * 2 || hexString.length() > DLT645Frame.MAX_FRAME_LEN * 2) {
            return null;
        }
        //解析出原始数据帧
        String[] commands = HexConverter.getCommandStringArray(hexString);
        return commands[commands.length - 2];
    }

    /**
     * 计算接收到的数据的校验和
     *
     * @param hexString 16进制字符串
     * @return
     */
    public static String checkSumOfRecv(String hexString) {
        if (Objects.isNull(hexString) || hexString.length() < DLT645Frame.MIN_FRAME_LEN * 2 || hexString.length() > DLT645Frame.MAX_FRAME_LEN * 2) {
            return null;
        }
        //解析出原始数据帧
        String[] commands = HexConverter.getCommandStringArray(hexString);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < commands.length - 2; i++) {
            sb.append(commands[i]);
        }
        return checkSum(HexConverter.fillBlank(sb.toString()));
    }


    /**
     * 向客户端写数据
     *
     * @param channel 服务器和设备建立的通道
     * @param hexMsg  命令信息
     */
    public static void writeMessage2Client(Channel channel, String hexMsg) {
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
    }


    public static void main(String[] args) {

        System.out.println(getControlBit("68370300928139689108333333333A3333338E16"));
        //System.out.println(DLT645FrameUtils.getEleMeterId2AddressFiled("68 37 03 00 92 81 39 68 91 08 33 33 34 33 37 33 33 33 8C 16"));
        //DLT645FrameUtils.readData("398192000337", ElectricMeterReader.MasterRequestFrame.getControlCode(), "04", "33 33 34 33");
        //System.out.println(DLT645FrameUtils.checkSum("68 04 00 00 78 93 45 68 11 04 33 33 34 33"));
        /*"68 37 03 00 92 81 39 68 91 08 [33 33 34 33 | 37 33 33 33 ] 8C 16";
        00 00 01 00                       DI0      DI3
        37-33=04;
        33-33=00;
        33-33=00;
        33-33=00; 000000.04KW/h;

        68 37 03 00 92 81 39 68 91 08  [33 33 34 33  |3A 33 33 33 ] 8F 16
        3A-33=07


        68 37 03 00 92 81 39 68 91 06    33 34 34 35   C7 56    B6 16
                                         00 01 01 02   [94 23] ==> 2394 ==>239.4v
        */
    }

}
