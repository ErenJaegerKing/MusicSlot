package com.ruoyi.web.task;

import com.ruoyi.common.enums.PlayMode;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.service.IMusicService;
import com.ruoyi.system.service.ITimeSlotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    private ITimeSlotService iTimeSlotService;

    @Autowired
    private IMusicService iMusicService;

    @Scheduled(cron = "0/30 * * * * ? ")
    public void reportCurrentTimeWithCronExpression() {
        log.info("新定时任务");
        List<TimeSlot> timeSlots = iTimeSlotService.selectAllTimeSlot();
        // 遍历时间段 
        for (TimeSlot timeSlot : timeSlots) {
            // 星期是否满足
            String[] selectedWeekdays = timeSlot.getWeekdays().split(",");
            boolean isWeekdayMatch = false;

            int currentWeekday = LocalDate.now().getDayOfWeek().getValue();

            for (String weekday : selectedWeekdays) {
                if (Integer.parseInt(weekday.trim()) == currentWeekday) {
                    isWeekdayMatch = true;
                    break;
                }
            }

            if (!isWeekdayMatch) {
                continue;
            }

            // 时间是否满足
            LocalTime now = LocalTime.now();
            LocalTime startTime = timeSlot.getStartTime();
            LocalTime endTime = timeSlot.getEndTime();

            // 如果开始时间满足的就异步执行直到结束，不满足就结束
            if (now.isAfter(startTime) && now.isBefore(endTime)) {
                try {
                    // 根据音乐Ids表查询出所有的歌单，查询出来的歌单不排序 是什么样子的，等会去试一下
                    Long[] musicIds = Arrays.stream(timeSlot.getMusicIds().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .map(Long::valueOf)
                            .toArray(Long[]::new);
                    List<Music> music = iMusicService.selectMusicByIds(musicIds);

                    // 记录初始播放模式 有无 循环 或者 乱序
                    String[] playModes = timeSlot.getPlayMode().split(",");

                    boolean isShuffle = false;
                    boolean isLoop = false;

                    for (String mode : playModes) {
                        if (mode.equals(PlayMode.SHUFFLE.getValue())) {
                            isShuffle = true;
                        } else if (mode.equals(PlayMode.LOOP.getValue())) {
                            isLoop = true;
                        }
                    }
                    String playModeStr = isShuffle ? "乱序" : "顺序";
                    String loopStr = isLoop ? "循环" : "不循环";
                    log.info("播放时间段 {}，播放模式: {}，循环模式: {}", timeSlot.getSlotId(), playModeStr, loopStr);

                    // 处理播放顺序
                    if (timeSlot.getPlayMode() == PlayMode.SHUFFLE.getValue()) {
                        Collections.shuffle(music); // 乱序
                    } else {
                        music = sortMusicListByIds(music, musicIds); //循序
                    }

                    final List<Music> finalMusic = music;
                    final Boolean finalIsShuffle = isShuffle;
                    final Boolean finalIsLoop = isLoop;

                    // 异步播放音乐直到结束时间
                    CompletableFuture.runAsync(() -> {
                        // 歌单中的下标
                        int currentIndex = 0;
                        // 循环次数
                        int loopCount = 0;
                        while (LocalTime.now().isBefore(endTime)) {
                            if (currentIndex >= finalMusic.size()) {
                                if (finalIsLoop) {
                                    currentIndex = 0;  // 循环播放则从头开始
                                    if (timeSlot.getPlayMode() == PlayMode.SHUFFLE.getValue()) {
                                        Collections.shuffle(finalMusic);  // 如果是乱序模式，每次循环重新打乱
                                        log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环，已重新打乱顺序", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
                                    } else {
                                        log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
                                    }
                                } else {
                                    log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环,播放结束原因:歌单播放完毕(非循环模式)", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
                                    break;  // 非循环模式则结束播放
                                }
                            }

                            Music currentMusic = finalMusic.get(currentIndex);

                            // 播放音乐的逻辑
                            log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环，歌名：{}，ID: {}，歌手：{}，时长: {}秒",
                                    timeSlot.getSlotId(),
                                    playModeStr, loopStr,
                                    loopCount,
                                    currentMusic.getTitle(),
                                    currentMusic.getMusicId(),
                                    currentMusic.getArtist(),
                                    currentMusic.getDuration());
               

                            // 等待音乐播放完成或到达结束时间
                            long duration = currentMusic.getDuration();
                            try {
                                long waitTime = Math.min(duration * 1000, Duration.between(LocalTime.now(), endTime).toMillis());
                                if (waitTime > 0) {
                                    Thread.sleep(waitTime);
                                } else {
                                    log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环,播放结束原因:已到达时间段结束时间", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
                                    break;
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环,播放结束原因:播放结束原因: 被中断", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
                                break;
                            }
                            currentIndex++;
                        }
                        // 结尾
                        if (LocalTime.now().isAfter(endTime)) {
                            log.info("时间段 {} 已结束", timeSlot.getSlotId());
                        }
                    });

                } catch (Exception e) {
                }
            }
        }
    }

    private static List<Music> sortMusicListByIds(List<Music> musicList, Long[] musicIds) {
        Map<Long, Music> idToMusicMap = musicList.stream()
                .collect(Collectors.toMap(Music::getMusicId, m -> m));
        List<Music> sortedList = new ArrayList<>(musicIds.length);
        for (Long id : musicIds) {
            Music m = idToMusicMap.get(id);
            sortedList.add(m);
        }
        return sortedList;
    }


}
