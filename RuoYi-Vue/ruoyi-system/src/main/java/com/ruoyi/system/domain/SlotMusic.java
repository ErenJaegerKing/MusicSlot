package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 时间段音乐关联对象 slot_music
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public class SlotMusic extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 时间段ID */
    @Excel(name = "时间段ID")
    private Long slotId;

    /** 音乐ID */
    @Excel(name = "音乐ID")
    private Long musicId;

    /** 播放顺序 */
    @Excel(name = "播放顺序")
    private Long playOrder;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setPlayOrder(Long playOrder) 
    {
        this.playOrder = playOrder;
    }

    public Long getPlayOrder() 
    {
        return playOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("slotId", getSlotId())
            .append("musicId", getMusicId())
            .append("playOrder", getPlayOrder())
            .toString();
    }
}
