package cn.edu.xust.communication.protocol;

import cn.edu.xust.communication.util.DLT645FrameUtils;
import cn.edu.xust.communication.util.HexConverter;

import java.io.*;
import java.util.*;

/**
 * DL/T 645-2007协议规约帧结构类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/04 13:10
 */
public class DLT645Frame {

    /**
     * DLT645协议帧最大字节数
     */
    public static final int MAX_FRAME_LEN = 22;
    /**
     * DLT645协议帧最小字节数
     */
    public static final int MIN_FRAME_LEN = 16;

    /**
     * 帧起始符
     */
    public static final String FRAME_STARTER = "68";

    /**
     * 帧结束符
     */
    public static final String FRAME_END = "16";

    /**
     * 广播帧，当有新设备连接上来后服务端发送此指令和设备进行身份确认
     */
    public static final String BROADCAST_FRAME = "68 AA AA AA AA AA AA 68 11 04 33 33 33 33 AD 16";

    /**
     * 地址域：即设备的出厂编号，智能电表的编号一般在正面的二维码上
     */
    private String addressField;

    /**
     * 控制码
     */
    private String controlCode;

    /**
     * 数据域长度
     */
    private String dataLength;

    /**
     * 数据标识
     */
    private String dataIdentification;

    /**数据*/
    private String data;

    /**
     * 校验码
     */
    private String checkSum;



    public DLT645Frame() {
    }

    public String getAddressField() {
        return addressField;
    }

    public void setAddressField(String addressField) {
        this.addressField = addressField;
    }

    public String getControlCode() {
        return controlCode;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = controlCode;
    }

    public String getDataLength() {
        return dataLength;
    }

    public void setDataLength(String dataLength) {
        this.dataLength = dataLength;
    }

    public String getDataIdentification() {
        return dataIdentification;
    }

    public void setDataIdentification(String dataIdentification) {
        this.dataIdentification = dataIdentification;
    }

    public String getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(String checkSum) {
        this.checkSum = checkSum;
    }

    public DLT645Frame(String addressField, String controlCode, String dataLength, String dataIdentification, String checkSum) {
        this.addressField = addressField;
        this.controlCode = controlCode;
        this.dataLength = dataLength;
        this.dataIdentification = dataIdentification;
        this.checkSum = checkSum;
    }

    @Override
    public String toString() {
        return FRAME_STARTER + " " + addressField + " " + FRAME_STARTER + " " + controlCode + " " + dataLength + " " + dataIdentification + " " + checkSum + " " + "16";
    }

    /**
     * 解析DLT645-2007报文
     * <pre>
     *  输入示例：681213432156768110433333333AD16
     * <pre/>
     * @param hexString 16进制字符串
     * @throws Exception
     */
    public DLT645Frame analysis(String hexString) throws Exception {
        DLT645Frame frame = new DLT645Frame();
        String recvCommand = HexConverter.fillBlank(hexString);
        String[] commands = Objects.requireNonNull(recvCommand).trim().split(" ");
        if (commands.length < MIN_FRAME_LEN || commands.length > MAX_FRAME_LEN || !commands[0].equals(FRAME_STARTER) || !commands[commands.length - 1].equals(FRAME_END)) {
            throw new IllegalArgumentException("Illegal frame cannot be parsed!");
        } else {
            System.out.println("您的输入：" + hexString);
            System.out.println("原始地址：" + Arrays.toString(commands));
            System.out.println("帧起始符：" + commands[0]);
            System.out.println("电表地址：" + DLT645FrameUtils.getAmmeterIdFromResponseFrame(hexString));
            System.out.println("控制域：" + DLT645FrameUtils.getControlBit(hexString));
            System.out.println("数据域长度：" + DLT645FrameUtils.getDataLength(hexString));
            System.out.println("校验码：" + DLT645FrameUtils.checkSumOfRecv(hexString));
            System.out.println("停止位：" + DLT645FrameUtils.getStopBit(hexString));

            //解析数据标识
            List<String> list2 = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                list2.add(Integer.toHexString(Integer.parseInt(commands[commands.length - 3 - i - (Integer.parseInt(commands[9], 16) - 4)], 16) - 51));
            }
            String[] DTID = list2.toArray(new String[list2.size()]);
            StringBuilder dataIdentification = new StringBuilder();
            for (int i = 0; i < DTID.length; i++) {
                if (DTID[i].length() == 1) {
                    DTID[i] = String.format("%02d", Integer.parseInt(DTID[i]));
                } else if (DTID[i].length() == 8) {
                    DTID[i] = "FF";
                }
                dataIdentification.append(DTID[i]);
            }

            //加载数据标识文件，确定数据的类型
            InputStream is = new BufferedInputStream(new FileInputStream("src/config.properties"));
            InputStreamReader isr = new InputStreamReader(is, "GBK");
            Properties properties = new Properties();
            try {
                properties.load(isr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("数据项名称：" + properties.getProperty(dataIdentification.toString()));
            return frame;
           /* //解析返回数据
            if (commands.length > 16) {
                int DTID0 = Integer.parseInt(DTID[0]);
                int DTID1 = Integer.parseInt(DTID[1]);
                List<String> list3 = new ArrayList();
                for (int i = 0; i < Integer.parseInt(commands[9], 16) - 4; i++) {
                    list3.add(commands[commands.length - 3 - i]);
                }

                String[] data = list3.toArray(new String[list3.size()]);
                long num = Long.parseLong((this.DataFormat(data)).toString());
                BigDecimal bigDecimal = new BigDecimal(num);
                if (DTID0 == 2 && DTID1 == 1 && !String.valueOf(DTID[2]).equals("FF")) {
                    //电压0.1v
                    System.out.println(properties.getProperty(sbr.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.1")) + "v");
                } else if (DTID0 == 2 && DTID1 == 2) {
                    //电流0.001A
                    System.out.println(properties.getProperty(sbr.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.001")) + "A");
                } else if ((DTID0 == 2 && DTID1 == 3) || (DTID0 == 2 && DTID1 == 4) || (DTID0 == 2 && DTID1 == 5)) {
                    //有无功功率0.0001
                    System.out.println(properties.getProperty(sbr.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.0001")));
                } else if (DTID0 == 2 && DTID1 == 6) {
                    //功率因数0.001
                    System.out.println(properties.getProperty(sbr.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.001")));
                } else if ((DTID0 == 0 && DTID1 == 0) || (DTID0 == 0 && DTID1 == 1) || (DTID0 == 0 && DTID1 == 2) || (DTID0 == 0 && DTID1 == 3) || (DTID0 == 0 && DTID1 == 4) || (DTID0 == 0 && DTID1 == 5) || (DTID0 == 0 && DTID1 == 6) || (DTID0 == 0 && DTID1 == 7) || (DTID0 == 0 && DTID1 == 8)) {
                    //有无功总电能、四象限无功总电能0.01
                    System.out.println(properties.getProperty(sbr.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.01")));
                } else if (DTID0 == 2 && DTID1 == 1 && String.valueOf(DTID[2]).equals("FF")) {
                    //电压数据块
                    System.out.println(num);
                    System.out.println(String.valueOf(num).substring(0, 4));
                    System.out.println(String.valueOf(num).substring(4, 8));
                    System.out.println(String.valueOf(num).substring(8));
                    System.out.println("C相电压" + new BigDecimal(String.valueOf(num).substring(0, 4)).multiply(new BigDecimal("0.1")));
                    System.out.println("B相电压" + new BigDecimal(String.valueOf(num).substring(4, 8)).multiply(new BigDecimal("0.1")));
                    System.out.println("A相电压" + new BigDecimal(String.valueOf(num).substring(8)).multiply(new BigDecimal("0.1")));
                } else {
                    System.out.println(properties.getProperty(sbr.toString()) + "：" + num);
                }
            }*/
        }
    }

    private StringBuffer dataFormat(String data[]) {
        StringBuffer sbr = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String data1 = String.valueOf(Integer.parseInt(data[i].substring(0, 1), 16) - 3);
            String data2 = String.valueOf(Integer.parseInt(data[i].substring(1), 16) - 3);
            sbr.append(data1);
            sbr.append(data2);
        }
        return sbr;
    }

}
