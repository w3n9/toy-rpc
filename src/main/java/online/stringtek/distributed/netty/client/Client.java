package online.stringtek.distributed.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        //创建线程池
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        ChannelFuture channelFuture = bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    protected void initChannel(Channel channel) throws Exception {
                        channel.pipeline()
                                .addLast(new StringEncoder());
                    }
                })
                .connect("127.0.0.1", 34500);
        Channel channel = channelFuture.channel();
        System.out.println("连接成功");
        while(true){
            System.out.println("write");
            channel.writeAndFlush("草泥马了隔壁");
            Thread.sleep(2000);
        }
    }
}
