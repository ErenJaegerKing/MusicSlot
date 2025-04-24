package com.ruoyi.framework.netty.server;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.framework.netty.util.MsgUtil;
import com.ruoyi.system.domain.TimeSlot;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyServerHandler extends ChannelInboundHandlerAdapter {

    private Logger log = LoggerFactory.getLogger(MyServerHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
//                ctx.writeAndFlush(MsgUtil.buildMsg("Request","读取等待：客户端你在吗？"));
                ctx.writeAndFlush(MsgUtil.buildTimeSlotMsg("读超时了，客户端你还在吗？"));
//                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
//                log.info("写空闲：=> Write Idle");
//                ctx.writeAndFlush(MsgUtil.buildMsg("Request","写入等待：客户端你在吗？"));
            } else if (e.state() == IdleState.ALL_IDLE) {
//                log.info("读写空闲：=> All_IDLE");
//                ctx.writeAndFlush(MsgUtil.buildMsg("Request","读写空闲：客户端你在吗?"));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
//        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelHandler.channelGroup.add(ctx.channel());
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("服务端：链接报告开始");
        log.info("服务端：链接报告信息：有一客户端链接到本服务端");
        log.info("服务端：链接报告IP:{}", channel.localAddress().getHostString());
        log.info("服务端：链接报告Port:{}", channel.localAddress().getPort());
        log.info("服务端：链接报告完毕");

        String str = "服务端通知客户端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
        ChannelHandler.channelGroup.writeAndFlush(MsgUtil.buildTimeSlotMsg(str));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开链接{}", ctx.channel().localAddress().toString());
        ChannelHandler.channelGroup.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TimeSlot timeSlot = (TimeSlot) msg;
//        log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 服务端接收到消息：" + timeSlot);
//        log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息类型：" + msg.getClass());
        log.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息内容：" + JSON.toJSONString(((TimeSlot) msg).getSlotName()));
//        String str = "服务端接收到客户端消息：" + new Date() + " " + msg + "\r\n";
//        ChannelHandler.channelGroup.writeAndFlush(MsgUtil.buildTimeSlotMsg(str));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("异常信息：\r\n" + cause.getMessage());
        ctx.close();
    }

}