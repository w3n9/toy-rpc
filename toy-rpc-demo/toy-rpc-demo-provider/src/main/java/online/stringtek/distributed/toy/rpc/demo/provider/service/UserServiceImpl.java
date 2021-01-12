package online.stringtek.distributed.toy.rpc.demo.provider.service;

import online.stringtek.distributed.toy.rpc.demo.api.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String username) {
        return "Hello, "+username;
    }
}
