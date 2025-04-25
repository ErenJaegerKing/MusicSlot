package com.ruoyi.web.task;

import com.ruoyi.system.netty.server.NettyServer;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    // 正在播放的时间段
    private final Map<Long, Boolean> playingSlots = new ConcurrentHashMap<>();

    // 不循环 歌单播放完毕，但是时间段未结束的 可以在每天凌晨0电的时候，清空这个集合
    private final Map<Long, Boolean> notLoopSlots = new ConcurrentHashMap<>();

    @Autowired
    private ITimeSlotService iTimeSlotService;

    @Autowired
    private IMusicService iMusicService;

    @Autowired
    private NettyServer nettyServer;

    // 每5秒向所有客户端发送消息
    @Scheduled(fixedRate = 10000)
    public void sendPeriodicMessage() {
//        MsgInfo msg = MsgUtil.buildMsg("1", "这是java对象");
//        nettyServer.sendMessageToAllClients(msg);
        TimeSlot timeSlot = iTimeSlotService.selectTimeSlotBySlotId(56L);
        nettyServer.sendMessageToAllClients(timeSlot);
    }

//    @Scheduled(cron = "0/30 * * * * ? ")
//    public void TimeSlotPlayScheduler() {
//        log.info("新定时任务 - 时间：{}", dateFormat.format(new Date()));
//        List<TimeSlot> timeSlots = iTimeSlotService.selectAllTimeSlot();
//
//        // 遍历时间段
//        for (TimeSlot timeSlot : timeSlots) {
//            // 跳过正在播放的时间段
//            if (playingSlots.containsKey(timeSlot.getSlotId())) {
//                continue;
//            }
//            // 跳过今天已经播放完毕的时间段
//            if (notLoopSlots.containsKey(timeSlot.getSlotId())) {
//                continue;
//            }
//            // 星期检查
//            String[] selectedWeekdays = timeSlot.getWeekdays().split(",");
//            boolean isWeekdayMatch = false;
//
//            int currentWeekday = LocalDate.now().getDayOfWeek().getValue();
//
//            for (String weekday : selectedWeekdays) {
//                if (Integer.parseInt(weekday.trim()) == currentWeekday) {
//                    isWeekdayMatch = true;
//                    break;
//                }
//            }
//
//            if (!isWeekdayMatch) {
//                continue;
//            }
//
//            // 时间检查
//            LocalTime now = LocalTime.now();
//            LocalTime startTime = timeSlot.getStartTime();
//            LocalTime endTime = timeSlot.getEndTime();
//            if (now.isAfter(startTime) && now.isBefore(endTime)) {
//
//                // 时间和星期都满足就标记为正在播放
//                playingSlots.put(timeSlot.getSlotId(), true);
//
//                try {
//                    // 根据音乐Ids表查询出所有的歌单
//                    Long[] musicIds = Arrays.stream(timeSlot.getMusicIds().split(","))
//                            .map(String::trim)
//                            .filter(s -> !s.isEmpty())
//                            .map(Long::valueOf)
//                            .toArray(Long[]::new);
//                    List<Music> music = iMusicService.selectMusicByIds(musicIds);
//
//                    // 是否乱序 是否循环
//                    String[] playModes = timeSlot.getPlayMode().split(",");
//
//                    boolean isShuffle = false;
//                    boolean isLoop = false;
//
//                    for (String mode : playModes) {
//                        if (mode.equals(PlayMode.SHUFFLE.getValue())) {
//                            isShuffle = true;
//                        } else if (mode.equals(PlayMode.LOOP.getValue())) {
//                            isLoop = true;
//                        }
//                    }
//                    String playModeStr = isShuffle ? "乱序" : "顺序";
//                    String loopStr = isLoop ? "循环" : "不循环";
//
//                    // 如果乱序就打算音乐单，如果顺序就排序音乐单
//                    if (timeSlot.getPlayMode() == PlayMode.SHUFFLE.getValue()) {
//                        Collections.shuffle(music); // 乱序
//                    } else {
//                        music = sortMusicListByIds(music, musicIds); //循序
//                    }
//
//                    // 音乐在歌单中的下标
//                    int currentIndex = 0;
//                    // 循环次数
//                    int loopCount = 0;
//                    while (LocalTime.now().isBefore(endTime)) {
//
//                        // 当歌单播放完成之后
//                        if (currentIndex >= music.size()) {
//                            if (isLoop) {
//                                currentIndex = 0;
//                                loopCount++;
//                                if (isShuffle) {
//                                    // 如果是乱序模式，每次循环重新打乱
//                                    Collections.shuffle(music);
//                                }
//                            } else {
//                                // 歌单播放完毕（非循环模式）
//                                log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环,播放结束原因:歌单播放完毕，非循环模式", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
//                                notLoopSlots.put(timeSlot.getSlotId(), true);
//                                break;
//                            }
//                        }
//
//                        Music currentMusic = music.get(currentIndex);
//
//                        // 播放音乐的逻辑
//                        log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环，歌名：{}，ID: {}，歌手：{}，时长: {}秒",
//                                timeSlot.getSlotId(),
//                                playModeStr, loopStr,
//                                loopCount,
//                                currentMusic.getTitle(),
//                                currentMusic.getMusicId(),
//                                currentMusic.getArtist(),
//                                currentMusic.getDuration());
//
//
//                        // 等待音乐播放完成或到达结束时间
//                        long duration = currentMusic.getDuration();
//                        try {
//                            long waitTime = Math.min(duration * 1000, Duration.between(LocalTime.now(), endTime).toMillis());
//                            if (waitTime > 0) {
//                                Thread.sleep(waitTime);
//                            } else {
//                                // 歌单播放完毕（时间段到了）
//                                log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环,播放结束原因:已到达时间段结束时间", timeSlot.getSlotId(), playModeStr, loopStr, loopCount);
//                                playingSlots.remove(timeSlot.getSlotId());
//                                break;
//                            }
//                        } catch (InterruptedException e) {
//                            Thread.currentThread().interrupt();
//                            break;
//                        }
//                        currentIndex++;
//                    }
//
//                } catch (Exception e) {
//                    playingSlots.remove(timeSlot.getSlotId());
//                }
//            }
//        }
//    }

//    @Scheduled(cron = "0 0 0 * * ?")
//    public void removeNotLoopSlotsScheduler() {
//        notLoopSlots.clear();
//    }

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
