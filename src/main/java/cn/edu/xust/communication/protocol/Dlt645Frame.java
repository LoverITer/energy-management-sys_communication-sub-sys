package cn.edu.xust.communication.protocol;

import cn.edu.xust.bean.AmmeterParameter;
import cn.edu.xust.communication.config.ApplicationContextHolder;
import cn.edu.xust.communication.enums.AmmeterStatusEnum;
import cn.edu.xust.communication.util.Dlt645FrameUtils;
import cn.edu.xust.communication.util.HexConverter;
import cn.edu.xust.mapper.AmmeterParameterMapper;

import java.io.*;
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
     * DLT645协议帧最小字节数
     */
    public static final int MIN_FRAME_LEN = 13;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
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
     * @throws Exception
     */
    public Dlt645Frame analysis(String hexString) throws Exception {
        Dlt645Frame frame = new Dlt645Frame();
        String recvCommand = HexConverter.fillBlank(hexString);
        //电表读取命令中返回01 控制码是错误响应    6837030092813968 D1 01 35 5D 16
        //                                      6837030092813968 91 07 33 33 36 35 33 33 33 58 16
        String[] commands = Objects.requireNonNull(recvCommand).trim().split(" ");
        if (commands.length < MIN_FRAME_LEN || commands.length > MAX_FRAME_LEN || !commands[0].equals(FRAME_STARTER) || !commands[commands.length - 1].equals(FRAME_END)) {
            return null;
        } else {
            System.out.println("原始地址：" + Arrays.toString(commands));
            System.out.println("帧起始符：" + commands[0]);
            System.out.println("电表地址：" + Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString));
            System.out.println("控制域：" + Dlt645FrameUtils.getControlBit(hexString));
            System.out.println("数据域长度：" + Dlt645FrameUtils.getDataLength(hexString));
            System.out.println("校验码：" + Dlt645FrameUtils.checkSumOfRecv(hexString));
            System.out.println("停止位：" + Dlt645FrameUtils.getStopBit(hexString));

            frame.setAddressField(Dlt645FrameUtils.getAmmeterIdFromResponseFrame(hexString));
            frame.setControlCode(Dlt645FrameUtils.getControlBit(hexString));
            frame.setDataLength(Dlt645FrameUtils.getDataLength(hexString));
            frame.setCheckSum(Dlt645FrameUtils.checkSumOfRecv(hexString));
            frame.setData(Dlt645FrameUtils.getData(hexString));

            AmmeterParameter ammeterParameter = new AmmeterParameter();
            ammeterParameter.setAcqTime(new Date());
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

            //加载数据标识文件，确定数据的类型
            InputStream is = new BufferedInputStream(new FileInputStream("src/config.properties"));
            InputStreamReader isr = new InputStreamReader(is, "GBK");
            Properties properties = new Properties();
            try {
                properties.load(isr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            frame.setDataIdentification(dataIdentification.toString());
            System.out.println("数据项名称：" + properties.getProperty(dataIdentification.toString()));
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
                    System.out.println(properties.getProperty(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.1")) + "v");
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
                    System.out.println(properties.getProperty(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.001")) + "A");
                    double current = Double.parseDouble(String.valueOf(bigDecimal.multiply(new BigDecimal("0.001"))));
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
                    System.out.println(properties.getProperty(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.0001")));
                    double power = Double.parseDouble(String.valueOf(bigDecimal.multiply(new BigDecimal("0.0001"))));
                    if ("02030000".equals(dataIdentification.toString())) {
                        //有功功率
                        ammeterParameter.setCurrentActivePower(power);
                    } else if ("02040000".equals(dataIdentification.toString())) {
                        //无功功率
                        ammeterParameter.setCurrentReactivePower(power);
                    }
                } else if (isPowerFactor) {
                    //功率因数0.001
                    System.out.println(properties.getProperty(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.001")));
                    double currentPowerFactor = Double.parseDouble(String.valueOf(bigDecimal.multiply(new BigDecimal("0.001"))));
                    ammeterParameter.setCurrentPowerFactor(currentPowerFactor);
                } else if (isEnergy) {
                    //有/无功总电能、四象限无功总电能0.01
                    System.out.println(properties.getProperty(dataIdentification.toString()) + "：" + bigDecimal.multiply(new BigDecimal("0.01")));
                    double enery = Double.parseDouble(String.valueOf(bigDecimal.multiply(new BigDecimal("0.01"))));
                    if ("00000000".equals(dataIdentification.toString())) {
                        ammeterParameter.setCurrentTotalActivePower(enery);
                    } else if ("00010000".equals(dataIdentification.toString())) {
                        ammeterParameter.setCurrentPositiveActivePower(enery);
                    } else if ("00020000".equals(dataIdentification.toString())) {
                        ammeterParameter.setCurrentNegtiveActivePower(enery);
                    }
                } else if (isVoltageDataBlock) {
                    //电压数据块
                    ammeterParameter.setCurrentAVoltage(Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(num).substring(8)).multiply(new BigDecimal("0.1")))));
                    ammeterParameter.setCurrentAVoltage(Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(num).substring(4, 8)).multiply(new BigDecimal("0.1")))));
                    ammeterParameter.setCurrentAVoltage(Double.parseDouble(String.valueOf(new BigDecimal(String.valueOf(num).substring(0, 4)).multiply(new BigDecimal("0.1")))));
                } else {
                    System.out.println(properties.getProperty(dataIdentification.toString()) + "：" + num);
                }
            }
            ammeterParameter.setAmmeterStatus(AmmeterStatusEnum.OK.getMessage());
            Object mapper = ApplicationContextHolder.getBean("ammeterParamMapper");
            if(mapper instanceof AmmeterParameterMapper) {
                AmmeterParameterMapper ammeterParamMapper=(AmmeterParameterMapper)mapper;
                int ret = ammeterParamMapper.updateSelective(ammeterParameter);
                if (ret <= 0) {
                    ammeterParamMapper.insertSelective(ammeterParameter);
                }
            }
            return frame;
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
