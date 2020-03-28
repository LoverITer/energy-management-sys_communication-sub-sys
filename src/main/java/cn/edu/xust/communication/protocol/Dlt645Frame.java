package cn.edu.xust.communication.protocol;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.enums.AmmeterReader;
import cn.edu.xust.communication.enums.AmmeterStatusEnum;
import cn.edu.xust.communication.util.Dlt645FrameUtils;
import cn.edu.xust.communication.util.FileUtils;
import cn.edu.xust.communication.util.HexConverter;

import java.math.BigDecimal;
import java.util.*;

/**
 * DL/T 645-2007协议规约帧结构类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/04 13:10
 */
public class Dlt645Frame {

    /**
     * DLT645协议帧最大字节数
     */
    public static final int MAX_FRAME_LEN = 22;
    /**
     * DLT645协议帧正常响应最小字节数
     */
    public static final int MIN_FRAME_LEN = 16;

    public static final int ERR_RESPONSE_FRAME_LEN=13;

    /**帧结构中地址域的字节数*/
    public static final int ADDRESS_FIELD_LEN=6;

    /**帧起始符*/
    public static final String FRAME_STARTER = "68";

    /** 帧结束符*/
    public static final String FRAME_END = "16";

    /** 广播帧，当有新设备连接上来后服务端发送此指令和设备进行身份确认*/
    public static final String BROADCAST_FRAME = "68 AA AA AA AA AA AA 68 11 04 33 33 33 33 AD 16";

    /**地址域：即设备的出厂编号，智能电表的编号一般在正面的二维码上*/
    private String addressField;

    /** 控制码*/
    private String controlCode;

    /** 数据域长度*/
    private String dataLength;

    /**数据标识*/
    private String dataIdentification;

    /**数据*/
    private String data;

    /**校验码*/
    private String checkSum;

    public Dlt645Frame() {

    }

    public Dlt645Frame(String addressField, String controlCode, String dataLength, String dataIdentification) {
        this.addressField = addressField;
        this.controlCode = controlCode;
        this.dataLength = dataLength;
        this.dataIdentification = dataIdentification;
    }

    public Dlt645Frame(String addressField, String controlCode, String dataLength, String dataIdentification, String data, String checkSum) {
        this.addressField = addressField;
        this.controlCode = controlCode;
        this.dataLength = dataLength;
        this.dataIdentification = dataIdentification;
        this.checkSum = checkSum;
        this.data = data;
    }


    /**
     * 拼接读取命令报文
     */
    public String createFrame(){
        return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        //帧数据前半部分（校验码由此部分数据计算得出）
        sb.append(Dlt645Frame.FRAME_STARTER).append(" ")
                .append(Dlt645FrameUtils.getAddressFiled(this.addressField))
                .append(Dlt645Frame.FRAME_STARTER).append(" ")
                .append(this.controlCode).append(" ")
                .append(this.dataLength).append(" ")
                .append(this.dataIdentification);
        return sb.append(" ").append(Dlt645FrameUtils.checkSum(sb.toString())).append(" ").append(Dlt645Frame.FRAME_END).toString();
    }

    /**
     * 解析DLT645-2007报文
     * <pre>
     *  输入示例：681213432156768110433333333AD16
     * <pre/>
     * @param hexString 16进制字符串
     */
    public AmmeterParameter analysis(String hexString) {
        //加载配置文件
        Map<String,String> properties=FileUtils.getPropertiesMap();
        String commandRCVD = HexConverter.fillBlank(hexString);
        String[] commands = Objects.requireNonNull(commandRCVD).trim().split(" ");
        AmmeterParameter ammeterParameter = new AmmeterParameter();
        ammeterParameter.setAcqTime(new Date());
        if (commands.length < MIN_FRAME_LEN || commands.length > MAX_FRAME_LEN ||
                !commands[0].equals(FRAME_STARTER) || !commands[commands.length - 1].equals(FRAME_END)) {
            String controlCode=Dlt645FrameUtils.getControlBit(hexString);
            if (Objects.nonNull(controlCode)&&AmmeterReader.SlaveExceptionResponseFrame.getControlCode().equalsIgnoreCase(controlCode)) {
                ammeterParameter.setDeviceNumber(Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString));
                ammeterParameter.setAmmeterStatus(AmmeterStatusEnum.DEVICE_ERROR.getMessage());
            }
            return ammeterParameter;
        } else {
            System.out.println("原始帧：" + Arrays.toString(commands));
            System.out.println("帧起始符：" + commands[0]);
            System.out.println("电表地址：" + Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString));
            System.out.println("控制域：" + Dlt645FrameUtils.getControlBit(hexString));
            System.out.println("数据域长度：" + Dlt645FrameUtils.getDataLength(hexString));
            System.out.println("校验码：" + Dlt645FrameUtils.checkSumOfRecv(hexString));
            System.out.println("停止位：" + Dlt645FrameUtils.getStopBit(hexString));
            ammeterParameter.setDeviceNumber(Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString));

            //解析数据标识
            List<String> list2 = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                list2.add(Integer.toHexString(Integer.parseInt(commands[commands.length - 3 - i - (Integer.parseInt(commands[9], 16) - 4)], 16) - 51));
            }
            //4字节的数据标识
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

            System.out.println("数据项名称：" + properties.get(dataIdentification.toString()));
            //解析返回数据
             if (commands.length > Dlt645Frame.MIN_FRAME_LEN) {
                int DTID0 = Integer.parseInt(DTID[0]);
                int DTID1 = Integer.parseInt(DTID[1]);
                List<String> list3 = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(commands[9], 16) - 4; i++) {
                    list3.add(commands[commands.length - 3 - i]);
                }

                String[] data = list3.toArray(new String[list3.size()]);
                long num = Long.parseLong((this.dataFormat(data)).toString());
                BigDecimal bigDecimal = new BigDecimal(num);
                boolean isVoltage=DTID0 == 2 && DTID1 == 1 && !"FF".equals(String.valueOf(DTID[2]));
                boolean isCurrent=DTID0 == 2 && DTID1 == 2;
                boolean isReactivePower=(DTID0 == 2 && DTID1 == 3) || (DTID0 == 2 && DTID1 == 4) || (DTID0 == 2 && DTID1 == 5);
                boolean  isPowerFactor=DTID0 == 2 && DTID1 == 6;
                boolean isEnergy=(DTID0 == 0 && DTID1 == 0) || (DTID0 == 0 && DTID1 == 1) || (DTID0 == 0 && DTID1 == 2)
                        || (DTID0 == 0 && DTID1 == 3) || (DTID0 == 0 && DTID1 == 4) || (DTID0 == 0 && DTID1 == 5)
                        || (DTID0 == 0 && DTID1 == 6) || (DTID0 == 0 && DTID1 == 7) || (DTID0 == 0 && DTID1 == 8);
                boolean isVoltageDataBlock=DTID0 == 2 && DTID1 == 1 && "FF".equals(String.valueOf(DTID[2]));
                if (isVoltage) {
                    System.out.println(properties.get(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.1")) + "v");
                    double voltage = Double.parseDouble(String.valueOf(bigDecimal.multiply(new BigDecimal("0.1"))));
                    if ("02010100".contentEquals(dataIdentification.toString())) {
                        //A相电压
                        ammeterParameter.setCurrentAVoltage(voltage);
                    } else if ("02010200".contentEquals(dataIdentification.toString())) {
                        //B相电压
                        ammeterParameter.setCurrentBVoltage(voltage);
                    } else if ("02010300".contentEquals(dataIdentification.toString())) {
                        //B相电压
                        ammeterParameter.setCurrentCVoltage(voltage);
                    }
                } else if (isCurrent) {
                    System.out.println(properties.get(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.001")) + "A");
                    double current = Double.parseDouble(String.valueOf(bigDecimal.multiply(BigDecimal.valueOf(0.001))));
                    if ("02020100".contentEquals(dataIdentification.toString())) {
                        //A相电流
                        ammeterParameter.setCurrentACurrent(current);
                    } else if ("02020200".contentEquals(dataIdentification.toString())) {
                        //B相电流
                        ammeterParameter.setCurrentBCurrent(current);
                    } else if ("02020300".contentEquals(dataIdentification.toString())) {
                        //B相电流
                        ammeterParameter.setCurrentCCurrent(current);
                    }
                } else if (isReactivePower) {
                    //有/无功功率0.0001
                    System.out.println(properties.get(dataIdentification.toString()) + "：" + bigDecimal.multiply(BigDecimal.valueOf(0.0001)));
                    double power = Double.parseDouble(String.valueOf(bigDecimal.multiply(BigDecimal.valueOf(0.0001))));
                    if ("02030000".equals(dataIdentification.toString())) {
                        //有功功率
                        ammeterParameter.setCurrentActivePower(power);
                    } else if ("02040000".equals(dataIdentification.toString())) {
                        //无功功率
                        ammeterParameter.setCurrentReactivePower(power);
                    }
                } else if (isPowerFactor) {
                    //功率因数0.001
                    System.out.println(properties.get(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.001")));
                    double currentPowerFactor = Double.parseDouble(String.valueOf(bigDecimal.multiply(BigDecimal.valueOf(0.001))));
                    ammeterParameter.setCurrentPowerFactor(currentPowerFactor);
                } else if (isEnergy) {
                    //有/无功总电能、四象限无功总电能0.01
                    System.out.println(properties.get(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.01")));
                    double energy = Double.parseDouble(String.valueOf(bigDecimal.multiply(new BigDecimal("0.01"))));
                    if ("00000000".equals(dataIdentification.toString())) {
                        ammeterParameter.setCurrentTotalActivePower(energy);
                    } else if ("00010000".equals(dataIdentification.toString())) {
                        ammeterParameter.setCurrentPositiveActivePower(energy);
                    } else if ("00020000".equals(dataIdentification.toString())) {
                        ammeterParameter.setCurrentNegtiveActivePower(energy);
                    }
                } else if (isVoltageDataBlock) {
                    //电压数据块
                    ammeterParameter.setCurrentAVoltage(Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(num).substring(8)).multiply(new BigDecimal("0.1")))));
                    ammeterParameter.setCurrentAVoltage(Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(num).substring(4, 8)).multiply(new BigDecimal("0.1")))));
                    ammeterParameter.setCurrentAVoltage(Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(num).substring(0, 4)).multiply(new BigDecimal("0.1")))));
                }
            }
            ammeterParameter.setAmmeterStatus(AmmeterStatusEnum.OK.getMessage());
            return ammeterParameter;
        }
    }


    private StringBuffer dataFormat(String[] data) {
        StringBuffer sbr = new StringBuffer();
        for (String datum : data) {
            String data1 = String.valueOf(Integer.parseInt(datum.substring(0, 1), 16) - 3);
            String data2 = String.valueOf(Integer.parseInt(datum.substring(1), 16) - 3);
            sbr.append(data1);
            sbr.append(data2);
        }
        return sbr;
    }

}
