package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.constant.TimeSlotConstants;
import com.ruoyi.common.utils.CronUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.domain.TimeSlotMusic;
import com.ruoyi.system.domain.dto.ScheduleSettingDTO;
import com.ruoyi.system.mapper.ScheduleSettingMapper;
import com.ruoyi.system.mapper.TimeSlotMapper;
import com.ruoyi.system.mapper.TimeSlotMusicMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ITimeSlotService;
import com.ruoyi.system.service.ScheduleExecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 时间段Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class TimeSlotServiceImpl implements ITimeSlotService {
    @Autowired
    private TimeSlotMapper timeSlotMapper;

    @Autowired
    private ISysConfigService iSysConfigService;

    @Autowired
    private ScheduleExecService scheduleExecService;

    @Autowired
    private TimeSlotMusicMapper timeSlotMusicMapper;

    @Autowired
    private ScheduleSettingMapper scheduleSettingMapper;
    /**
     * 查询时间段
     *
     * @param slotId 时间段主键
     * @return 时间段
     */
    @Override
    public TimeSlot selectTimeSlotBySlotId(Long slotId) {
        return timeSlotMapper.selectTimeSlotBySlotId(slotId);
    }

    /**
     * 根据定时任务Id查询时间段
     *
     * @param taskId 定时任务主键
     * @return 时间段
     */
    @Override
    public TimeSlot selectTimeSlotByTaskId(String taskId) {
        return timeSlotMapper.selectTimeSlotByTaskId(taskId);
    }

    /**
     * 查询时间段列表
     *
     * @param timeSlot 时间段
     * @return 时间段
     */
    @Override
    public List<TimeSlot> selectTimeSlotList(TimeSlot timeSlot) {
        return timeSlotMapper.selectTimeSlotList(timeSlot);
    }

    @Override
    public List<TimeSlot> selectAllTimeSlot() {
        String enabled = iSysConfigService.selectTimeSlotEnabledByKey(TimeSlotConstants.TIMESLOT_QUERY_ENABLED);
        if (TimeSlotConstants.DISENABLED.equals(enabled)) {
            return Collections.emptyList();
        }
        return timeSlotMapper.selectAllTimeSlot();
    }


    /**
     * 新增时间段
     *
     * @param timeSlot 时间段
     * @return 结果
     */
    @Override
    public int insertTimeSlot(TimeSlot timeSlot) {
        timeSlot.setCreateTime(DateUtils.getNowDate());
        // 生成cron表达式
        LocalTime startTime = timeSlot.getStartTime();
        String cron = CronUtil.getCustomWeekDayHourMinuteCron(timeSlot.getWeekdays(),
                String.format("%d", startTime.getHour()),
                String.format("%d", startTime.getMinute()));
        // 添加定时任务
        ScheduleSettingDTO scheduleSettingDTO = new ScheduleSettingDTO();
        scheduleSettingDTO.setId(UUID.randomUUID().toString());
        scheduleSettingDTO.setJobId(UUID.randomUUID().toString());
        scheduleSettingDTO.setCronExpression(cron);
        scheduleExecService.add(scheduleSettingDTO);
        // 关联时间段与定时任务
        timeSlot.setTaskId(scheduleSettingDTO.getId());

        // 插入时间段，获得自增Id
        timeSlotMapper.insertTimeSlot(timeSlot);
        // 关联音乐表
        List<TimeSlotMusic> collects = timeSlot.getMusicIds().stream()
                .map(musicId -> {
                    TimeSlotMusic timeSlotMusic = new TimeSlotMusic();
                    timeSlotMusic.setMusicId(musicId);
                    timeSlotMusic.setSlotId(timeSlot.getSlotId());
                    return timeSlotMusic;
                })
                .collect(Collectors.toList());
        return timeSlotMusicMapper.batchInsert(collects);
    }

    /**
     * 修改时间段
     *
     * @param timeSlot 时间段
     * @return 结果
     */
    @Override
    public int updateTimeSlot(TimeSlot timeSlot) {
        timeSlot.setUpdateTime(DateUtils.getNowDate());
        // 定时任务
            // 停止定时任务
        ScheduleSettingDTO scheduleSettingDTO1 = new ScheduleSettingDTO();
        scheduleSettingDTO1.setId(timeSlot.getTaskId());
        try {
            scheduleExecService.stop(scheduleSettingDTO1);
        } catch (Exception e) {
            e.printStackTrace();
        }
            // 生成cron表达式
        LocalTime startTime = timeSlot.getStartTime();
        String cron = CronUtil.getCustomWeekDayHourMinuteCron(timeSlot.getWeekdays(),
                String.format("%d", startTime.getHour()),
                String.format("%d", startTime.getMinute()));
            // 添加定时任务
        ScheduleSettingDTO scheduleSettingDTO = new ScheduleSettingDTO();
        scheduleSettingDTO.setId(UUID.randomUUID().toString());
        scheduleSettingDTO.setJobId(UUID.randomUUID().toString());
        scheduleSettingDTO.setCronExpression(cron);
        scheduleExecService.add(scheduleSettingDTO);
            // 关联时间段与定时任务
        timeSlot.setTaskId(scheduleSettingDTO.getId());

        // 歌单
            // 删除所有关联表
        LambdaQueryWrapper<TimeSlotMusic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TimeSlotMusic::getSlotId,timeSlot.getSlotId());
        timeSlotMusicMapper.delete(wrapper);
            // 批量插入关联表
        List<TimeSlotMusic> collect = timeSlot.getMusicIds().stream()
                .map(musicId -> {
                    TimeSlotMusic timeSlotMusic = new TimeSlotMusic();
                    timeSlotMusic.setMusicId(musicId);
                    timeSlotMusic.setSlotId(timeSlot.getSlotId());
                    return timeSlotMusic;
                })
                .collect(Collectors.toList());
        timeSlotMusicMapper.batchInsert(collect);

        return timeSlotMapper.updateTimeSlot(timeSlot);
    }

    /**
     * 删除时间段信息（没用到这个）
     *
     * @param slotId 时间段主键
     * @return 结果
     */
    @Override
    public int deleteTimeSlotBySlotId(Long slotId) {
        // 查询时间段获得任务Id
        TimeSlot timeSlot = timeSlotMapper.selectTimeSlotBySlotId(slotId);
        // 停止定时任务
        ScheduleSettingDTO scheduleSettingDTO = new ScheduleSettingDTO();
        scheduleSettingDTO.setId(timeSlot.getTaskId());
        try {
            scheduleExecService.stop(scheduleSettingDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 批量删除关联表
        LambdaQueryWrapper<TimeSlotMusic> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TimeSlotMusic::getSlotId, slotId);
        timeSlotMusicMapper.delete(wrapper);

        return timeSlotMapper.deleteTimeSlotBySlotId(slotId);
    }

    /**
     * 批量删除时间段
     *
     * @param slotIds 需要删除的时间段主键
     * @return 结果
     */
    @Override
    public int deleteTimeSlotBySlotIds(Long[] slotIds) {
        // 查询时间段获得任务Id
        List<TimeSlot> timeSlots = timeSlotMapper.selectTimeSlotBySlotIds(slotIds);
        for (TimeSlot timeSlot : timeSlots) {
            // 停止定时任务
            ScheduleSettingDTO scheduleSettingDTO = new ScheduleSettingDTO();
            scheduleSettingDTO.setId(timeSlot.getTaskId());
            try {
                scheduleExecService.stop(scheduleSettingDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 删除关联表
        LambdaQueryWrapper<TimeSlotMusic> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(TimeSlotMusic::getSlotId, slotIds);
        timeSlotMusicMapper.delete(wrapper);

        return timeSlotMapper.deleteTimeSlotBySlotIds(slotIds);
    }

}
