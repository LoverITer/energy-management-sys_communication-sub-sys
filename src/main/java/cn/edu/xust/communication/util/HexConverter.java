package cn.edu.xust.communication.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 编码转换器
 *
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/17 12:33
 */
@Slf4j
public final class HexConverter {

    private HexConverter() {
    }

    /**
     * 字节数组转换为hex字符串
     *
     * @param bytes 字节数据
     * @return hex字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        if (Objects.isNull(bytes)) {
            throw new IllegalArgumentException("param `bytes` can't be null");
        }
        StringBuffer hexString = new StringBuffer();
        String hex = "";
        for (int i = 0, len = bytes.length; i < len; i++) {
            hex = Integer.toHexString(i);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }


    /**
     * hex字符串转换为byte数组
     *
     * @param hexString hex字符串
     * @return byte数组
     */
    public static byte[] hexString2ByteArray(String hexString) {
        if (Objects.isNull(hexString)) {
            throw new IllegalArgumentException("param `hexString` can't be null");
        }
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length() / 2;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return bytes;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param hexString c16进制串
     * @return 字符串
     */
    public static String hexString2String(String hexString) {
        if (Objects.isNull(hexString)) {
            throw new IllegalArgumentException("param `hexString` can't be null");
        }
        hexString = hexString.replaceAll(" ", "");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < hexString.length() / 2; i++) {
            buffer.append((char) Integer.valueOf(hexString.substring(i * 2, i * 2 + 2), 16).byteValue());
        }
        return buffer.toString();
    }

    /**
     * 字符转成字节数据char-->integer-->byte
     *
     * @param ch 字符
     * @return 字节数据
     */
    public static Byte char2Byte(Character ch) {
        return Integer.valueOf(ch).byteValue();
    }

    /**
     * 10进制数字转成16进制
     *
     * @param a
     * @param len
     * @return
     */
    public static String int2HexString(int a, int len) {
        len <<= 1;
        String hexString = Integer.toHexString(a);
        int b = len - hexString.length();
        if (b > 0) {
            for (int i = 0; i < b; i++) {
                hexString = "0" + hexString;
            }
        }
        return hexString;
    }

    /**
     * 对16进制的两个字符串进行异或
     *
     * @param strHex_X
     * @param strHex_Y
     * @return
     */
    public static String xor(String strHex_X, String strHex_Y) {
        //将x、y转成二进制形式
        String anotherBinary = Integer.toBinaryString(Integer.valueOf(strHex_X, 16));
        String thisBinary = Integer.toBinaryString(Integer.valueOf(strHex_Y, 16));
        String result = "";
        //判断是否为8位二进制，否则左补零
        if (anotherBinary.length() != 8) {
            for (int i = anotherBinary.length(); i < 8; i++) {
                anotherBinary = "0" + anotherBinary;
            }
        }
        if (thisBinary.length() != 8) {
            for (int i = thisBinary.length(); i < 8; i++) {
                thisBinary = "0" + thisBinary;
            }
        }
        //异或运算
        for (int i = 0; i < anotherBinary.length(); i++) {
            //如果相同位置数相同，则补0，否则补1
            if (thisBinary.charAt(i) == anotherBinary.charAt(i)) {
                result += "0";
            } else {
                result += "1";
            }
        }
        return Integer.toHexString(Integer.parseInt(result, 2));
    }

    /**
     * @param src
     * @return
     */
    public static String bytes2String(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 接收字节数组并转换为16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String receiveHexToString(byte[] bytes) {
        try {
            String hexStr = bytes2String(bytes);
            assert hexStr != null;
            hexStr = hexStr.toUpperCase();
            return hexStr;
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("接收字节数据并转为16进制字符串异常");
        }
        return null;
    }

    /**
     * 以1字节分隔16进制字符串，比如：
     * <pre>
     *  "9ca525a6f035"=====>"9C A5 25 A6 F0 35"
     * <pre/>
     *
     * @param hexString
     * @return
     */
    public static String fillBlank(String hexString) {
        if (Objects.isNull(hexString)) {
            return null;
        }
        hexString = hexString.toUpperCase();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hexString.length() / 2; i++) {
            sb.append(hexString, i * 2, i * 2 + 2).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * 帧结构高位填充，比如：
     * <pre>
     * fill("7dd",6,'0')
     * "7dd"====================> "0007dd"
     * <pre/>
     *
     * @param input  需要补位的字符串
     * @param size   补位后的最终长度
     * @param symbol 按symol补充 如'0'
     * @return
     */
    public static String fill(String input, int size, char symbol) {
        while (input.length() < size) {
            input = symbol + input;
        }
        return input;
    }

    /**
     * 帧结构对齐，高位补0
     *
     * @param hexString 需要补位的字符串
     * @param symbol    按symol补充 如'0'
     * @return 补齐后的字符串
     */
    public static String frameAligned(String hexString, char symbol) {
        if (Objects.isNull(hexString)) {
            return null;
        }
        if (hexString.length() % 2 != 0) {
            hexString = fill(hexString, hexString.length() + 1, symbol);
        }
        return hexString;
    }

    /**
     * 把一串16进制字符串转换为String数组
     *
     * @param hexString 16进制字符串
     * @return String数组
     */
    public static String[] getCommandStringArray(String hexString) {
        String readAbleCommand = HexConverter.fillBlank(hexString);
        return Objects.requireNonNull(readAbleCommand).trim().split(" ");
    }

    public static void main(String[] args) {
        System.out.println("start" + fillBlank("68AAAAAAAAAAAA68110433333333AD16") + "end");
    }

}
