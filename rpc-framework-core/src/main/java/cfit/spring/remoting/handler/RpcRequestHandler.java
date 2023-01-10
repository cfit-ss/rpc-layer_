package cfit.spring.remoting.handler;

import cfit.fbs.exception.RpcException;
import cfit.fbs.factory.SingletonFactory;
import cfit.spring.provider.ServiceProvider;
import cfit.spring.provider.ServiceProviderImpl;
import cfit.spring.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;

/**
 * @author shengshuo
 *
 */
@Slf4j
public class RpcRequestHandler {

    private final ServiceProvider serviceProvider;

    public RpcRequestHandler() {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * Processing rpcRequest: call the corresponding method, and then return the method
     */
    public Object handle(RpcRequest rpcRequest) {
        //获取对应服务者实例
        Object service = serviceProvider.getService(rpcRequest.toRpcProperties());
        return invokeTargetMethod(rpcRequest, service);
    }
    //
    //参数字段-参数字段
    /*
    public  Object invokeTargetMethod_flag(RpcRequest rpcRequest,Object service){
        //声明式Object
        Object result_flag;
        try{
            //参数
           Method method =service.getClass().getMethod(rpcRequest.getRequestName(),rpcRequest.getMethodName());
           //反射机制
            System.out.println("RpcRequestHandler invokeTargetMethod 反射方法："+rpcRequest.getMethodName());
            result = method.invoke(service,rpcRequest.getParameters());//类别限制
            log.info("service:[{}] successful invoke method:[{}]",rpcRequest.getInterfaceName(),rpcRequest.getMethodName());
        }catch(NoSuchAlgorithmException | IllegalArgumentException| InvocationTargetException|IllegalAccessException e){
            //抛出对应字段
            throw new RpcExceprion(e.getMessage(),e);
        }
        //返回生成字段
        return result;//生成字段
    }
    */


    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            //获对应的方法
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            //通过反射获取对应的方法
            System.out.println("RpcRequestHandler invokeTargetMethod 通过反射开始执行服务端类的 方法： " + rpcRequest.getMethodName());
            //限制对应类
            result = method.invoke(service, rpcRequest.getParameters());//限制类别
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        //返回结果-生成参数字段
        return result;

    }
}
