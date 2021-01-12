package online.stringtek.distributed.toy.rpc.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import online.stringtek.distributed.toy.rpc.core.serializer.Serializer;

import java.util.List;

public class RpcResponseDecoder extends ByteToMessageDecoder {
    private Serializer serializer;
    public RpcResponseDecoder(Serializer serializer){
        this.serializer=serializer;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readableBytes();
        byte[] bytes=new byte[len];
        byteBuf.readBytes(bytes);
        list.add(serializer.deserialize(Object.class,bytes));
    }
}
