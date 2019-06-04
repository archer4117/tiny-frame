package com.example.wing.rpc.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qxs on 2019/3/4.
 * 用于标识rpc服务提供者，spring启动时自动加载
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RpcService {
    public Class<?> value();
}
