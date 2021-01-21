package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.consumer.config;


import lombok.Data;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "toy.rpc")
public class ToyRpcConfiguration {
    @Autowired
    private ToyRpcZooKeeperConfiguration toyRpcZooKeeperConfiguration;
    private String host;
    private int port;
    private ServiceType serviceType;
}
