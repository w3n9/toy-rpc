package online.stringtek.distributed.toy.rpc.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import online.stringtek.distributed.toy.rpc.core.common.RpcRequest;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestHandler extends ChannelInboundHandlerAdapter implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
    //基于类的远程调用
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("{}",msg);
        try{
            RpcRequest rpcRequest=(RpcRequest) msg;//可能类型转换错误
            Class<?> clazz = Class.forName(rpcRequest.getClassName());//接口
            String methodName = rpcRequest.getMethodName();
            Object[] parameters = rpcRequest.getParameters();
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            Object object = applicationContext.getBean(clazz);
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(object, parameters);
            log.info("result:{}",result);
            //写回客户端
            ctx.channel().writeAndFlush(result);
        }catch (Exception e){
            e.printStackTrace();
            log.error("{}:{}",e,e.getMessage());
            //TODO 返回非法请求
        }
    }

}
