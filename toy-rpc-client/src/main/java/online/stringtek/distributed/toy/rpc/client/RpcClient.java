package online.stringtek.distributed.toy.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import online.stringtek.distributed.toy.rpc.client.handler.RpcResponseHandler;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;
import online.stringtek.distributed.toy.rpc.core.handler.RpcRequestEncoder;
import online.stringtek.distributed.toy.rpc.core.serializer.JSONSerializer;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RpcClient   {
    private static final ExecutorService executorService
            = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static RpcResponseHandler handler;

    public static void connect(String ip,int port) throws InterruptedException {
        handler=new RpcResponseHandler();
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
        ChannelFuture future = bootstrap.connect(ip, port).sync();
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(RpcRequest rpcRequest) throws InterruptedException, ClassNotFoundException {
        return (T) Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{Class.forName(rpcRequest.getClassName())}, (proxy, method, args) -> {
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
        RpcRequest rpcRequest=new RpcRequest();
        rpcRequest.setClassName("java.io.Serializable");
        rpcRequest.setRequestId("000001");
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new RpcRequestEncoder(RpcRequest.class,new JSONSerializer()));
                    }
                });
        ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost",23231)).sync();
        System.out.println("connected to server.");

        Channel channel = future.channel();
        channel.writeAndFlush(new RpcRequest());

    }

}
