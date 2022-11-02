package cfit.spring.loadbalance.loadbalancer;

import cfit.spring.loadbalance.AbstractLoadBalance;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

/**
 
 *
 * @author shengshuo
 *
 */
@Slf4j
public class RandomLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses, String rpcServiceName) {
        Random random = new Random();
        System.out.println("Successfully  ConsistentHashLoadBalance 调用随机 均衡策略");
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }

}
