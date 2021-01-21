package online.stringtek.distributed.toy.rpc.discovery.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import online.stringtek.distributed.toy.rpc.client.RpcClient;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class RpcClientWrapper {
    private RpcClient rpcClient;
    public RpcClientWrapper(RpcClient rpcClient){
        this.rpcClient=rpcClient;
    }
}
