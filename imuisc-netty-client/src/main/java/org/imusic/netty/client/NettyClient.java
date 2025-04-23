package org.imusic.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component("nettyClient")
public class NettyClient {

    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    //配置客户端NIO线程组
    private final EventLoopGroup workGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture connect(InetSocketAddress address) {
        ChannelFuture channelFuture = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new MyChannelInitializer());

            channelFuture = b.connect(address).syncUninterruptibly();
            channel = channelFuture.channel();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                logger.info("客户端：启动成功");
            } else {
                logger.error("客户端：启动失败");
            }
        }
        return channelFuture;
    }

    /**
     * 关闭客户端
     */
    public void destroy() {
        if (null == channel) return;
        channel.close();
        workGroup.shutdownGracefully();
    }

    /**
     * 获取通道
     * @return Channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * 发送消息
     * @param message 消息内容
     */
    public void sendMessage(Object message) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(message);
        } else {
            logger.error("客户端发送失败，通道不畅通");
        }
    }

}
