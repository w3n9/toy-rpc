package online.stringtek.distributed.rmi;

import online.stringtek.distributed.rmi.api.HelloService;
import online.stringtek.distributed.rmi.pojo.User;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Main {
    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        HelloService helloService = (HelloService) Naming.lookup("rmi://127.0.0.1:8888/hello");
        System.out.println(helloService.sayHello(User.builder().name("StringTek").build()));
    }
}
