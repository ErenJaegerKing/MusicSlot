package com.ruoyi.system.netty.web;

import com.ruoyi.system.netty.server.NettyServer;
import io.netty.channel.ChannelFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;


@Component
public class NettyApplication implements CommandLineRunner {

    @Value("${netty.host}")
    private String host;

    @Value("${netty.port}")
    private int port;

    @Autowired
    private NettyServer nettyServer;

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(host, port);
        ChannelFuture channelFuture = nettyServer.bing(address);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nettyServer.destroy()));
        channelFuture.channel().closeFuture().syncUninterruptibly();
    }

}
