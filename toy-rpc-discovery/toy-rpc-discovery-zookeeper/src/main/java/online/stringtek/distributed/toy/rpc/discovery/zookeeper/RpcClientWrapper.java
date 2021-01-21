package online.stringtek.distributed.toy.rpc.discovery.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import online.stringtek.distributed.toy.rpc.client.RpcClient;

@Builder
@Data
@AllArgsConstructor
public class RpcClientWrapper {
    private RpcClient rpcClient;
    private int responseTime;//单位ms
    public RpcClientWrapper(RpcClient rpcClient){
        this.rpcClient=rpcClient;
        responseTime=0;
    }
}
