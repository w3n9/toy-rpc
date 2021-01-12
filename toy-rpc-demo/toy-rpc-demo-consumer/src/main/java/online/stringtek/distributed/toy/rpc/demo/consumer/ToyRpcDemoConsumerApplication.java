package online.stringtek.distributed.toy.rpc;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToyRpcDemoConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ToyRpcDemoProviderApplication.class,args);
    }
}
