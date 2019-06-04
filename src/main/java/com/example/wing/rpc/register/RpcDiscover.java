package com.example.wing.rpc.register;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author qxs on 2019/3/4.
 * 服务发现
 */
@Component
public class RpcDiscover {

    @Resource
    private ZkProperties zkProperties;

    private volatile List<String> dataList = new ArrayList<>();
    private ZooKeeper zooKeeper;

    @PostConstruct
    public void initMethod() throws IOException {
        System.out.println("zk register url:"+zkProperties.getRegistry());
        zooKeeper = new ZooKeeper(zkProperties.getRegistry(), Constant.SESSION_TIMEOUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //监听服务器列表变化
                if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                    watchNode();
                }
            }
        });

        watchNode();
    }

    private void watchNode() {
        try {
            List<String> nodeList = zooKeeper.getChildren(Constant.REGISTRY_PATH, true);
            ArrayList<String> dataList = new ArrayList<>();
            for (String node :
                    nodeList) {
                byte[] data = zooKeeper.getData(Constant.REGISTRY_PATH + "/" + node, false, null);
                dataList.add(new String(data));
            }
            this.dataList = dataList;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机从服务端返回一个可用地址
     *
     * @return server
     */
    public String discover() {
        int size = dataList.size();
        if (size > 0) {
            int index = new Random().nextInt(size);
            return dataList.get(index);
        }
        throw new RuntimeException("没有找到可用服务器");
    }
}
