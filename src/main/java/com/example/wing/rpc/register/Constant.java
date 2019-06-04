package com.example.wing.rpc.register;

/**
 * @author qxs on 2019/3/4.
 * 设置zk相关参数
 */
public interface Constant {
    /**
     * 定义用于保存rpc通信服务端的地址信息的目录
     */
    int SESSION_TIMEOUT = 4000;
    /**
     * 定义数据存放的具体目录
     */
    String REGISTRY_PATH = "/rpc";
    String DATA_PATH = REGISTRY_PATH + "/data";
}
