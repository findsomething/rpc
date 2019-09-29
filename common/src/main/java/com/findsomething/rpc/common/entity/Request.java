package com.findsomething.rpc.common.entity;

import lombok.Data;

/** @author link */
@Data
public class Request {

    /** 类名 */
    private String className;
    /** 函数名称 */
    private String methodName;
    /** 参数类型 */
    private Class<?>[] parameterTypes;
    /** 参数列表 */
    private Object[] parameters;
    /** 唯一请求时间 */
    private String requestId;
    /** 超时时间 */
    private int overTime;

}
