package cn.edu.xust.communication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用于封装返回客户端的异步消息
 * @author huanxin
 * @since 2020/03/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Serializable {
    private static final long serialVersionUID = -7033707301911915197L;

    private int code;
    private String message;
    private String uniId;


}
