package com.ruoyi.system.util;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;

public class ScheduledMP3Player {
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
    public void playMp3Music(List<String> paths) {
        if (isPlaying) {
            System.out.println("播放器正在运行中");
            return;
        }


        isPlaying = true;
        try {
            while (!shouldStop && isWithinTimeWindow()) {
                for (String path : paths) {
                    if (shouldStop || !isWithinTimeWindow()) {
                        break;
                    }
                    playSingleFile(path);
                }
                System.out.println("个蛋执行完毕");
            }
        } finally {
            isPlaying = false;
            System.out.println("音乐播放队列完成");
        }
    }

    /**
     * 播放单个MP3文件
     */
    private void playSingleFile(String path) {
        File mp3File = new File(path);
        if (!mp3File.exists()) {
            System.err.println("文件不存在: " + mp3File.getAbsolutePath());
            return;
        }
        System.out.println("开始播放: " + path);

        // 创建新线程播放，以便我们可以监控状态
        try (FileInputStream stream = new FileInputStream(mp3File)) {
            currentPlayer = new Player(stream);

            Thread playThread = new Thread(() -> {
                try {
                    currentPlayer.play();
                    System.out.println("播放完成: " + path);
                } catch (JavaLayerException e) {
                    System.err.println("播放错误: " + e.getMessage());
                }
            });
            playThread.start();
            // 终止条件：时间到了 | 歌放完了
            while (playThread.isAlive()) {
                if (shouldStop || !isWithinTimeWindow()) {
                    System.out.println("时间到了，终止播放");
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
            System.err.println("播放失败: " + path);
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
        return !now.isBefore(startTime) && !now.isAfter(endTime);
    }

    /**
     * 停止播放
     */
    public void stop() {
        shouldStop = true;
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
