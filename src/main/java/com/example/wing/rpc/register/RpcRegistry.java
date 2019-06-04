package com.example.wing.rpc.register;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author qxs on 2019/3/4.
 * 服务注册
 */
@Slf4j
@Component
public class RpcRegistry {

    @Resource
    private ZkProperties zkProperties;

    public void createNode(String data) throws IOException {
        //注册可以不用监听
        ZooKeeper zooKeeper = new ZooKeeper(zkProperties.getRegistry(), Constant.SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });

        try {
            Stat stat = zooKeeper.exists(Constant.REGISTRY_PATH, false);
            //如果目录不存在，创建一个永久节点
            if (stat == null) {
                zooKeeper.create(Constant.REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            //创建一个临时节点，保存注册信息
            zooKeeper.create(Constant.DATA_PATH, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
