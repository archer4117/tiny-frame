package com.example.wing.rpc.common;

import lombok.Data;

/**
 * @author qxs on 2019/3/4.
 * 请求消息封装对象
 */
@Data
public class RpcRequest {
    /**
     * 请求消息的消息Id
     */
    private String requestId;
    /**
     * 请求的具体的类名(接口名称)
     */
    private String className;
    /**
     * 请求的具体的方法名称
     */
    private String methodName;
    /**
     * 请求的方法参数类型列表
     */
    private Class<?>[] parameterTypes;
    /**
     * 请求的方法参数列表
     */
    private Object[] parameters;
}
