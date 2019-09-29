package com.findsomething.rpc.common.entity;

import lombok.Data;

/** @author link */
@Data
public class Response {

    /** 唯一Id */
    private String requestId;
    /** 错误码 */
    private int code;
    /** 错误信息 */
    private String error;
    /** 返回数据 */
    private Object data;
}
