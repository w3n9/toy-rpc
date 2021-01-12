package online.stringtek.distributed.toy.rpc.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import online.stringtek.distributed.toy.rpc.core.serializer.Serializer;

import java.util.List;

public class RpcRequestDecoder extends ByteToMessageDecoder {
    private final Class<?> clazz;
    private final Serializer serializer;
    public RpcRequestDecoder(Class<?> clazz, Serializer serializer){
        this.clazz=clazz;
        this.serializer=serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        System.out.println("rpcDecoder");
        byte[] bytes=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        list.add(serializer.deserialize(clazz,bytes));
    }
}
