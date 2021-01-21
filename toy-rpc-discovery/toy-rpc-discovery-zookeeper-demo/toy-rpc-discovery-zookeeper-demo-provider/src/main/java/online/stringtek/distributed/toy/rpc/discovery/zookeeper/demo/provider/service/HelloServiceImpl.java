package online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.provider.service;

import online.stringtek.distributed.toy.rpc.discovery.zookeeper.demo.api.HelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String greet(String name) {
        return "Hello, "+name;
    }
}
