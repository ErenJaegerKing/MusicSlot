package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.domain.TimeSlotMusic;
import com.ruoyi.system.mapper.MusicMapper;
import com.ruoyi.system.mapper.TimeSlotMusicMapper;
import com.ruoyi.system.service.IMusicService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 音乐Service业务层处理
 *
 * @author ruoyi
 * @date 2025-04-08
 */
@Service
public class MusicServiceImpl implements IMusicService {
    @Autowired
    private MusicMapper musicMapper;

    @Autowired
    private TimeSlotMusicMapper timeSlotMusicMapper;

    /**
     * 查询音乐
     *
     * @param musicId 音乐主键
     * @return 音乐
     */
    @Override
    public Music selectMusicByMusicId(Long musicId) {
        return musicMapper.selectMusicByMusicId(musicId);
    }

    /**
     * 查询音乐列表
     *
     * @param music 音乐
     * @return 音乐
     */
    @Override
    public List<Music> selectMusicList(Music music) {
        return musicMapper.selectMusicList(music);
    }

    /**
     * 批量查询音乐
     *
     * @param musicIds 音乐Ids
     * @return 音乐集合
     */
    @Override
    public List<Music> selectMusicByIds(Long[] musicIds) {
        return musicMapper.selectMusicByIds(musicIds);
    }

    /**
     * 新增音乐
     *
     * @param music 音乐
     * @return 结果
     */
    @Override
    public int insertMusic(Music music) {
        return musicMapper.insertMusic(music);
    }

    /**
     * 修改音乐
     *
     * @param music 音乐
     * @return 结果
     */
    @Override
    public int updateMusic(MultipartFile file, Music music) {
        return musicMapper.updateMusic(music);
    }

    /**
     * 批量删除音乐
     *
     * @param musicIds 需要删除的音乐主键
     * @return 结果
     */
    @Override
    public int deleteMusicByMusicIds(Long[] musicIds) {
        // 检验是否有时间段关联
        boolean exists = timeSlotMusicMapper.exists(
                new LambdaQueryWrapper<TimeSlotMusic>()
                        .in(TimeSlotMusic::getMusicId, Arrays.asList(musicIds))
        );
        if (exists) {
            throw new ServiceException("时间段关联，无法删除",500);
        }
        return musicMapper.deleteMusicByMusicIds(musicIds);
    }

    /**
     * 删除音乐信息
     *
     * @param musicId 音乐主键
     * @return 结果
     */
    @Override
    public int deleteMusicByMusicId(Long musicId) {
        return musicMapper.deleteMusicByMusicId(musicId);
    }

    @Override
    public Boolean findByFileMd5(String md5) {
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Music::getFileMd5, md5);
        Music music = musicMapper.selectOne(wrapper);
        return music != null;
    }
}
