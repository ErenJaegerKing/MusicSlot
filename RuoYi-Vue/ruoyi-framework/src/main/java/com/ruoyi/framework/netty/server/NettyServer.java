package com.ruoyi.framework.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component("nettyServer")
public class NettyServer {

    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    //配置服务端NIO线程组
    private final EventLoopGroup parentGroup = new NioEventLoopGroup(); //NioEventLoopGroup extends MultithreadEventLoopGroup Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", NettyRuntime.availableProcessors() * 2));
    private final EventLoopGroup childGroup = new NioEventLoopGroup();
    private Channel channel;

    public ChannelFuture bing(InetSocketAddress address) {
        ChannelFuture channelFuture = null;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(parentGroup, childGroup)
                    .channel(NioServerSocketChannel.class)    //非阻塞模式
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new MyChannelInitializer());

            channelFuture = b.bind(address).syncUninterruptibly();
            channel = channelFuture.channel();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                logger.info("NettyServer启动成功");
            } else {
                logger.error("NettyServer启动失败");
            }
        }
        return channelFuture;
    }

    public void destroy() {
        if (null == channel) return;
        channel.close();
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
    }

    public Channel getChannel()
    {
        return channel;
    }

    /**
     * 向所有连接的客户端发送消息
     */
    public void sendMessageToAllClients(Object message) {
        if (channel != null) {
            // 获取所有活跃的客户端channel
            ChannelGroup channels = ChannelHandler.channelGroup;
            if (channels != null) {
                channels.writeAndFlush(message).addListener(future -> {
                    if (!future.isSuccess()) {
                        logger.error("消息发送失败", future.cause());
                    }
                });
            }
        }
    }

    /**
     * 向特定客户端发送消息
     */
    public void sendMessageToClient(Channel clientChannel, Object message) {
        if (clientChannel != null && clientChannel.isActive()) {
            clientChannel.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    logger.error("消息发送失败", future.cause());
                }
            });
        }
    }
}