package cfit.javaguide;

//import cfit.fbs.entity.RpcServiceProperties;
import cfit.spring.proxy.RpcClientProxy;
import cfit.spring.remoting.transport.RpcRequestTransport;
import cfit.spring.remoting.transport.socket.SocketRpcClient;

/**
 * @author shengs
 */
public class SocketClientMain {

    public static void main(String[] args) throws InterruptedException {
        RpcRequestTransport rpcRequestTransport = new SocketRpcClient();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder().group("test2").version("version2").build();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcRequestTransport, rpcServiceProperties);
        //直接对接口生成代理增强类
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        String hello1 = helloService.hello(new Hello("111", "第一条"));
        String hello2 = helloService.hello(new Hello("222", "第二条"));
        System.out.println(hello1);
        System.out.println(hello2);

    }

    public static int test() {
        int i1 = 10;
        int i2 = (i1++) + (++i1);
        return i2;
    }

}
