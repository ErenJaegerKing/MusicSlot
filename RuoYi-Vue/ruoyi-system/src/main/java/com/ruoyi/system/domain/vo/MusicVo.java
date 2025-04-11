package com.ruoyi.system.domain.vo;

import com.ruoyi.system.domain.Music;
import org.springframework.web.multipart.MultipartFile;

public class MusicVo {
    private MultipartFile file;
    
    private Music music;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }
}
