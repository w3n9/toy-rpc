package online.stringtek.distributed.toy.rpc.discovery.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ServiceInfo {
    private ServiceType serviceType;
    private String name;
    private String ip;
    private int port;
    private String instancePath;
    private long executeTime;
    private long responseTimeStamp;
}
