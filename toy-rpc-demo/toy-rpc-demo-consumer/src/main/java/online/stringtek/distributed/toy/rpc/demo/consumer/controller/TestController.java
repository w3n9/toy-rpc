package online.stringtek.distributed.toy.rpc.demo.consumer.controller;

import online.stringtek.distributed.toy.rpc.client.RpcClient;
import online.stringtek.distributed.toy.rpc.demo.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    private RpcClient rpcClient;
    @GetMapping("/")
    public String sayHello(){
        UserService userService = rpcClient.proxy(UserService.class);
        return userService.sayHello("Tek");
    }
}
