package online.stringtek.distributed.toy.rpc.demo.provider;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;


@RestController
@SpringBootApplication
public class ToyRpcDemoProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToyRpcDemoProviderApplication.class,args);
    }
}
