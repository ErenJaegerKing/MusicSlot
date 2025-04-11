package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.domain.vo.MusicVo;

/**
 * 音乐Mapper接口
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public interface MusicMapper 
{
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
     * @param music 音乐
     * @return 结果
     */
    public int insertMusic(Music music);

    /**
     * 修改音乐
     * 
     * @param music 音乐
     * @return 结果
     */
    public int updateMusic(Music music);

    /**
     * 删除音乐
     * 
     * @param musicId 音乐主键
     * @return 结果
     */
    public int deleteMusicByMusicId(Long musicId);

    /**
     * 批量删除音乐
     * 
     * @param musicIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMusicByMusicIds(Long[] musicIds);
}
