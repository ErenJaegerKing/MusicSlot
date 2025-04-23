package org.imusic.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class MyChannelFutureListener implements ChannelFutureListener {
    private final NettyClient nettyClient;

    private final InetSocketAddress address;

    public MyChannelFutureListener(NettyClient nettyClient, InetSocketAddress address) {
        this.nettyClient = nettyClient;
        this.address = address;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (channelFuture.isSuccess()) {
            System.out.println("1111111");
        }
        final EventLoop loop = channelFuture.channel().eventLoop();
        loop.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    nettyClient.connect(address);
                    System.out.println("11111断线重连");
                    Thread.sleep(500);
                } catch (Exception e) {
                    System.out.println("11111断线重连失败，退出重连");
                }
            }
        }, 1L, TimeUnit.SECONDS);
    }
}
