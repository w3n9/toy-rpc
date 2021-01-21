package online.stringtek.distributed.toy.rpc.discovery.zookeeper;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Consumer需要使用这个类来维护RpcClient
 */
@Slf4j
public class ToyRpcClientZooKeeperContext {
    private final ZooKeeperRegistry registry;

    // k 对应的zk节点路径 | v 对应的Rpc客户端
    private final Map<String, Map<String,RpcClientWrapper>> rpcClientMap;
    private final Map<String,Boolean> watchedMap;

    public ToyRpcClientZooKeeperContext(ZooKeeperRegistry registry){
        this.registry=registry;
        this.rpcClientMap=new HashMap<>();
        this.watchedMap=new HashMap<>();
    }

    public void remove(String providerName,String path) throws IOException {
        Map<String, RpcClientWrapper> providerMap = rpcClientMap.get(providerName);
        RpcClient rpcClient = providerMap.get(path).getRpcClient();
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
        Map<String, RpcClientWrapper> providerMap = rpcClientMap.get(providerName);
        providerMap.put(serviceInfo.getInstancePath(),new RpcClientWrapper(rpcClient));
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
                String path=event.getData().getPath();
                log.info(path);
                ServiceInfo serviceInfo;
                switch (event.getType()){
                    case CHILD_ADDED:
                        //不处理，因为我是先create节点之后再set值
                        break;
                    case CHILD_REMOVED:
                        remove(providerName,path);
                        log.info("provider {} down.",event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        byte[] bytes = curator.getData().forPath(path);
                        JSON.parseObject(bytes,ServiceInfo.class);
                        serviceInfo=JSON.parseObject(new String(bytes),ServiceInfo.class);
                        add(providerName,serviceInfo);
                        log.info("provider {} up.", serviceInfo);
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
        Map<String, RpcClientWrapper> providerMap = rpcClientMap.get(providerName);
        if(providerMap==null){
            //TODO 没有服务，处理
            return null;
        }
        Set<String> keySet = providerMap.keySet();
        if(keySet.size()==0){
            //TODO 没有服务，处理
            return null;
        }
        String[] keys = keySet.toArray(new String[0]);
        String key=keys[new Random().nextInt(keys.length)];
        RpcClient rpcClient = providerMap.get(key).getRpcClient();
        return rpcClient.proxy(clazz);
    }
}
