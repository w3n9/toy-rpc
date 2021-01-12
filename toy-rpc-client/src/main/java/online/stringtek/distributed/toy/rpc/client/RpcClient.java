package online.stringtek.distributed.toy.rpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import online.stringtek.distributed.toy.rpc.client.handler.RpcResponseHandler;
import online.stringtek.distributed.toy.rpc.core.common.RpcConstant;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;
import online.stringtek.distributed.toy.rpc.core.handler.RpcRequestEncoder;
import online.stringtek.distributed.toy.rpc.core.handler.RpcResponseDecoder;
import online.stringtek.distributed.toy.rpc.core.serializer.JSONSerializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RpcClient{
    private AtomicLong id;
    private final ExecutorService executorService;
    private static RpcResponseHandler handler;
    public RpcClient(){
        executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        id=new AtomicLong();
    }

    public void connect(String ip,int port) throws InterruptedException {
        handler=new RpcResponseHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //出站
                        nioSocketChannel.pipeline()
                                .addLast(new RpcRequestEncoder(RpcRequest.class,new JSONSerializer()));
                        nioSocketChannel.pipeline()
                                .addLast(new LengthFieldBasedFrameDecoder(RpcConstant.MAX_FRAME_LENGTH,0,4,0,4))
                                .addLast(new RpcResponseDecoder(new JSONSerializer()))
                                .addLast(handler);
                        //入站
                    }
                });
        ChannelFuture future = bootstrap.connect(ip, port).sync();
        log.info("rpc server connected.");
    }

    public Object proxy(String className) throws InterruptedException, ClassNotFoundException {
        return proxy(Class.forName(className));
    }


    @SuppressWarnings("unchecked")
    public <T> T proxy(Class<T> clazz) {
        return (T)Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest rpcRequest=new RpcRequest();
                rpcRequest.setRequestId(String.valueOf(id.incrementAndGet()));
                rpcRequest.setClassName(clazz.getName());
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);
                //TODO 多线程下同时setRpcRequest引发的问题等处理
                handler.setRpcRequest(rpcRequest);
                return executorService.submit(handler).get();
            }
        });
    }


}
