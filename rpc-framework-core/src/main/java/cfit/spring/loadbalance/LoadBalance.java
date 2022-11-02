package cfit.spring.loadbalance;

import cfit.fbs.extension.SPI;

import java.util.List;

/**
 *
 * @author shengshuo
 *
 */
@SPI
public interface LoadBalance {
    /**
     *
     * @param serviceAddresses Service address list
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceAddresses, String rpcServiceName);
}
