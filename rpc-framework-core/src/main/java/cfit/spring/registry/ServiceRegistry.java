package cfit.spring.registry;

import cfit.fbs.extension.SPI;

import java.net.InetSocketAddress;

/**
 * service registration
 *
 * @author shengshuo
 *
 */
@SPI
public interface ServiceRegistry {
    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
