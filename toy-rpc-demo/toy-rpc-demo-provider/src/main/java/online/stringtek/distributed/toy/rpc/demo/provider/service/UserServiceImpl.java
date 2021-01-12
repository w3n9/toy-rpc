package online.stringtek.distributed.toy.rpc.demo.provider.service;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String username) {
        return "Hello, "+username;
    }
}
