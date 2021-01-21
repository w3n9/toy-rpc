package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.provider.config;

import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceInfo;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceType;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ZooKeeperRegistry;
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
    @Bean
    public ZooKeeperRegistry zooKeeperRegistry(ToyRpcZooKeeperConfiguration zooKeeperConfig){
        ZooKeeperRegistry zooKeeperRegistry= ZooKeeperRegistry.builder()
                .connectString(zooKeeperConfig.getIp()+":"+zooKeeperConfig.getPort())
                .build();
        zooKeeperRegistry.init();
        return zooKeeperRegistry;
    }
    @Bean
    public ServiceInfo serviceInfo(ToyRpcConfiguration toyRpcConfiguration){
        return ServiceInfo.builder()
                .name(toyRpcConfiguration.getToyRpcZooKeeperConfiguration().getName())
                .ip(toyRpcConfiguration.getHost())
                .port(toyRpcConfiguration.getPort())
                .serviceType(toyRpcConfiguration.getServiceType())
                .build();
    }
}
