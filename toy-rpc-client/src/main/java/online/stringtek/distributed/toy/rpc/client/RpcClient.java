package online.stringtek.distributed.toy.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import online.stringtek.distributed.toy.rpc.client.handler.RpcRequestHandler;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;
import online.stringtek.distributed.toy.rpc.core.serializer.Serializer;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcClient {
    private static final ExecutorService executorService
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static RpcRequestHandler handler;

    public static void connect(String ip,int port) throws InterruptedException {
        handler=new RpcRequestHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(handler);
                    }
                });
        ChannelFuture future = bootstrap.connect(ip, port);
        future.sync();
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(RpcRequest<T> rpcRequest) throws InterruptedException {
        return (T) Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{rpcRequest.getClazz()}, (proxy, method, args) -> {
            if(handler==null)
                connect("127.0.0.1",23231);
            handler.setRpcRequest(rpcRequest);
            System.out.println("set request ok");
            Object o = executorService.submit(handler).get();
            System.out.println(o);
            return o;
        });
    }

    public static void main(String[] args) throws InterruptedException {
        RpcRequest<Serializable> rpcRequest=new RpcRequest<>();
        rpcRequest.setClazz(Serializable.class);
        rpcRequest.setRequestId("000001");
        Object proxy = proxy(rpcRequest);
        proxy.toString();
    }

}
