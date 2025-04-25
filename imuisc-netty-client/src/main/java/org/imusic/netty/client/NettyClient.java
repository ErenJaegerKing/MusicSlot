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
import java.util.concurrent.TimeUnit;

@Component("nettyClient")
public class NettyClient {

    private Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private int retryCount = 0;

    private long delay = 2;

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
            b.handler(new MyChannelInitializer(this));
            channelFuture = b.connect(address).syncUninterruptibly();
            channelFuture.addListener(new MyChannelFutureListener(this, address)); //添加监听，处理重连
            channel = channelFuture.channel();
        } catch (Exception e) {
            logger.error(e.getMessage());
            scheduleReconnect(address);
        } finally {
            if (null != channelFuture && channelFuture.isSuccess()) {
                logger.info("启动成功");
                retryCount = 0;
                delay = 0;
            } else {
                logger.error("启动失败");
            }
        }
        return channelFuture;
    }

    /**
     * 独立的重连调度逻辑（用于首次连接失败）
     */
    private void scheduleReconnect(InetSocketAddress address) {
        delay = calculateRetryDelay(retryCount);
        workGroup.schedule(() -> {
            retryCount++;
            logger.warn("第{}次连接失败，尝试重连...",retryCount);
            connect(address);
        }, 2, TimeUnit.SECONDS);
    }

    /**
     * 计算重连间隔（2s → 5s → 10s → 30s → 30s...）
     */
    private long calculateRetryDelay(int retryCount) {
        switch (retryCount) {
            case 0:
                return 2;
            case 1:
                return 5;
            case 2:
                return 10;
            default:
                return 30;
        }
    }

    public void destroy() {
        if (null == channel) return;
        channel.close();
        workGroup.shutdownGracefully();
    }

    public Channel getChannel() {
        return channel;
    }


}
