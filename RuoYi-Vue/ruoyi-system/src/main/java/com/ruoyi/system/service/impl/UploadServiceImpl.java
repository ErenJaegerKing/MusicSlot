package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.enums.RespEnum;
import com.ruoyi.system.domain.Files;
import com.ruoyi.system.domain.vo.FileUploadInfo;
import com.ruoyi.system.mapper.FilesMapper;
import com.ruoyi.system.service.IMusicService;
import com.ruoyi.system.service.UploadService;
import com.ruoyi.system.util.MinioUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UploadServiceImpl implements UploadService {

    private static Logger log = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Resource
    private FilesMapper filesMapper;

    @Resource
    private MinioUtil minioUtil;

    @Resource
    private RedisCache redisCache;

    @Autowired
    private IMusicService musicService;

    @Value("${minio.breakpoint-time}")
    private Integer breakpointTime;

    /**
     * 通过 md5 获取已上传的数据（断点续传）
     *
     * @param md5 String
     * @return Mono<Map < String, Object>>
     */
    @Override
    public AjaxResult getByFileMD5(String md5) {
        log.info("tip message: 通过 <{}> 查询redis是否存在", md5);
        AjaxResult r = AjaxResult.success();
        // 从redis获取文件名称和id
        FileUploadInfo fileUploadInfo = redisCache.getCacheObject(md5);
        if (fileUploadInfo != null) {
            // 正在上传，查询上传后的分片数据
            List<Integer> chunkList = minioUtil.getChunkByFileMD5(
                    fileUploadInfo.getFileName(),
                    fileUploadInfo.getUploadId(),
                    fileUploadInfo.getFileType());
            fileUploadInfo.setChunkUploadedList(chunkList);
            r.put("data", fileUploadInfo);
            r.put("code1", RespEnum.UPLOADING.getCode());
            r.put("msg", RespEnum.UPLOADING.getMessage());
            return r;
        }
        log.info("tip message: 通过 <{}> 查询mysql是否存在", md5);
        // 查询数据库是否上传成功
        Files one = filesMapper.selectOne(new LambdaQueryWrapper<Files>().eq(Files::getFileMd5, md5));
        if (one != null) {
            r.put("code1", RespEnum.UPLOADSUCCESSFUL.getCode());
            r.put("msg", RespEnum.UPLOADSUCCESSFUL.getMessage());
            r.put("data", one);
            return r;
        }
        r.put("code1", RespEnum.NOT_UPLOADED.getCode());
        r.put("msg", RespEnum.NOT_UPLOADED.getMessage());
        return r;
    }

    /**
     * 文件分片上传
     *
     * @param fileUploadInfo
     * @return Mono<Map < String, Object>>
     */
    @Override
    public Object initMultiPartUpload(FileUploadInfo fileUploadInfo) {
        FileUploadInfo redisFileUploadInfo = redisCache.getCacheObject(fileUploadInfo.getFileMd5());
        if (redisFileUploadInfo != null) {
            fileUploadInfo = redisFileUploadInfo;
        }
        log.info("tip message: 通过 <{}> 开始初始化<分片上传>任务", fileUploadInfo);
        // 获取桶 音乐 radio
        String bucketName = minioUtil.getBucketName(fileUploadInfo.getFileType());

        // 单文件上传
        if (fileUploadInfo.getChunkNum() == 1) {
            log.info("tip message: 当前分片数量 <{}> 进行单文件上传", fileUploadInfo.getChunkNum());
            // 保存到数据库中 | 保存一个音乐
            Files files = saveFileToDB(fileUploadInfo);
            String fileName = files.getUrl().substring(files.getUrl().lastIndexOf("/") + 1);
            return minioUtil.getUploadObjectUrl(fileName, bucketName);
        }
        // 分片上传
        else {
            log.info("tip message: 当前分片数量 <{}> 进行分片上传", fileUploadInfo.getChunkNum());
            Map<String, Object> map = minioUtil.initMultiPartUpload(fileUploadInfo, fileUploadInfo.getFileName(), fileUploadInfo.getChunkNum(), fileUploadInfo.getContentType(), bucketName);
            String uploadId = (String) map.get("uploadId");
            fileUploadInfo.setUploadId(uploadId);
            redisCache.setCacheObject(fileUploadInfo.getFileMd5(), fileUploadInfo, breakpointTime * 60 * 60 * 24, TimeUnit.SECONDS);
            return map;
        }
    }

    private Files saveFileToDB(FileUploadInfo fileUploadInfo) {
        String suffix = fileUploadInfo.getFileName().substring(fileUploadInfo.getFileName().lastIndexOf("."));
        String url = this.getFliePath(fileUploadInfo.getFileType().toLowerCase(), fileUploadInfo.getFileMd5() + suffix);
        //存入数据库
        Files files = new Files();
        BeanUtils.copyProperties(fileUploadInfo, files);
        files.setBucketName(fileUploadInfo.getFileType());
        files.setUrl(url);
        filesMapper.insert(files);

        return files;
    }

    @Override
    public String getFliePath(String bucketName, String fileName) {
        return minioUtil.getFliePath(bucketName, fileName);
    }

    /**
     * 文件合并
     *
     * @param
     * @return String
     */
    @Override
    public String mergeMultipartUpload(FileUploadInfo fileUploadInfo) {
        log.info("tip message: 通过 <{}> 开始合并<分片上传>任务", fileUploadInfo);
        FileUploadInfo redisFileUploadInfo = redisCache.getCacheObject(fileUploadInfo.getFileMd5());
        if (redisFileUploadInfo != null) {
            fileUploadInfo.setFileName(redisFileUploadInfo.getFileName());
        }
        boolean result = minioUtil.mergeMultipartUpload(fileUploadInfo.getFileName(), fileUploadInfo.getUploadId(), fileUploadInfo.getFileType());

        //合并成功
        if (result) {
            //存入数据库
            Files files = saveFileToDB(fileUploadInfo);
            redisCache.deleteObject(fileUploadInfo.getFileMd5());
            return files.getUrl();
        }
        return null;
    }

}
