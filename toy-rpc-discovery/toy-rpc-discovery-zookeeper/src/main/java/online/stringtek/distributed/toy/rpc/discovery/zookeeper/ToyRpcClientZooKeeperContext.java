package online.stringtek.distributed.toy.rpc.discovery.zookeeper;

import com.alibaba.fastjson.JSON;
import online.stringtek.distributed.toy.rpc.client.RpcClient;
import online.stringtek.distributed.toy.rpc.core.serializer.JSONSerializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZKUtil;

import java.io.IOException;
import java.util.*;

/**
 * Consumer需要使用这个类来维护RpcClient
 */
public class ToyRpcClientZooKeeperContext {
    private final ZooKeeperRegistry registry;

    // k 对应的zk节点路径 | v 对应的Rpc客户端
    private final Map<String, Map<String,RpcClient>> rpcClientMap;
    private final Map<String,Boolean> watchedMap;

    public ToyRpcClientZooKeeperContext(ZooKeeperRegistry registry){
        this.registry=registry;
        this.rpcClientMap=new HashMap<>();
        this.watchedMap=new HashMap<>();
    }

    public void remove(String providerName,String path) throws IOException {
        Map<String, RpcClient> providerMap = rpcClientMap.get(providerName);
        RpcClient rpcClient = providerMap.get(path);
        rpcClient.close();
        providerMap.remove(path);
    }
    public void init(String providerName) throws Exception {
        List<ServiceInfo> serviceInfos = registry.get(providerName);
        for (ServiceInfo serviceInfo : serviceInfos)
            add(providerName,serviceInfo);
        watch(providerName);
    }

    public void add(String providerName,ServiceInfo serviceInfo) throws InterruptedException {
        String ip = serviceInfo.getIp();
        int port = serviceInfo.getPort();
        RpcClient rpcClient = new RpcClient();
        rpcClient.connect(ip,port);
        if(!rpcClientMap.containsKey(providerName))
            rpcClientMap.put(providerName,new HashMap<>());
        Map<String, RpcClient> providerMap = rpcClientMap.get(providerName);
        providerMap.put(serviceInfo.getInstancePath(),rpcClient);
    }
    /**
     * 监听provider
     * @param providerName provider名称
     * @throws Exception
     */
    public void watch(String providerName) throws Exception {
        String parentPath="/"+providerName+"/providers";
        PathChildrenCache cache = new PathChildrenCache(registry.getCurator(),parentPath, true);
        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curator, PathChildrenCacheEvent event) throws Exception {
                String newPath = event.getData().getPath();
                System.out.println(new String(event.getData().getData()));
                String path=event.getData().getPath();
                ServiceInfo serviceInfo;
                switch (event.getType()){
                    case CHILD_ADDED:
                        serviceInfo=JSON.parseObject(ZKPaths.getNodeFromPath(newPath),ServiceInfo.class);
                        add(providerName,serviceInfo);
                        break;
                    case CHILD_REMOVED:
                        remove(providerName,path);
                        break;
                    case CHILD_UPDATED:
                        //TODO
                        break;
                }
            }
        });
    }
    public <T> T get(String providerName,Class<T> clazz) throws Exception {
        Boolean watched = watchedMap.get(providerName);
        if(watched==null||!watched){
            init(providerName);
            watchedMap.put(providerName,true);
        }
        //TODO 负载均衡
        Map<String, RpcClient> providerMap = rpcClientMap.get(providerName);
        Set<String> keySet = providerMap.keySet();
        if(keySet.size()==0){
            //TODO 没有服务，处理
        }
        String[] keys = keySet.toArray(new String[0]);
        String key=keys[new Random().nextInt(keys.length)];
        RpcClient rpcClient = providerMap.get(key);
        return rpcClient.proxy(clazz);
    }
}
