package online.stringtek.distributed.toy.rpc.client.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class RpcResponseHandler extends ChannelInboundHandlerAdapter implements Callable<Object> {
    //TODO 解决线程安全问题
    private ChannelHandlerContext ctx;
    private RpcRequest rpcRequest;
    private Object result;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("set ctx");
        this.ctx=ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result=msg;
        notify();
    }

    @Override
    public synchronized Object call() throws Exception {
        ctx.channel().writeAndFlush(rpcRequest);
        wait();
        return result;
    }
}
