package online.stringtek.distributed.rmi.service;

import online.stringtek.distributed.rmi.api.HelloService;
import online.stringtek.distributed.rmi.pojo.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

    public HelloServiceImpl() throws RemoteException {
    }

    public String sayHello(User user) throws RemoteException {
        return "Hello "+user.getName();
    }
}
