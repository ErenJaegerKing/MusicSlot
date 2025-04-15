package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.mapper.MusicMapper;
import com.ruoyi.system.service.IMusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public int insertMusic(MultipartFile[] file) {
        String filePath = null;
//        try {
//            filePath = FileUploadUtils.uploadMinio(file);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        Music music = new Music();
        music.setFilePath(filePath);
        music.setFileUrl(FileUtils.getName(filePath));
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
    public int updateMusic(MultipartFile file, Music music) {
        String filePath = null;
        try {
            filePath = FileUploadUtils.uploadMinio(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        music.setUpdateTime(DateUtils.getNowDate());
        music.setFilePath(filePath);
        music.setFileUrl(FileUtils.getName(filePath));
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
}
