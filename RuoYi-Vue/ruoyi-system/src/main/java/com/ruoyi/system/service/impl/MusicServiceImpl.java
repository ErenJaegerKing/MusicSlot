package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.MusicMapper;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.service.IMusicService;

/**
 * 音乐Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class MusicServiceImpl implements IMusicService 
{
    @Autowired
    private MusicMapper musicMapper;

    /**
     * 查询音乐
     * 
     * @param musicId 音乐主键
     * @return 音乐
     */
    @Override
    public Music selectMusicByMusicId(Long musicId)
    {
        return musicMapper.selectMusicByMusicId(musicId);
    }

    /**
     * 查询音乐列表
     * 
     * @param music 音乐
     * @return 音乐
     */
    @Override
    public List<Music> selectMusicList(Music music)
    {
        return musicMapper.selectMusicList(music);
    }

    /**
     * 新增音乐
     * 
     * @param music 音乐
     * @return 结果
     */
    @Override
    public int insertMusic(Music music)
    {
        music.setCreateTime(DateUtils.getNowDate());
        return musicMapper.insertMusic(music);
    }

    /**
     * 修改音乐
     * 
     * @param music 音乐
     * @return 结果
     */
    @Override
    public int updateMusic(Music music)
    {
        music.setUpdateTime(DateUtils.getNowDate());
        return musicMapper.updateMusic(music);
    }

    /**
     * 批量删除音乐
     * 
     * @param musicIds 需要删除的音乐主键
     * @return 结果
     */
    @Override
    public int deleteMusicByMusicIds(Long[] musicIds)
    {
        return musicMapper.deleteMusicByMusicIds(musicIds);
    }

    /**
     * 删除音乐信息
     * 
     * @param musicId 音乐主键
     * @return 结果
     */
    @Override
    public int deleteMusicByMusicId(Long musicId)
    {
        return musicMapper.deleteMusicByMusicId(musicId);
    }
}
