package online.stringtek.distributed.toy.rpc.demo.provider.config;

import online.stringtek.distributed.toy.rpc.server.RpcServer;
import online.stringtek.distributed.toy.rpc.server.handler.RpcRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToyRpcConfig {
    @Bean
    public RpcRequestHandler rpcRequestHandler(){
        return new RpcRequestHandler();
    }
    @Bean
    public RpcServer rpcServer(RpcRequestHandler rpcRequestHandler){
        return new RpcServer(rpcRequestHandler);
    }
}
