package cfit.javaguide;

import spring.annotation.RpcReference;
import org.springframework.stereotype.Component;

/**
 * @author shengs
 */
@Component
public class HelloController {

    @RpcReference(version = "version1", group = "test1")
    private HelloService helloService;

    @RpcReference(version = "version1", group = "user1")
    private UserService userService;


    public void setHelloService() throws InterruptedException {
        System.out.println("HelloController.setHelloService 开始" + System.currentTimeMillis() / 1000);
        String hello = this.helloService.hello(new Hello("‘’‘’‘’’‘’‘’‘’‘’‘’‘’‘’‘’‘’‘’‘’‘’‘’‘‘’‘’‘’‘’‘’‘’‘111", "222"));
        //如需使用 assert 断言，需要在 VM options 添加参数：-ea
        for (int i = 0; i < 1; i++) {
            //在执行 hello之前 会先执行 代理对象的invoke 方法
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }

    public void getUser() throws InterruptedException {
        System.out.println("HelloController.getUser 开始" + System.currentTimeMillis() / 1000);
        User users = this.userService.getUsers(1);
        System.out.println("HelloController getUser 收到 " + users);
        for (int i = 0; i < 1; i++) {
            System.out.println(helloService.hello(new Hello("111", "222")));
        }
    }
}

















