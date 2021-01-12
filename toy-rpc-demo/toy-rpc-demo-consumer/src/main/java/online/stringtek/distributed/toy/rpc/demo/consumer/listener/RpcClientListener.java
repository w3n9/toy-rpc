package online.stringtek.distributed.toy.rpc.demo.consumer.listener;

import online.stringtek.distributed.toy.rpc.client.RpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RpcClientListener implements ApplicationRunner {
    @Autowired
    private RpcClient rpcClient;
    @Value("${toy.rpc.remote-host}")
    private String remoteHost;
    @Value("${toy.rpc.port}")
    private int port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        rpcClient.connect(remoteHost,port);
    }
}
