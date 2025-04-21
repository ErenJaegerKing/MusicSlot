package com.ruoyi.system.service;

import java.util.List;

import com.ruoyi.system.domain.Music;
import org.springframework.web.multipart.MultipartFile;

/**
 * 音乐Service接口
 *
 * @author ruoyi
 * @date 2025-04-08
 */
public interface IMusicService {
    /**
     * 查询音乐
     *
     * @param musicId 音乐主键
     * @return 音乐
     */
    public Music selectMusicByMusicId(Long musicId);
    
    /**
     * 查询音乐列表
     *
     * @param music 音乐
     * @return 音乐集合
     */
    public List<Music> selectMusicList(Music music);

    /**
     * 批量查询音乐
     *
     * @param musicIds 音乐Ids
     * @return 音乐集合
     */
    public List<Music> selectMusicByIds(Long[] musicIds);


    /**
     * 新增音乐
     *
     */
    public int insertMusic(String url);

    /**
     * 修改音乐
     *
     * @param music 音乐
     * @return 结果
     */
    public int updateMusic(MultipartFile file, Music music);

    /**
     * 批量删除音乐
     *
     * @param musicIds 需要删除的音乐主键集合
     * @return 结果
     */
    public int deleteMusicByMusicIds(Long[] musicIds);

    /**
     * 删除音乐信息
     *
     * @param musicId 音乐主键
     * @return 结果
     */
    public int deleteMusicByMusicId(Long musicId);

    Boolean findByFileMd5(String md5);
}
