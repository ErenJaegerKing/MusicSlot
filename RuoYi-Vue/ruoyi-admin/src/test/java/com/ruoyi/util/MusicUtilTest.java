package com.ruoyi.util;


import com.ruoyi.system.util.MusicUtil;
import org.junit.Test;

public class MusicUtilTest {

    @Test
    public void getMusicInfo() {
        MusicUtil.getMP3Info("C:\\Users\\dell\\Desktop\\音乐目录\\晴天 - 周杰伦.mp3");
    }

}