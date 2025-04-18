package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.domain.TimeSlotMusic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 时间段Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface TimeSlotMapper 
{
    /**
     * 查询时间段
     * 
     * @param slotId 时间段主键
     * @return 时间段
     */
    public TimeSlot selectTimeSlotBySlotId(Long slotId);

    /**
     * 根据定时任务Id查询时间段
     *
     * @param taskId 定时任务主键
     * @return 时间段
     */
    public TimeSlot selectTimeSlotByTaskId(String taskId);

    /**
     * 查询时间段列表
     *
     * @param slotIds 时间段主键
     * @return 时间段
     */
    public List<TimeSlot> selectTimeSlotBySlotIds(Long[] slotIds);

    /**
     * 查询时间段列表
     * 
     * @param timeSlot 时间段
     * @return 时间段集合
     */
    public List<TimeSlot> selectTimeSlotList(TimeSlot timeSlot);

    /**
     * 查询所有时间段
     *
     * @return 时间段集合
     */
    public List<TimeSlot> selectAllTimeSlot();


    /**
     * 新增时间段
     * 
     * @param timeSlot 时间段
     * @return 结果
     */
    public int insertTimeSlot(TimeSlot timeSlot);

    /**
     * 修改时间段
     * 
     * @param timeSlot 时间段
     * @return 结果
     */
    public int updateTimeSlot(TimeSlot timeSlot);

    /**
     * 删除时间段
     * 
     * @param slotId 时间段主键
     * @return 结果
     */
    public int deleteTimeSlotBySlotId(Long slotId);

    /**
     * 批量删除时间段
     * 
     * @param slotIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTimeSlotBySlotIds(Long[] slotIds);
}
