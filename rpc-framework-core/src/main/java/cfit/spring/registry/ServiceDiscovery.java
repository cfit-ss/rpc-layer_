package cfit.spring.registry;

import cfit.fbs.extension.SPI;

import java.net.InetSocketAddress;

/**
 * service discovery
 *
 * @author shengshuo
 *
 */
@SPI
public interface ServiceDiscovery {
    /**
     * @param rpc service name
     * @return service address
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
