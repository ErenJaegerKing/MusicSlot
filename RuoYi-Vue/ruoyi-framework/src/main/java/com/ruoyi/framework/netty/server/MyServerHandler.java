package com.ruoyi.framework.netty.server;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.framework.netty.util.MsgUtil;
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

    private Logger logger = LoggerFactory.getLogger(MyServerHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                System.out.println("读取等待：=> Reader Idle");
                ctx.writeAndFlush("读取等待：客户端你在吗... ...\r\n");
            } else if (e.state() == IdleState.WRITER_IDLE) {
                System.out.println("写入等待：=> Write Idle");
                ctx.writeAndFlush("写入等待：客户端你在吗... ...\r\n");
            } else if (e.state() == IdleState.ALL_IDLE) {
                System.out.println("全部时间：=> All_IDLE");
                ctx.writeAndFlush("全部时间：客户端你在吗... ...\r\n");
            }
        }
        ctx.flush();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelHandler.channelGroup.add(ctx.channel());
        SocketChannel channel = (SocketChannel) ctx.channel();
        logger.info("服务端：链接报告开始");
        logger.info("服务端：链接报告信息：有一客户端链接到本服务端");
        logger.info("服务端：链接报告IP:{}", channel.localAddress().getHostString());
        logger.info("服务端：链接报告Port:{}", channel.localAddress().getPort());
        logger.info("服务端：链接报告完毕");

        String str = "服务端通知客户端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
        ChannelHandler.channelGroup.writeAndFlush(MsgUtil.buildMsg(channel.id().toString(), str));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端断开链接{}", ctx.channel().localAddress().toString());
        ChannelHandler.channelGroup.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 服务端接收到消息：" + msg);
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息类型：" + msg.getClass());
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到消息内容：" + JSON.toJSONString(msg));
        String str = "服务端接收到客户端消息：" + new Date() + " " + msg + "\r\n";
        ChannelHandler.channelGroup.writeAndFlush(str);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("异常信息：\r\n" + cause.getMessage());
        ctx.close();
    }

}