package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.consumer.controller;

import online.stringtek.distributed.toy.rpc.discovery.zookeeper.ToyRpcClientZooKeeperContext;
import online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @Autowired
    private ToyRpcClientZooKeeperContext ctx;

    @GetMapping("/greet")
    public String greet(String name) throws Exception {
        HelloService helloService = ctx.get("demo-provider", HelloService.class);
        return helloService.greet(name);
    }

}
