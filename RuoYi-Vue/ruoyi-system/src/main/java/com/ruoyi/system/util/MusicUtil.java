package com.ruoyi.system.util;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;

public class MusicUtil {

    private static Logger log = LoggerFactory.getLogger(MusicUtil.class);

    /**
     * 获取MP3歌曲名、歌手、专辑、时长、封面
     *
     * @param url
     * @return
     */
    public static void getMP3Info(String url) {

        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(new File(url));
            AbstractID3v2Tag v2tag = mp3File.getID3v2Tag();

            // 歌名
            String songName = v2tag.getFirst(FieldKey.TITLE);
            // 歌手名
            String artist = v2tag.getFirst(FieldKey.ARTIST);
            // 专辑名
            String album = v2tag.getFirst(FieldKey.ALBUM);
            // 歌曲时长
            MP3AudioHeader header = mp3File.getMP3AudioHeader();
            int length = header.getTrackLength();

            // 歌曲封面
            AbstractID3v2Tag tag = mp3File.getID3v2Tag();
            AbstractID3v2Frame frame = (AbstractID3v2Frame) tag.getFrame("APIC");
            FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
            byte[] imageData = body.getImageData();
            FileOutputStream fos = new FileOutputStream("D://test1.jpg");
            fos.write(imageData);
            fos.close();
        }catch (Exception e) {

        }
    }
}
