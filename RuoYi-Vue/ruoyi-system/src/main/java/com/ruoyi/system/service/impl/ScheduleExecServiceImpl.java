package com.ruoyi.system.service.impl;

import com.ruoyi.common.enums.PlayMode;
import com.ruoyi.system.config.CronTaskRegistrar;
import com.ruoyi.system.config.ScheduledTaskInit;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.domain.ScheduleSettingPO;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.domain.dto.ScheduleSettingDTO;
import com.ruoyi.system.mapper.ScheduleSettingMapper;
import com.ruoyi.system.service.IMusicService;
import com.ruoyi.system.service.ITimeSlotService;
import com.ruoyi.system.service.ScheduleExecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ScheduleExecServiceImpl implements ScheduleExecService {

    private static final Logger log = LoggerFactory.getLogger(ScheduleExecServiceImpl.class);

    @Resource
    ScheduledTaskInit scheduledTaskInit;

    @Autowired
    private CronTaskRegistrar scheduledTaskRegistrar;

    @Autowired
    private ScheduleSettingMapper scheduleSettingMapper;

    @Autowired
    private ITimeSlotService iTimeSlotService;

    @Autowired
    private IMusicService iMusicService;

    @Override
    public void scheduleExec(String taskId) {
        // TODO 在数据库中为taskId添加索引以优化查询效率
        // 根据taskId查询时间段
        TimeSlot timeSlot = iTimeSlotService.selectTimeSlotByTaskId(taskId);
        // 根据时间段查询相关歌单
        Long[] musicIds = null;
//                Arrays.stream(timeSlot.getMusicIds().split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .map(Long::valueOf)
//                .toArray(Long[]::new);
        List<Music> music = iMusicService.selectMusicByIds(musicIds);
        // 顺序还是乱序
        String playMode = timeSlot.getPlayMode();
        if (playMode.equals(PlayMode.SEQUENCE.getValue())) {
            music = sortMusicListByIds(music, musicIds);
        } else {
            Collections.shuffle(music);
        }
        // sleep到结束时间endTime
        LocalTime endTime = timeSlot.getEndTime();
        // 音乐在歌单中的下标
        int currentIndex = 0;
        // 循环次数
        int loopCount = 0;
        while (LocalTime.now().isBefore(endTime)) {

            if (currentIndex >= music.size()) {
                currentIndex = 0;
                loopCount++;
                if (playMode.equals(PlayMode.SHUFFLE.getValue())) {
                    // 如果是乱序模式，每次循环重新打乱
                    Collections.shuffle(music);
                }
            }

            Music currentMusic = music.get(currentIndex);
            // 播放音乐的逻辑
            log.info("播放时间段 {}，播放模式: {}，歌单第{}次循环，歌名：{}，ID: {}，歌手：{}，时长: {}秒",
                    timeSlot.getSlotId(),
                    playMode,
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
                    // 歌单播放完毕（时间段到了）
                    log.info("播放时间段 {}，播放模式: {}，循环模式: {},歌单第{}次循环,播放结束原因:已到达时间段结束时间",
                            timeSlot.getSlotId(), playMode, loopCount);
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            currentIndex++;
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

    @Override
    public void add(ScheduleSettingDTO scheduleSettingDTO) {
        // 添加定时任务
        scheduledTaskRegistrar.addCronTask(
                () -> scheduleExec(scheduleSettingDTO.getId()),
                scheduleSettingDTO.getCronExpression(),
                scheduleSettingDTO.getJobId());
        // 将定时任务保存到数据库中
        ScheduleSettingPO scheduleSettingPO = new ScheduleSettingPO();
        BeanUtils.copyProperties(scheduleSettingDTO, scheduleSettingPO);
        scheduleSettingPO.setStatus("1");
        scheduleSettingMapper.insert(scheduleSettingPO);
    }


    @Override
    public void stop(ScheduleSettingDTO scheduleSettingDTO) {
        //根据jobId暂停
        ScheduleSettingPO scheduleSettingPO = scheduleSettingMapper.findById(scheduleSettingDTO.getId());
        scheduledTaskRegistrar.removeCronTask(scheduleSettingPO.getJobId());
        //更新定时任务数据到数据库中(删除或者修改)
        scheduleSettingMapper.deleteById(scheduleSettingDTO.getId());
    }

    @Override
    public void stopAll() {
        scheduledTaskRegistrar.destroy();
    }

    @Override
    public void refresh() {
        scheduledTaskInit.refresh(this);
    }

}
