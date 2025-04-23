package com.ruoyi.system.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.domain.TimeSlotMusic;
import com.ruoyi.system.mapper.MusicMapper;
import com.ruoyi.system.mapper.TimeSlotMusicMapper;
import com.ruoyi.system.service.IMusicService;
import com.ruoyi.system.util.MinioUtil;
import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    @Autowired
    private MinioUtil minioUtil;

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
     */
    @Override
    public int insertMusic(String url) {
        Music music = new Music();
        music.setFileUrl(url);
        // 返回一个Minio中默认的图片,手动放一张图片
        music.setImageUrl(minioUtil.getFliePath("image", "default.jpg"));
        try {
            // 下载远程文件到临时文件
            File tempFile = downloadFileToTemp(url);

            // 使用 JAudiotagger 解析 MP3 文件
            MP3File mp3File = (MP3File) AudioFileIO.read(tempFile);
            if (mp3File.getID3v2Tag() == null) {
                mp3File.setID3v2Tag(new ID3v22Tag());
            }
            AbstractID3v2Tag v2tag = mp3File.getID3v2Tag();

            String title = v2tag.getFirst(FieldKey.TITLE);
            String artist = v2tag.getFirst(FieldKey.ARTIST);
            String album = v2tag.getFirst(FieldKey.ALBUM);
            // 歌名
            if (title == null || title.trim().isEmpty()) title = "未知歌曲";
            // 歌手名
            if (artist == null || artist.trim().isEmpty()) artist = "未知歌手";
            // 专辑名
            if (album == null || album.trim().isEmpty()) album = "未知专辑";
            // 歌曲时长
            long length = mp3File.getMP3AudioHeader().getTrackLength();
            music.setTitle(title);
            music.setArtist(artist);
            music.setAlbum(album);
            music.setDuration(length > 0 ? length : 0);

            // 歌曲封面
            AbstractID3v2Frame frame = (AbstractID3v2Frame) v2tag.getFrame("APIC");
            if (frame != null) {
                FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
                byte[] imageData = body.getImageData();
                if (imageData != null && imageData.length > 0) {
                    String fileName = UUID.randomUUID() + ".jpg";
                    // 首次进入如果没有桶的话，会自动创建
                    String imageUrl = MinioUtil.uploadMusicFile("image", fileName, imageData);
                    music.setImageUrl(imageUrl);
                }
            }
            // 删除临时文件
            tempFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return musicMapper.insertMusic(music);
    }

    /**
     * 将远程文件下载到临时文件
     *
     * @param urlString 远程文件的 URL
     * @return 临时文件对象
     * @throws IOException 如果下载失败
     */
    private static File downloadFileToTemp(String urlString) throws IOException {
        URL url = new URL(urlString);
        File tempFile = File.createTempFile("tempMp3", ".mp3");
        FileUtils.copyURLToFile(url, tempFile);
        return tempFile;
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
            throw new ServiceException("时间段关联，无法删除", 500);
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
