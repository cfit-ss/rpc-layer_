package cfit.spring.proxy;

import cfit.fbs.entity.RpcServiceProperties;
import cfit.fbs.enums.RpcErrorMessageEnum;
import cfit.fbs.enums.RpcResponseCodeEnum;
import cfit.fbs.exception.RpcException;
import cfit.spring.remoting.dto.RpcRequest;
import cfit.spring.remoting.dto.RpcResponse;
import cfit.spring.remoting.transport.RpcRequestTransport;
import cfit.spring.remoting.transport.netty.client.NettyRpcClient;
import cfit.spring.remoting.transport.socket.SocketRpcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author shengs
 *
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    /**
     * 发送请求到服务端
     */
    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceProperties rpcServiceProperties) {
        this.rpcRequestTransport = rpcRequestTransport;
        if (rpcServiceProperties.getGroup() == null) {
            rpcServiceProperties.setGroup("");
        }
        if (rpcServiceProperties.getVersion() == null) {
            rpcServiceProperties.setVersion("");
        }
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, cfit.javaguide.RpcServiceProperties rpcServiceProperties) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceProperties = RpcServiceProperties.builder().group("").version("").build();
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, cfit.javaguide.RpcServiceProperties rpcServiceProperties) {
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, cfit.javaguide.RpcServiceProperties rpcServiceProperties) {
    }

    /**
     * 获取代理实例化proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        //创建类的实例
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * 当您使用代理对象调用方法时，实际上会调用此方法。
     * 代理对象是通过getProxy方法获得的对象。
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion())
                .build();

        RpcResponse<Object> rpcResponse = null;
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<Object>> completableFuture =
                    (CompletableFuture<RpcResponse<Object>>) rpcRequestTransport.sendRpcRequest(rpcRequest);
            // CompletableFuture的get()方法值会阻塞主线程，直到子线程执行任务完成返回结果才会取消阻塞
            //todo 是否可以用线程池解决
            System.out.println("客户端开始get()消息：" + System.currentTimeMillis() / 1000);
            rpcResponse = completableFuture.get();
            System.out.println("客户端已经 得到 消息：" + rpcResponse + System.currentTimeMillis() / 1000);
        }

        if (rpcRequestTransport instanceof SocketRpcClient) {
            rpcResponse = (RpcResponse<Object>) rpcRequestTransport.sendRpcRequest(rpcRequest);
        }

        this.check(rpcResponse, rpcRequest);

        return rpcResponse.getData();
    }

    private void check(RpcResponse<Object> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcErrorMessageEnum.REQUEST_NOT_MATCH_RESPONSE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCodeEnum.SUCCESS.getCode())) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
