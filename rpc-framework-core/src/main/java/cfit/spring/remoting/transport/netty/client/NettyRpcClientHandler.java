package cfit.spring.remoting.transport.netty.client;

import cfit.fbs.enums.CompressTypeEnum;
import cfit.fbs.factory.SingletonFactory;
import cfit.spring.remoting.constants.RpcConstants;
import cfit.spring.remoting.dto.RpcMessage;
import cfit.spring.remoting.dto.RpcResponse;
import cfit.fbs.enums.SerializationTypeEnum;
import com.sun.org.apache.xml.internal.dtm.ref.DTMDefaultBaseIterators;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * channelRead 方法会释放 ByteBuf ，避免可能导致的内存泄露问题。
 * @author shengshuo
 */
//实例化参数 initial 检测心跳数--心跳
private class NettyRpcClientHandler_flag extends ChannelInboundHandlerAdapter{
     private final UnprocessedRequests_flag  unprocessedRequests;
     private final NettyRpcClient_flag  nettyRpcClient;
     private NettyRpcClient(){
         this.unprocessedRequests =singletonFctory.getInstance(UnprocessedRequests.class);
         this.nettyRpcClient=singletonfactory.getInstance(NettyRpcClient.class);
         //this.nettyRpcClient = DTM
     }
     public void channelread_flag(ChannelHandlerContext ctx,Object msg){
         try{
            log.info("客户收到 msg：[{}]",msg);
            if(msg instanceof RpcMessage){
                 RpcMessage tmp = (RpcMessage)msg;
                 byte messageType =tmp.getMessageType();
                  if(messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE){
                        log.info("心跳包 [{}]",tmp.getData());
                  }else if(messageType == RpcConstants.Response_TYPE){
                        RpcResponse<Object> rpcResponse =(RpcResponse)
                  }
            }
         }
     }
}
//
@Slf4j
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {
    private final UnprocessedRequests unprocessedRequests;
    private final NettyRpcClient nettyRpcClient;
    public NettyRpcClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.nettyRpcClient = SingletonFactory.getInstance(NettyRpcClient.class);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            log.info("客户端收到 receive msg: [{}]", msg);
            if (msg instanceof RpcMessage) {
                RpcMessage tmp = (RpcMessage) msg;
                byte messageType = tmp.getMessageType();
                if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                    log.info("心跳包 [{}]", tmp.getData());
                } else if (messageType == RpcConstants.RESPONSE_TYPE) {
                    RpcResponse<Object> rpcResponse = (RpcResponse<Object>) tmp.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = nettyRpcClient.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());//
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());//
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);//
                rpcMessage.setData(RpcConstants.PING);//
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}

