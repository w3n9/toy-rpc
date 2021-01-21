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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Consumer需要使用这个类来维护RpcClient
 */
@Slf4j
public class ToyRpcClientZooKeeperContext {
    private final ZooKeeperRegistry registry;

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

    public void add(String providerName,ServiceInfo serviceInfo) throws InterruptedException, IOException {
        String ip = serviceInfo.getIp();
        int port = serviceInfo.getPort();
        RpcClient rpcClient = new RpcClient();
        rpcClient.connect(ip,port);
        if(!rpcClientMap.containsKey(providerName))
            rpcClientMap.put(providerName,new HashMap<>());
        Map<String, RpcClientWrapper> providerMap = rpcClientMap.get(providerName);
        //如果原来有这个，则先移除再添加
        if(providerMap.containsKey(serviceInfo.getInstancePath())){
            providerMap.get(serviceInfo.getInstancePath()).getRpcClient().close();
        }
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
    @SuppressWarnings("unchecked")
    public <T> T get(String providerName,Class<T> clazz) throws Exception {
        Boolean watched = watchedMap.get(providerName);
        if(watched==null||!watched){
            init(providerName);
            watchedMap.put(providerName,true);
        }
        Map<String, RpcClientWrapper> providerMap = rpcClientMap.get(providerName);
        Collection<RpcClientWrapper> values = providerMap.values();
        return (T)Proxy.newProxyInstance(ToyRpcClientZooKeeperContext.class.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //TODO 使用策略模式
                // 随机访问模式
                Optional<RpcClientWrapper> wrapperOptional = values.stream().skip(new Random().nextInt(values.size())).findFirst();
                RpcClientWrapper rpcClientWrapper = wrapperOptional.orElseGet(null);
                RpcClient rpcClient = rpcClientWrapper.getRpcClient();
                return method.invoke(rpcClient.proxy(clazz),args);
            }
        });
    }
}
