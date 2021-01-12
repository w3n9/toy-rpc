package online.stringtek.distributed.toy.rpc.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import online.stringtek.distributed.toy.rpc.core.serializer.Serializer;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    private final Class<?> clazz;
    private final Serializer serializer;
    public RpcDecoder(Class<?> clazz,Serializer serializer){
        this.clazz=clazz;
        this.serializer=serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int len = byteBuf.readInt();
        byte[] bytes=new byte[len];
        byteBuf.readBytes(bytes);
        list.add(serializer.deserialize(clazz,bytes));
    }
}
