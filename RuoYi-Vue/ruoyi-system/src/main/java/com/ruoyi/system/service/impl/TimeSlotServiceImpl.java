package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.TimeSlotConstants;
import com.ruoyi.common.utils.CronUtil;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.domain.dto.ScheduleSettingDTO;
import com.ruoyi.system.mapper.TimeSlotMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ITimeSlotService;
import com.ruoyi.system.service.ScheduleExecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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

        // 关联音乐表
        List<Long> musicIds = timeSlot.getMusicIds();

        for (Long id : musicIds) {

        }

        return timeSlotMapper.insertTimeSlot(timeSlot);
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
        return timeSlotMapper.updateTimeSlot(timeSlot);
    }

    /**
     * 删除时间段信息
     *
     * @param slotId 时间段主键
     * @return 结果
     */
    @Override
    public int deleteTimeSlotBySlotId(Long slotId) {
        // 查询时间段获得任务Id
        TimeSlot timeSlot = timeSlotMapper.selectTimeSlotBySlotId(slotId);
        String taskId = timeSlot.getTaskId();
        // 停止定时任务
        ScheduleSettingDTO scheduleSettingDTO = new ScheduleSettingDTO();
        scheduleSettingDTO.setId(taskId);
        try {
            scheduleExecService.stop(scheduleSettingDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        List<TimeSlot> timeSlots = timeSlotMapper.selectTimeSlotBySlotIds(slotIds);
        for (TimeSlot timeSlot : timeSlots) {
            // 查询时间段获得任务Id
            String taskId = timeSlot.getTaskId();
            // 停止定时任务
            ScheduleSettingDTO scheduleSettingDTO = new ScheduleSettingDTO();
            scheduleSettingDTO.setId(taskId);
            try {
                scheduleExecService.stop(scheduleSettingDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return timeSlotMapper.deleteTimeSlotBySlotIds(slotIds);
    }

}
