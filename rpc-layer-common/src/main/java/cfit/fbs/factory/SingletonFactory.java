package cfit.fbs.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取单例对象的工厂类
 *
 * @author shengshuo
 *
 */
public final class SingletonFactory {

    private static final Map<String, Object> OBJECT_MAP = new HashMap<>();

    private SingletonFactory() {
    }


    public static <T> T getInstance(Class<T> c) {
        String key = c.toString();
        Object instance = OBJECT_MAP.get(key);
        if (instance != null) {
            return c.cast(instance);
        }
       //同步消息协议flag
        synchronized (SingletonFactory.class) {
            instance = OBJECT_MAP.get(key);
            if (instance == null) {
                try {
                    instance = c.getDeclaredConstructor().newInstance();
                    OBJECT_MAP.put(key, instance);
                } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }

        return c.cast(instance);
    }


    //抛出 ClassNotFoundException异常;基于Class.forName()中抛出
    public static void main(String[] args) throws ClassNotFoundException {
        //使用 Object中的getClass()  方法获得
        Date date = new Date();
        Class dateClass_1 = date.getClass();
        System.out.println(dateClass_1);

        //使用 类名.class 来获取
        //这个是JVM支持的，但是得先把包导进来
        Class dateClass_2 = Date.class;
        System.out.println(dateClass_2);

        //利用Class中的forName()方法获取类
        Class dateCLass_3 = Class.forName("java.util.Date");
        System.out.println(dateCLass_3);
    }


}
