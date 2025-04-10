package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SlotMusic;

/**
 * 时间段音乐关联Service接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface ISlotMusicService 
{
    /**
     * 查询时间段音乐关联
     * 
     * @param id 时间段音乐关联主键
     * @return 时间段音乐关联
     */
    public SlotMusic selectSlotMusicById(Long id);

    /**
     * 查询时间段音乐关联列表
     * 
     * @param slotMusic 时间段音乐关联
     * @return 时间段音乐关联集合
     */
    public List<SlotMusic> selectSlotMusicList(SlotMusic slotMusic);

    /**
     * 新增时间段音乐关联
     * 
     * @param slotMusic 时间段音乐关联
     * @return 结果
     */
    public int insertSlotMusic(SlotMusic slotMusic);

    /**
     * 批量新增时间段音乐关联
     *
     * @param slotMusic 时间段音乐关联
     * @return 结果
     */
    public int batchInsertSlotMusic(List<SlotMusic> slotMusics);
    
    /**
     * 修改时间段音乐关联
     * 
     * @param slotMusic 时间段音乐关联
     * @return 结果
     */
    public int updateSlotMusic(SlotMusic slotMusic);

    /**
     * 批量删除时间段音乐关联
     * 
     * @param ids 需要删除的时间段音乐关联主键集合
     * @return 结果
     */
    public int deleteSlotMusicByIds(Long[] ids);

    /**
     * 删除时间段音乐关联信息
     * 
     * @param id 时间段音乐关联主键
     * @return 结果
     */
    public int deleteSlotMusicById(Long id);
}
