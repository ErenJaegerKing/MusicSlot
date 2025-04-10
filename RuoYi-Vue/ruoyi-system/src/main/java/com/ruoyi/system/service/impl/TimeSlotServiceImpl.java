package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.SlotMusic;
import com.ruoyi.system.service.ISlotMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TimeSlotMapper;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.service.ITimeSlotService;

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
    private ISlotMusicService iSlotMusicService;

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
//        // 保存时间段
//        timeSlotMapper.insertTimeSlot(timeSlot);
//        // 自增计数器
//        AtomicInteger counter = new AtomicInteger(1);
//        // 保存关联关系表（不需要）
//        List<SlotMusic> slotMusics = timeSlot.getMusicIds().stream()
//                .map(musicId -> new SlotMusic(
//                        null,
//                        timeSlot.getSlotId(),
//                        musicId,
//                        counter.getAndIncrement()
//                )).collect(Collectors.toList());
//        iSlotMusicService.batchInsertSlotMusic(slotMusics);
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
     * 批量删除时间段
     *
     * @param slotIds 需要删除的时间段主键
     * @return 结果
     */
    @Override
    public int deleteTimeSlotBySlotIds(Long[] slotIds) {
        return timeSlotMapper.deleteTimeSlotBySlotIds(slotIds);
    }

    /**
     * 删除时间段信息
     *
     * @param slotId 时间段主键
     * @return 结果
     */
    @Override
    public int deleteTimeSlotBySlotId(Long slotId) {
        return timeSlotMapper.deleteTimeSlotBySlotId(slotId);
    }
}
