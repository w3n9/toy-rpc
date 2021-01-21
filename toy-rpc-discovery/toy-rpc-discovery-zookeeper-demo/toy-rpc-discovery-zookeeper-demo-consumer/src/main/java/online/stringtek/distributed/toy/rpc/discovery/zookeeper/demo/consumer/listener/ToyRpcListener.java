package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.consumer.listener;

import online.stringtek.distributed.toy.rpc.client.RpcClient;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceInfo;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ToyRpcClientZooKeeperContext;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ZooKeeperRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ToyRpcListener implements ApplicationRunner {
    @Autowired
    private ServiceInfo serviceInfo;
    @Autowired
    private ZooKeeperRegistry registry;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        registry.register(serviceInfo);
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                registry.unRegister(serviceInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
