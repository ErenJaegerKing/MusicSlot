package com.ruoyi.system.domain;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 时间段对象 time_slot
 *
 * @author ruoyi
 * @date 2025-04-08
 */
public class TimeSlot extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 时间段ID
     */
    private Long slotId;

    /**
     * 时间段名称 (任务名称)
     */
    @Excel(name = "时间段名称")
    private String slotName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "HH:mm:ss")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "HH:mm:ss")
    private LocalTime endTime;

    /**
     * 播放模式（1循环 2乱序 可多选）
     */
    @Excel(name = "播放模式")
    private String playMode;

    /**
     * 播放时段（1-7星期一到星期六）
     */
    @Excel(name = "播放时段")
    private String weekdays;
    
    /**
     * 音乐Ids
     */
    @Excel(name = "音乐Ids")
    private String musicIds;
    
    /**
     * 状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;
    
    /**
     * 任务Id
     */
    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }


    public String getMusicIds() {
        return musicIds;
    }

    public void setMusicIds(String musicIds) {
        this.musicIds = musicIds;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    
    public String getPlayMode() {
        return playMode;
    }

    public void setPlayMode(String playMode) {
        this.playMode = playMode;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }
    
    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("slotId", getSlotId())
                .append("startTime", getStartTime())
                .append("endTime", getEndTime())
                .append("slotName", getSlotName())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}
