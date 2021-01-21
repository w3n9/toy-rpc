package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.provider.listener;

import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceInfo;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceType;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ZooKeeperRegistry;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.provider.config.ToyRpcConfiguration;
import online.stringtek.distributed.toy.rpc.server.RpcServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class ToyRpcListener implements ApplicationRunner {
    @Autowired
    private RpcServer rpcServer;
    @Autowired
    private ZooKeeperRegistry zooKeeperRegistry;
    @Autowired
    private ToyRpcConfiguration toyRpcConfiguration;
    @Autowired
    private ServiceInfo serviceInfo;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        //启动RPC服务端
        rpcServer.start(toyRpcConfiguration.getHost(), toyRpcConfiguration.getPort());
        //注册zk节点
        zooKeeperRegistry.register(serviceInfo);
        //注册结束Hook，从zk上移除对应的节点
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                zooKeeperRegistry.unRegister(serviceInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }
}
