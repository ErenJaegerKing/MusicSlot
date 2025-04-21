package com.ruoyi.system.util;

import org.apache.commons.io.FileUtils;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class MusicUtil {

    private static Logger log = LoggerFactory.getLogger(MusicUtil.class);

    /**
     * 获取MP3歌曲名、歌手、专辑、时长、封面
     * Minio地址专用，将文件下载到本地，然后再进行读取
     * @param url
     * @return
     */
    public static void getMP3Info(String url) {

        try {
            // 下载远程文件到临时文件
            File tempFile = downloadFileToTemp(url);

            // 使用 JAudiotagger 解析 MP3 文件
            MP3File mp3File = (MP3File) AudioFileIO.read(tempFile);
            AbstractID3v2Tag v2tag = mp3File.getID3v2Tag();

            // 歌名
            String songName = v2tag.getFirst(FieldKey.TITLE);
            // 歌手名
            String artist = v2tag.getFirst(FieldKey.ARTIST);
            // 专辑名
            String album = v2tag.getFirst(FieldKey.ALBUM);
            // 歌曲时长
            int length = mp3File.getMP3AudioHeader().getTrackLength();

            System.out.println("歌名: " + songName);
            System.out.println("歌手名: " + artist);
            System.out.println("专辑名: " + album);
            System.out.println("歌曲时长: " + length + " 秒");

            // 歌曲封面
            AbstractID3v2Frame frame = (AbstractID3v2Frame) v2tag.getFrame("APIC");
            if (frame != null) {
                FrameBodyAPIC body = (FrameBodyAPIC) frame.getBody();
                byte[] imageData = body.getImageData();
                try (FileOutputStream fos = new FileOutputStream("D://test1.jpg")) {
                    fos.write(imageData);
                }
                System.out.println("封面图片已保存到 D://test1.jpg");
            } else {
                System.out.println("未找到封面图片");
            }
            // 删除临时文件
            tempFile.delete();
        }catch (Exception e) {
            e.printStackTrace();
        }
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
}
