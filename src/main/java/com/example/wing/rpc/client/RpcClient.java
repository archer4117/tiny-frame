package com.example.wing.rpc.client;

import com.example.wing.rpc.common.RpcDecoder;
import com.example.wing.rpc.common.RpcEncoder;
import com.example.wing.rpc.common.RpcRequest;
import com.example.wing.rpc.common.RpcResponse;
import com.example.wing.rpc.register.RpcDiscover;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author qxs on 2019/3/4.
 * RPC通信客户端,往服务端发送请求,并且接受服务端的响应
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
    private RpcResponse rpcResponse;
    private RpcRequest rpcRequest;

    /**
     * 同步锁
     */
    private final Object object = new Object();

    private RpcDiscover rpcDiscover;

    public RpcClient(RpcRequest rpcRequest, RpcDiscover rpcDiscover) {
        this.rpcRequest = rpcRequest;
        this.rpcDiscover = rpcDiscover;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        this.rpcResponse = rpcResponse;
        synchronized (object) {
            //刷新缓存
            channelHandlerContext.flush();
            //唤醒等待
            object.notifyAll();
        }
    }

    public RpcResponse send() throws Exception {
        Bootstrap client = new Bootstrap();
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();
        try {
            client.group(loopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class)).addLast(this);
                        }
                    }).option(ChannelOption.SO_KEEPALIVE, true);
            String serverAddress = rpcDiscover.discover();
            String[] str = serverAddress.split(":");
            String host = str[0];
            int port = Integer.valueOf(str[1]);
            ChannelFuture future = client.connect(host, port).sync();
            System.out.println("客户端发送数据："+ rpcRequest);
            future.channel().writeAndFlush(rpcRequest).sync();
            synchronized(object){
                object.wait();
            }
            if (rpcResponse != null) {
                future.channel().closeFuture().sync();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            loopGroup.shutdownGracefully();
        }
        return rpcResponse;

    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }
}
