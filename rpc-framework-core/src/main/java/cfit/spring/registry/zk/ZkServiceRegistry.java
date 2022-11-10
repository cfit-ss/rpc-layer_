package cfit.spring.registry.zk;

import cfit.spring.registry.ServiceRegistry;
import cfit.spring.registry.zk.util.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * service registration  based on zookeeper
 *
 * @author shengshuo
 *
 */
@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // CuratorUtils.createPersistentNode(zkClient, servicePath);
        //修改为 临时节点
        CuratorUtils.createEphemeralNode(zkClient,servicePath);
    }
}
