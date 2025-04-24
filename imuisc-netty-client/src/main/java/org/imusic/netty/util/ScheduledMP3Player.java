package org.imusic.netty.util;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.apache.commons.io.FileUtils;
import org.imusic.netty.client.MyClientHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;

public class ScheduledMP3Player {
    private Logger logger = LoggerFactory.getLogger(MyClientHandler.class);

    private volatile boolean isPlaying = false;
    private volatile boolean shouldStop = false;
    private Player currentPlayer;

    // 开始时间和结束时间
    private final LocalTime startTime;
    private final LocalTime endTime;

    public ScheduledMP3Player(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * 顺序播放多个MP3文件
     *
     * @param paths MP3文件路径列表
     */
    public void playMp3Music(List<String> paths, Long slotId, String slotName) {
        if (isPlaying) {
            return;
        }

        isPlaying = true;
        try {
            while (!shouldStop && isWithinTimeWindow()) {
                for (String path : paths) {
                    if (shouldStop || !isWithinTimeWindow()) {
                        break;
                    }
                    playSingleFile(path, slotId, slotName);
                }
                logger.info("时间段Id:{},时间段名称：{},本组歌单执行完毕", slotId, slotName);
            }
        } finally {
            isPlaying = false;
            logger.info("时间段Id:{},时间段名称：{},时间段播放完毕", slotId, slotName);
        }
    }

    /**
     * 播放单个MP3文件
     */
    private void playSingleFile(String originalUrl, Long slotId, String slotName) {
        File mp3File = null;
        String path = originalUrl.replace("localhost", "192.168.20.197");
        try {
            mp3File = downloadFileToTemp(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("时间段Id:{},时间段名称：{},开始播放此音乐，音乐路径为：{}", slotId, slotName, path);
        // 创建新线程播放，以便我们可以监控状态
        try (FileInputStream stream = new FileInputStream(mp3File)) {
            currentPlayer = new Player(stream);
            Thread playThread = new Thread(() -> {
                try {
                    currentPlayer.play();
                    logger.info("时间段Id:{},时间段名称：{},音乐播放完成，音乐路径为：{}", slotId, slotName, path);
                } catch (JavaLayerException e) {
                    logger.info("时间段Id:{},时间段名称：{},音乐播放完成，音乐播放错误：{}", slotId, slotName, e.getMessage());
                }
            });
            playThread.start();
            // 时间到了
            while (playThread.isAlive()) {
                if (shouldStop || !isWithinTimeWindow()) {
                    logger.info("时间段Id:{},时间段名称：{},时间到了，终止播放：{}", slotId, slotName, path);
                    currentPlayer.close();
                    playThread.interrupt();
                    break;
                }
                try {
                    Thread.sleep(500); // 检查间隔改为500ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (Exception e) {
            logger.info("时间段Id:{},时间段名称：{},时间到了，播放失败：{}", slotId, slotName, path);
            e.printStackTrace();
        } finally {
            currentPlayer = null;
        }
    }

    /**
     * 将远程文件下载到临时文件
     *
     * @param urlString 远程文件的 URL
     * @return 临时文件对象
     * @throws IOException 如果下载失败
     */
    private static File downloadFileToTemp(String urlString) throws IOException {
        URL url = new URL(urlString);
        File tempFile = File.createTempFile("tempMp3", ".mp3");
        FileUtils.copyURLToFile(url, tempFile);
        return tempFile;
    }


    /**
     * 检查当前时间是否在允许的播放窗口内
     */
    private boolean isWithinTimeWindow() {
        LocalTime now = LocalTime.now();
        return !now.isAfter(endTime);
    }

    /**
     * 停止播放
     */
    public void stop() {
        shouldStop = true;
        isPlaying = false;
        if (currentPlayer != null) {
            currentPlayer.close();
        }
    }

    /**
     * 关闭播放器释放资源
     */
    public void shutdown() {
        stop();
    }


}
