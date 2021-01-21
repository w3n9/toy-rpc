package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.consumer.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "toy.rpc.discovery.zookeeper")
public class ToyRpcZooKeeperConfiguration {
    private String ip;
    private int port;
    private String name;
}
