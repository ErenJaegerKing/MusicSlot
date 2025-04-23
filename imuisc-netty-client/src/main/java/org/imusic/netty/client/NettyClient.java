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
    // TODO 客户端需要吗
    private Channel channel;

    public ChannelFuture connect(InetSocketAddress address) {
        ChannelFuture f = null;
        try {
            Bootstrap b = new Bootstrap();
            b.group(workGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.AUTO_READ, true);
            b.handler(new MyChannelInitializer());
            // TODO 客户端需要吗 syncUninterruptibly 和sync的区别是什么
            f = b.connect(address).syncUninterruptibly();
            f.addListener(new MyChannelFutureListener(this, address)); //添加监听，处理重连
            // TODO 客户端需要吗
            channel = f.channel();
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (null != f && f.isSuccess()) {
                logger.info("客户端：启动成功");
            } else {
                logger.error("客户端：启动失败");
            }
        }
        return f;
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
