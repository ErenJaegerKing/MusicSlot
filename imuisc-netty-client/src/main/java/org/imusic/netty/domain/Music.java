package org.imusic.netty.domain;

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
    private String title;

    /** 歌手 */
    private String artist;

    /** 专辑 */
    private String album;

    /** 时长(秒) */
    private Long duration;

    /** 图片路径 */
    private String imagePath;

    /** 图片URL */
    private String imageUrl;

    /** 文件访问路径长*/
    private String filePath;

    /** Minio存储路径  短*/
    private String fileUrl;

    /** 文件MD5 */
    private String fileMd5;

    /** 状态（0正常 1停用） */
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

}
