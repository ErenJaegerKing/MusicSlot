package com.ruoyi.system.netty.server;

import com.ruoyi.system.netty.codec.ObjDecoder;
import com.ruoyi.system.netty.codec.ObjEncoder;
import com.ruoyi.system.domain.TimeSlot;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) {
        /**
         * 心跳监测
         * 1、readerIdleTimeSeconds 读超时时间
         * 2、writerIdleTimeSeconds 写超时时间
         * 3、allIdleTimeSeconds    读写超时时间
         * 4、TimeUnit.SECONDS 秒[默认为秒，可以指定]
         */
        channel.pipeline().addLast(new IdleStateHandler(60, 0, 0));
        //对象传输处理
//        channel.pipeline().addLast(new ObjDecoder(MsgInfo.class));
//        channel.pipeline().addLast(new ObjEncoder(MsgInfo.class));
        channel.pipeline().addLast(new ObjDecoder(TimeSlot.class));
        channel.pipeline().addLast(new ObjEncoder(TimeSlot.class));
        // 在管道中添加我们自己的接收数据实现方法
        channel.pipeline().addLast(new MyServerHandler());
    }

}