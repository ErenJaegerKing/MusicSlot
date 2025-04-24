package org.imusic.netty.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyChannelFutureListener implements ChannelFutureListener {

    private static Logger log = LoggerFactory.getLogger(MyChannelFutureListener.class);

    private final NettyClient nettyClient;

    private final InetSocketAddress address;

    private int retryCount = 0;

    public MyChannelFutureListener(NettyClient nettyClient, InetSocketAddress address) {
        this.nettyClient = nettyClient;
        this.address = address;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        System.out.println("添加监听，处理重连");
        if (!channelFuture.isSuccess()) {
            long delay = calculateRetryDelay(retryCount);
            retryCount++;
            log.info("开始重试");
            channelFuture.channel().eventLoop().schedule(() -> {
                try {
                    log.info("第{}次尝试重连，" + new Date(), retryCount);
                    nettyClient.connect(address);
                } catch (Exception e) {
                    log.info("第{}次尝试重连，" + new Date(), retryCount, e.getMessage());
                }
            }, delay, TimeUnit.SECONDS);
        } else {
            retryCount = 0;
        }
//        if (channelFuture.isSuccess()) {
//            log.info("连接成功");
//        }
//
//        final EventLoop loop = channelFuture.channel().eventLoop();
//        loop.schedule(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    nettyClient.connect(address);
//                    log.info("断线重连:连接成功");
//                    Thread.sleep(500);
//                } catch (Exception e) {
//                    log.info("断线重连:发生异常，退出重连" + e.getMessage());
//                }
//            }
//        }, 1L, TimeUnit.SECONDS);
    }

    /**
     * 计算重连间隔（2s → 5s → 10s → 30s → 30s...）
     */
    private long calculateRetryDelay(int retryCount) {
        switch (retryCount) {
            case 0: return 2;
            case 1: return 5;
            case 2: return 10;
            default: return 30;
        }
    }
}
