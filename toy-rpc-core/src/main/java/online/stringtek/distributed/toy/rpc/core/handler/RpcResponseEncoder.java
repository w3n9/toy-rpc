package online.stringtek.distributed.toy.rpc.core.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import online.stringtek.distributed.toy.rpc.core.serializer.Serializer;

public class RpcResponseEncoder extends MessageToByteEncoder<Object>{

    private Serializer serializer;
    public RpcResponseEncoder(Serializer serializer){
        this.serializer=serializer;
    }
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        System.out.println(o);
        byte[] bytes = serializer.serialize(o);
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}
