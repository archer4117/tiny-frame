package com.example.wing.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author qxs on 2019/3/4.
 * 消息对象转换为字节数组通信
 */
public class RpcEncoder extends MessageToByteEncoder{

    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if (genericClass.isInstance(o)) {
            //序列化
            byte[] serialize = SerializationUtil.serialize(o);
            //写入下一个通道
            byteBuf.writeBytes(serialize);
        }
    }
}
