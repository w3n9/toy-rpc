package online.stringtek.distributed.toy.rpc.client;

public interface IRpcClient{
    void connect(String ip,int port) throws InterruptedException;
    Object proxy(String className) throws ClassNotFoundException;
    <T> T proxy(Class<T> clazz);
}
