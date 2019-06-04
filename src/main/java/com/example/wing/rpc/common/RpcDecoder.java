package com.example.wing.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author qxs on 2019/3/4.
 * 获取到的字节数组转换为对应的消息对象
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //消息长度
        int size = byteBuf.readableBytes();
        if (size < 4) {
            //保证所有消息接收完成
            return;
        }
        byte[] bytes = new byte[size];
        //读到缓存
        byteBuf.readBytes(bytes);
        //反序列化
        Object object = SerializationUtil.deserialize(bytes, genericClass);
        list.add(object);
        channelHandlerContext.flush();
    }
}
