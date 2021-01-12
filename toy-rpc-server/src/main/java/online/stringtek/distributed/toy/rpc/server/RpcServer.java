package online.stringtek.distributed.toy.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;
import online.stringtek.distributed.toy.rpc.core.handler.RpcDecoder;
import online.stringtek.distributed.toy.rpc.core.serializer.JSONSerializer;
import online.stringtek.distributed.toy.rpc.server.handler.RpcHandler;

public class RpcServer {
    public void start(String ip,int port) throws InterruptedException {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) {
                        nioSocketChannel.pipeline()
                                .addLast(new RpcDecoder(RpcRequest.class,new JSONSerializer()))
                                .addLast(new RpcHandler());
                    }
                });
//        ChannelFuture future = serverBootstrap.bind(port).sync();
        serverBootstrap.bind(port);
        //同步监听
        System.out.println("listening on"+ip+":"+port);

    }

    public static void main(String[] args) throws InterruptedException {
        new RpcServer().start("127.0.0.1",23231);
    }

}
