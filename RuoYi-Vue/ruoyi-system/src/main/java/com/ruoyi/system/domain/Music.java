package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.core.domain.BaseEntity1;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;

/**
 * 音乐对象 music
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
public class Music extends BaseEntity1
{
    private static final long serialVersionUID = 1L;

    /** 音乐ID */
    private Long musicId;

    /** 歌曲名称 */
    @Excel(name = " 歌曲名称")
    private String title;

    /** 歌手 */
    @Excel(name = "歌手")
    private String artist;

    /** 专辑 */
    @Excel(name = "专辑")
    private String album;

    /** 时长(秒) */
    @Excel(name = "时长(秒)")
    private Long duration;

    /** 图片路径 */
    @TableField("image_path")
    private String imagePath;

    /** 图片URL */
    @TableField("image_url")
    private String imageUrl;

    /** 文件访问路径  长*/
    @TableField("file_path")
    private String filePath;

    /** Minio存储路径  短*/
    @Excel(name = "Minio路径")
    private String fileUrl;

    /** 文件MD5 */
    @TableField("file_md5")
    private String fileMd5;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
    

    public Long getMusicId() 
    {
        return musicId;
    }

    public void setMusicId(Long musicId)
    {
        this.musicId = musicId;
    }

    public void setTitle(String title) 
    {
        this.title = title;
    }

    public String getTitle() 
    {
        return title;
    }

    public void setArtist(String artist) 
    {
        this.artist = artist;
    }

    public String getArtist() 
    {
        return artist;
    }

    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }

    public void setFilePath(String filePath) 
    {
        this.filePath = filePath;
    }

    public String getFilePath() 
    {
        return filePath;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("musicId", getMusicId())
            .append("title", getTitle())
            .append("artist", getArtist())
            .append("duration", getDuration())
            .append("filePath", getFilePath())
            .append("status", getStatus())
            .toString();
    }
}
