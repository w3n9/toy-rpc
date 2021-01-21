package online.stringtek.distributed.toy.rpc.discovery.zookeeper;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;


/**
 * 非线程安全
 */
@Data
@Builder
public class ZooKeeperRegistry {
    private static final String DEFAULT_NAMESPACE="toy-rpc";
    private String connectString;
    private String namespace;
    private CuratorFramework curator;
    public void init(){
        curator= CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(1000,3))
                .namespace(namespace==null?DEFAULT_NAMESPACE:namespace)
                .build();
        curator.start();
    }

    /**
     * 注册自身服务到ZooKeeper上
     * @param serviceInfo 服务信息
     * @throws Exception
     */
    public String register(ServiceInfo serviceInfo) throws Exception {
        String path="/"+serviceInfo.getName();
        if(serviceInfo.getServiceType()==ServiceType.PROVIDER)
            path+="/providers";
        else if(serviceInfo.getServiceType()==ServiceType.CONSUMER)
            path+="/consumers";
        path+="/instance-";
        String instancePath = curator.create().creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath(path);
        serviceInfo.setInstancePath(instancePath);
        curator.setData().forPath(instancePath,JSON.toJSONBytes(serviceInfo));
        return instancePath;
    }

    public void unRegister(ServiceInfo serviceInfo) throws Exception {
        curator.delete().forPath(serviceInfo.getInstancePath());
    }

    public List<ServiceInfo> get(String appName) throws Exception {
        List<ServiceInfo> ans=new ArrayList<>();
        List<String> instancePathList = curator.getChildren().forPath("/" + appName+"/providers");
        for (String instancePath : instancePathList) {
            String path="/"+appName+"/providers/"+instancePath;
            ans.add(JSON.parseObject(curator.getData().forPath(path),ServiceInfo.class));
        }
        return ans;
    }
}
