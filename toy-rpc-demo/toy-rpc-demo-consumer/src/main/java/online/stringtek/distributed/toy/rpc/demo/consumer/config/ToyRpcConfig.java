package online.stringtek.distributed.toy.rpc.demo.consumer.config;

import online.stringtek.distributed.toy.rpc.client.RpcClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToyRpcConfig {
    @Bean
    public RpcClient rpcClient(){
        return new RpcClient();
    }
}
