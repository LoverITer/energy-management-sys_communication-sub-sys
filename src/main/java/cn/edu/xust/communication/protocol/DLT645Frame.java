package cn.edu.xust.communication.protocol;

/**
 * DL/T 645-2007协议规约帧结构类
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/04 13:10
 */
public class DLT645Frame {

    /**帧起始符*/
    public static final String FRAME_STARTER ="68";
    /**地址域：即设备的出厂编号，智能电表的编号一般在正面的二维码上*/
    private  final String addressField;
    /**控制码*/
    private final String  controlCode;
    /**数据长度*/
    private final String dataLength;
    /**数据标识*/
    private final String dataIdentification;
    /**校验和*/
    private final String checkSum;
    /**帧结束符*/
    private static final String FRAME_END="16";


    public DLT645Frame(String addressField, String controlCode, String dataLength, String dataIdentification, String checkSum) {
        this.addressField = addressField;
        this.controlCode = controlCode;
        this.dataLength = dataLength;
        this.dataIdentification = dataIdentification;
        this.checkSum = checkSum;
    }

    @Override
    public String toString() {
        return FRAME_STARTER+" "+addressField+" "+FRAME_STARTER+" "+controlCode+" "+dataLength+" "+dataIdentification+" "+checkSum+" "+"16";
    }
}
