package cfit.spring.remoting.transport;

import cfit.fbs.extension.SPI;
import cfit.spring.remoting.dto.RpcRequest;

/**
 * send RpcRequest
 * @author shengs
 *
 */
@SPI
public interface RpcRequestTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
