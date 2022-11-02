package cfit.spring.loadbalance;

import java.util.List;

/**
 *
 * @author shengshuo
 *
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            //获取第一个
            System.out.println("默认地址服务是：" + serviceAddresses);
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses, rpcServiceName);
    }
    protected abstract String doSelect(List<String> serviceAddresses, String rpcServiceName);

}
