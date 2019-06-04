package com.example.wing.rpc.server;

import com.example.wing.rpc.common.RpcDecoder;
import com.example.wing.rpc.common.RpcEncoder;
import com.example.wing.rpc.common.RpcRequest;
import com.example.wing.rpc.common.RpcResponse;
import com.example.wing.rpc.register.RpcRegistry;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;
import io.netty.bootstrap.ServerBootstrap;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qxs on 2019/3/4.
 * 扫描所有rpc服务并启动
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Service
public class RpcServer implements ApplicationContextAware, InitializingBean {

    /**
     * 用于保存所有提供服务的方法, 其中key为类的全路径名, value是所有的实现类
     */
    private final Map<String, Object> serviceBeanMap = new HashMap<>();

    @Value("${rpc.address}")
    private String serverAddress;

    @Resource
    private RpcRegistry rpcRegistry;

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerBootstrap server = new ServerBootstrap();
        //用于创建TCP连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        //用于处理channel的I/O事件
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        server.group(bossGroup, workerGroup)
                //启动异步socket
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class))//1 解码请求
                                .addLast(new RpcEncoder(RpcResponse.class))//2编码响应信息
                                .addLast(new RpcServerHandler(serviceBeanMap));//3请求处理
                    }
                }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE,true);
        String[] str = serverAddress.split(":");
        String host = str[0];
        int port = Integer.valueOf(str[1]);
        ChannelFuture future  = server.bind(host, port).sync();
        System.out.println("服务器启动成功：" + future.channel().localAddress());
        rpcRegistry.createNode(serverAddress);
        System.out.println("向zk注册服务");
        //等待通信完成
        future.channel().closeFuture().sync();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        //获取@RpcService类
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Map.Entry entry : serviceBeanMap.entrySet()) {
                this.serviceBeanMap.put((String) entry.getKey(), entry.getValue());
            }
        }
        System.out.println("服务器：" +serverAddress + "提供服务的列表：" + serviceBeanMap);

    }
}
