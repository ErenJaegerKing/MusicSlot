package org.imusic.netty.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.imusic.netty.codec.ObjDecoder;
import org.imusic.netty.codec.ObjEncoder;
import org.imusic.netty.domain.TimeSlot;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final NettyClient nettyClient;

    public MyChannelInitializer(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new IdleStateHandler(0,20,0));
        //对象传输处理
//        channel.pipeline().addLast(new ObjDecoder(MsgInfo.class));
//        channel.pipeline().addLast(new ObjEncoder(MsgInfo.class));
        channel.pipeline().addLast(new ObjDecoder(TimeSlot.class));
        channel.pipeline().addLast(new ObjEncoder(TimeSlot.class));
        // 在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new MyClientHandler(nettyClient));
    }

}
