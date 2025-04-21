package com.ruoyi.util;


import com.ruoyi.system.util.MusicUtil;
import org.junit.Test;

public class MusicUtilTest {

    @Test
    public void getMusicInfo() {
//        MusicUtil.getMP3Info("C:\\Users\\dell\\Desktop\\音乐目录\\晴天 - 周杰伦.mp3");
        MusicUtil.getMP3Info("http://localhost:9000/radio/0362e4b182d581ecb733d7190bb16638.mp3");
    }

}