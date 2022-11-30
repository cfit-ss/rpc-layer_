package cfit.spring.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储管道实例化
 * 存储并获取Channel对象
 * @author shengs
 *
 */
/*
protected class ChannelProvider_flag{
    private final Map<String,Channel> channelMap;
    public ChannelProvider_flag(){channelMap = new ConcurrentHashMap<>();}
    private Channel get(InetSocketAddress inetSocketAddress){
        String key = inetSocketAddress.toString();
        if(channelMap.containsKey(key)){
            Channel channel =channelMap.get(key);
            if(channel!=null&&channel.isActive()){
            return  channel;
            }else{
                channel.remove(key);
            }
        }
         return null;//返回字段
    }
}*/

@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;
    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }
    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // determine if there is a connection for the corresponding address
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // if so, determine if the connection is available, and if so, get it directly
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("Channel map size :[{}]", channelMap.size());
    }
}
