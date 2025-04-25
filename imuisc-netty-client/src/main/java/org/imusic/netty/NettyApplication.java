package org.imusic.netty;

import io.netty.channel.ChannelFuture;
import org.imusic.netty.client.NettyClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.net.InetSocketAddress;

@SpringBootApplication(scanBasePackages = "org.imusic.netty")
public class NettyApplication implements CommandLineRunner {

    @Value("${netty.host}")
    private String host;
    @Value("${netty.port}")
    private int port;

    @Resource
    private NettyClient nettyClient;

    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        InetSocketAddress address = new InetSocketAddress(host, port);
        ChannelFuture channelFuture = nettyClient.connect(address);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> nettyClient.destroy()));
        channelFuture.channel().closeFuture().syncUninterruptibly();
    }

}
