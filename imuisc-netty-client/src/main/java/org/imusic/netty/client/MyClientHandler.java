package org.imusic.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.imusic.netty.domain.Music;
import org.imusic.netty.domain.TimeSlot;
import org.imusic.netty.util.MsgUtil;
import org.imusic.netty.util.ScheduledMP3Player;
import org.imusic.netty.util.ThreadPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MyClientHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(MyClientHandler.class);

    private final NettyClient nettyClient; // 需要注入 NettyClient

    // 在类中添加一个静态成员变量
    private static final ConcurrentHashMap<String, Boolean> runningSlots = new ConcurrentHashMap<>();

    public MyClientHandler(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush(MsgUtil.buildTimeSlotMsg("客户端：发送心跳包:ping\n"));
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        logger.info("客户端：链接报告开始");
        logger.info("客户端：链接报告信息：已经链接到服务端");
        logger.info("客户端：链接报告IP:{}", channel.remoteAddress().getHostString());
        logger.info("客户端：链接报告Port:{}", channel.remoteAddress().getPort());
        logger.info("客户端：链接报告完毕");

        String str = "客户端通知服务端链接建立成功" + " " + new Date() + " " + channel.localAddress().getHostString() + "\r\n";
        ctx.writeAndFlush(MsgUtil.buildTimeSlotMsg(str));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("断开链接{}", ctx.channel().localAddress().toString());

        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7397);
        ctx.channel().eventLoop().schedule(() -> {
            System.out.println("我先来断开后进行重连");
            nettyClient.connect(address);
        }, 2, TimeUnit.SECONDS);

//        使用过程中断线重连
//        ctx.channel().eventLoop().schedule(() -> connect(), 5, TimeUnit.SECONDS);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    InetSocketAddress address = new InetSocketAddress("127.0.0.1", 7397);
//                    nettyClient.connect(address);
//                    System.out.println("断线重连");
//                    Thread.sleep(10000);
//                } catch (Exception e){
//                    System.out.println("断线重连失败，退出重连");
//                }
//            }
//        }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TimeSlot timeSlot = (TimeSlot) msg;
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到服务端消息：" + timeSlot.getSlotName());
        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " 接收到服务端消息：" + timeSlot);
//        String str = "客户端收到：" + new Date() + " " + msg + "\r\n";
//        ctx.writeAndFlush(MsgUtil.buildTimeSlotMsg(str));
        if (timeSlot.getSlotId() != null) {
            logger.info("来了一个定时任务");
            if (runningSlots.putIfAbsent(String.valueOf(timeSlot.getSlotId()),Boolean.TRUE) != null) {
                logger.info("slotId {} 的任务已在执行，跳过", timeSlot.getSlotId());
                return;
            }
            logger.info("开始执行定时任务 slotId: {}", timeSlot.getSlotId());
            ThreadPoolManager.execute(() -> {
                try {
                    List<Music> musicList = timeSlot.getMusicList();
                    List<String> paths = musicList.stream()
                            .map(Music::getFilePath)
                            .collect(Collectors.toList());
                    ScheduledMP3Player player = new ScheduledMP3Player(
                            timeSlot.getStartTime(),
                            timeSlot.getEndTime());
                    List<String> path = Arrays.asList(
                            "C:\\Users\\dell\\Desktop\\090220\\01 - Bad.mp3"
                    );
                    if ("2".equals(timeSlot.getPlayMode())) {
                        Collections.shuffle(paths);
                    }
                    player.playMp3Music(path);
                    Runtime.getRuntime().addShutdownHook(
                            new Thread(player::shutdown)
                    );
                } catch (Exception e) {
                    logger.error("执行任务出错", e);
                } finally {
                    runningSlots.remove(String.valueOf(timeSlot.getSlotId()));
                    logger.info("任务 slotId: {} 执行完成", timeSlot.getSlotId());
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("异常信息：\r\n" + cause.getMessage());
        ctx.close();
    }

}
