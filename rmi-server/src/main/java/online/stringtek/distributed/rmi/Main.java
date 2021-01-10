package online.stringtek.distributed.rmi;

import online.stringtek.distributed.rmi.api.HelloService;
import online.stringtek.distributed.rmi.service.HelloServiceImpl;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static void main(String[] args) throws RemoteException, AlreadyBoundException, MalformedURLException {
        LocateRegistry.createRegistry(8888);
        HelloService helloService=new HelloServiceImpl();
        Naming.bind("rmi://127.0.0.1:8888/hello",helloService);
    }
}
