package online.stringtek.distributed.toy.rpc.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Data;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;

import java.util.concurrent.Callable;

@Data
public class RpcResponseHandler extends ChannelInboundHandlerAdapter implements Callable<Object> {

    //TODO 解决线程安全问题
    private ChannelHandlerContext ctx;
    private RpcRequest rpcRequest;
    private Object result;
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        result=msg;
        notify();
    }

    @Override
    public synchronized Object call() throws Exception {
        ctx.writeAndFlush(rpcRequest);
        System.out.println("写入服务端等待返回");
        wait();
        return result;
    }
}
