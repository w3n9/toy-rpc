package online.stringtek.distributed.rmi.api;

import online.stringtek.distributed.rmi.pojo.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloService extends Remote {
    String sayHello(User user) throws RemoteException;
}
