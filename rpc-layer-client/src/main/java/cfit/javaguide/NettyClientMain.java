package cfit.javaguide;

import spring.annotation.RpcScan;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author shengs
 *
 */
@RpcScan(basePackage = {"github.javaguide"})
public class NettyClientMain {

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 10000,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(10), new DefaultThreadFactory("mmy"), new ThreadPoolExecutor.AbortPolicy());

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(NettyClientMain.class);
        HelloController helloController = (HelloController) applicationContext.getBean("helloController");

        threadPoolExecutor.submit(() -> {
            try {
                helloController.setHelloService();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        threadPoolExecutor.submit(() -> {
            try {
                //  helloController.getUser();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }
}
