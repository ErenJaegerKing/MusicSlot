package org.imusic.netty.domain;

import com.fasterxml.jackson.annotation.JsonFormat;


import java.time.LocalTime;
import java.util.List;

/**
 * 时间段对象 time_slot
 *
 * @author ruoyi
 * @date 2025-04-08
 */
public class TimeSlot extends BaseEntity {
    private static final long serialVersionUID = 1L;

    public TimeSlot() {

    }

    public TimeSlot(String slotName) {
        this.slotName = slotName;
    }
    /**
     * 时间段ID
     */
    private Long slotId;

    /**
     * 时间段名称 (任务名称)
     */
    private String slotName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    /**
     * 播放模式（1循环 2乱序 可多选）
     */
    private String playMode;

    /**
     * 播放时段（1-7星期一到星期六）
     */
    private String weekdays;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 接收参数
     * 音乐Ids 不持久化
     */
    private List<Long> musicIds;

    /*
    * 查询参数
    * 音乐集合 不持久化
    */
    private List<Music> musicList;

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public List<Long> getMusicIds() {
        return musicIds;
    }

    public void setMusicIds(List<Long> musicIds) {
        this.musicIds = musicIds;
    }


    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
        return "TimeSlot{" +
                "slotId=" + slotId +
                ", slotName='" + slotName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", playMode='" + playMode + '\'' +
                ", weekdays='" + weekdays + '\'' +
                ", status='" + status + '\'' +
                ", taskId='" + taskId + '\'' +
                ", musicIds=" + musicIds +
                ", musicList=" + musicList +
                '}';
    }
}
