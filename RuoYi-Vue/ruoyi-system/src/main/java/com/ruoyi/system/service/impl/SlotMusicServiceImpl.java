package com.ruoyi.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SlotMusicMapper;
import com.ruoyi.system.domain.SlotMusic;
import com.ruoyi.system.service.ISlotMusicService;

/**
 * 时间段音乐关联Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class SlotMusicServiceImpl implements ISlotMusicService {

    @Autowired
    private SlotMusicMapper slotMusicMapper;

    /**
     * 查询时间段音乐关联
     *
     * @param id 时间段音乐关联主键
     * @return 时间段音乐关联
     */
    @Override
    public SlotMusic selectSlotMusicById(Long id) {
        return slotMusicMapper.selectSlotMusicById(id);
    }

    /**
     * 查询时间段音乐关联列表
     *
     * @param slotMusic 时间段音乐关联
     * @return 时间段音乐关联
     */
    @Override
    public List<SlotMusic> selectSlotMusicList(SlotMusic slotMusic) {
        return slotMusicMapper.selectSlotMusicList(slotMusic);
    }

    /**
     * 新增时间段音乐关联
     *
     * @param slotMusic 时间段音乐关联
     * @return 结果
     */
    @Override
    public int insertSlotMusic(SlotMusic slotMusic) {
        return slotMusicMapper.insertSlotMusic(slotMusic);
    }

    /**
     * 修改时间段音乐关联
     *
     * @param slotMusic 时间段音乐关联
     * @return 结果
     */
    @Override
    public int updateSlotMusic(SlotMusic slotMusic) {
        return slotMusicMapper.updateSlotMusic(slotMusic);
    }

    /**
     * 批量删除时间段音乐关联
     *
     * @param ids 需要删除的时间段音乐关联主键
     * @return 结果
     */
    @Override
    public int deleteSlotMusicByIds(Long[] ids) {
        return slotMusicMapper.deleteSlotMusicByIds(ids);
    }

    /**
     * 删除时间段音乐关联信息
     *
     * @param id 时间段音乐关联主键
     * @return 结果
     */
    @Override
    public int deleteSlotMusicById(Long id) {
        return slotMusicMapper.deleteSlotMusicById(id);
    }
}
