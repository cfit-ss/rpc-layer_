package cfit.spring.remoting.transport.netty.server;

import cfit.spring.config.CustomShutdownHook;
import cfit.spring.remoting.transport.netty.codec.RpcMessageDecoder;
import cfit.spring.remoting.transport.netty.codec.RpcMessageEncoder;
import cfit.fbs.entity.RpcServiceProperties;
import cfit.fbs.factory.SingletonFactory;
import cfit.spring.provider.ServiceProvider;
import cfit.spring.provider.ServiceProviderImpl;
import cfit.fbs.utils.RuntimeUtil;
import cfit.fbs.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author shengshuo
 */
@Slf4j
@Component
public class NettyRpcServer {

    //todo 服务器节点端口设置
    public static final int PORT = 9999;

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);

    public void registerService(Object service, RpcServiceProperties rpcServiceProperties) {
        serviceProvider.publishService(service, rpcServiceProperties);
    }

    @SneakyThrows
    public void start() {
        System.out.println("Netty 服务器开始启动");
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtils.createThreadFactory("service-handler-group", false)
        );
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                        }
                    });
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = b.bind(host, PORT).sync();
            if (f.isSuccess()) {
                log.error("occur success when start server:");
            } else {
                log.info("occur exception when start server: {}", f.get());
            }
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server: ", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
        System.out.println("Netty 服务器结束启动");
    }


}
