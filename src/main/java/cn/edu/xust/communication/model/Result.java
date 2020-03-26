package cn.edu.xust.communication.model;

import java.io.Serializable;

/**
 * @author ：huangxin
 * @modified ：
 * @since ：2020/03/26 14:48
 */
public class Result implements Serializable {


    private int code;
    private Object message;
    private String uniId;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
    }
}
