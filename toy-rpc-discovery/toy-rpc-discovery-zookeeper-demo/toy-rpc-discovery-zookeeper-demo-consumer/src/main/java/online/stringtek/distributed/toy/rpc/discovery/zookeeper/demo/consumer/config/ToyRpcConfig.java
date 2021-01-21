package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.consumer.config;

import online.stringtek.distributed.toy.rpc.client.RpcClient;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceInfo;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ToyRpcClientZooKeeperContext;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ZooKeeperRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToyRpcConfig {
    @Bean
    public ZooKeeperRegistry zooKeeperRegistry(ToyRpcZooKeeperConfiguration zooKeeperConfig){
        ZooKeeperRegistry zooKeeperRegistry= ZooKeeperRegistry.builder()
                .connectString(zooKeeperConfig.getIp()+":"+zooKeeperConfig.getPort())
                .build();
        zooKeeperRegistry.init();
        return zooKeeperRegistry;
    }
    @Bean
    public ToyRpcClientZooKeeperContext toyRpcClientZooKeeperContext(ZooKeeperRegistry registry){
        return new ToyRpcClientZooKeeperContext(registry);
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
