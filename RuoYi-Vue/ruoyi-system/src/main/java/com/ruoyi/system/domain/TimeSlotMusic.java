package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;

/**
 * 时间段音乐关联对象 slot_music
 *
 * @author x
 * @date 2025-04-17
 */
public class TimeSlotMusic
{
    private static final long serialVersionUID = 1L;

    /** 时间段ID */
    @Excel(name = "时间段ID")
    private Long slotId;

    /** 音乐ID */
    @Excel(name = "音乐ID")
    private Long musicId;

    public TimeSlotMusic(Long slotId, Long musicId) {
    }

    public TimeSlotMusic() {

    }

    public void setSlotId(Long slotId)
    {
        this.slotId = slotId;
    }

    public Long getSlotId()
    {
        return slotId;
    }

    public void setMusicId(Long musicId)
    {
        this.musicId = musicId;
    }

    public Long getMusicId()
    {
        return musicId;
    }

}
