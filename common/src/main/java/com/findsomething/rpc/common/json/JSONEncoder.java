package com.findsomething.rpc.common.json;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author link
 */
public class JSONEncoder extends MessageToMessageEncoder {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, List list) throws Exception {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer();
        byte[] bytes = JSON.toJSONBytes(msg);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
        list.add(byteBuf);
    }
}
