package com.ruoyi.system.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.HashMultimap;
import com.ruoyi.common.config.CustomMinioClient;
import com.ruoyi.common.core.domain.R1;
import com.ruoyi.common.enums.RespEnum;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.vo.FileUploadInfo;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Part;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Minio 文件存储工具类
 *
 * @author ruoyi
 */
@Component
public class MinioUtil {

    @Value(value = "${minio.endpoint}")
    private String endpoint;

    @Value(value = "${minio.accessKey}")
    private String accesskey;

    @Value(value = "${minio.secretKey}")
    private String secretkey;

    @Value(value = "${minio.expiry}")
    private Integer expiry;

    // 声明自定义Minio客户端 通过PostConstruct初始化
    private static CustomMinioClient customMinioClient;

    private static Logger log = LoggerFactory.getLogger(MinioUtil.class);

    /**
     * 用spring的自动注入会注入失败
     */
    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(endpoint) ||
                StringUtils.isEmpty(accesskey) ||
                StringUtils.isEmpty(secretkey)) {
            throw new IllegalStateException("MinIO配置不完整");
        }
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accesskey, secretkey)
                .build();
        customMinioClient = new CustomMinioClient(minioClient);
    }


    /**
     * 上传文件
     *
     * @param bucketName 桶名称
     * @param fileName
     * @throws IOException
     */
    public static String uploadFile(String bucketName, String fileName, MultipartFile multipartFile) throws IOException {
        String url = "";
        MinioClient minioClient = SpringUtils.getBean(MinioClient.class);
        try (InputStream inputStream = multipartFile.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(inputStream, multipartFile.getSize(), -1).contentType(multipartFile.getContentType()).build());
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).method(Method.GET).build());
            url = url.substring(0, url.indexOf('?'));
            return ServletUtils.urlDecode(url);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * 上传音乐专用
     *
     * @param bucketName 桶名称
     * @param fileName
     * @throws IOException
     */
    public static String uploadMusicFile(String bucketName, String fileName, byte[] imageData) throws IOException {
        String url = "";
        MinioClient minioClient = SpringUtils.getBean(MinioClient.class);
        try {
            //如果桶不存在，就创建桶
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            if (!customMinioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
                customMinioClient.makeBucket(makeBucketArgs);
            }
            minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(new ByteArrayInputStream(imageData),imageData.length, -1).contentType("image/jpeg").build());
            url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder().bucket(bucketName).object(fileName).method(Method.GET).build());
            url = url.substring(0, url.indexOf('?'));
            return ServletUtils.urlDecode(url);
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }


    /**
     * 通过 sha256 获取上传中的分片信息
     *
     * @param objectName 文件全路径名称
     * @param uploadId   返回的uploadId
     * @param bucketName 桶名称
     * @return Mono<Map < String, Object>>
     */
    public List<Integer> getChunkByFileMD5(String objectName, String uploadId, String bucketName) {
        log.info("通过 <{}-{}-{}> 查询<minio>上传分片数据", objectName, uploadId, bucketName);
        try {
            // 查询上传后的分片数据
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            return partResult.result().partList().stream().map(Part::partNumber).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("error message: 查询上传后的分片信息失败、原因:", e);
            return null;
        }
    }

    /**
     * 根据文件类型获取minio桶名称
     *
     * @param fileType
     * @return
     */
    public String getBucketName(String fileType) {
        try {
            //String bucketName = getProperty(fileType.toLowerCase());

            if (fileType != null && !fileType.equals("")) {
                //判断桶是否存在
                String bucketName2 = createBucket(fileType.toLowerCase());
                if (bucketName2 != null && !bucketName2.equals("")) {
                    return bucketName2;
                } else {
                    return fileType;
                }
            }

        } catch (Exception e) {

            log.error("Error reading bucket name ");
        }
        return fileType;
    }

    /**
     * 创建一个桶
     *
     * @return
     */
    public String createBucket(String bucketName) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucketName).build();
            //如果桶存在
            if (customMinioClient.bucketExists(bucketExistsArgs)) {
                return bucketName;
            }
            MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder().bucket(bucketName).build();
            customMinioClient.makeBucket(makeBucketArgs);
            return bucketName;
        } catch (Exception e) {
            log.error("创建桶失败：{}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名称
     * @param fileName   文件名
     * @return
     */
    public String getFliePath(String bucketName, String fileName) {
        return StrUtil.format("{}/{}/{}", endpoint, bucketName, fileName);//文件访问路径
    }

    /**
     * 单文件签名上传
     *
     * @param objectName 文件全路径名称
     * @param bucketName 桶名称
     * @return /
     */
    public Map<String, Object> getUploadObjectUrl(String objectName, String bucketName) {
        try {
            log.info("tip message: 通过 <{}-{}> 开始单文件上传<minio>", objectName, bucketName);
            Map<String, Object> resMap = new HashMap();
            List<String> partList = new ArrayList<>();
            String url = customMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiry, TimeUnit.DAYS)
                            .build());
            log.info("tip message: 单个文件上传、成功");
            // 上传后端返回的地址的问题
            log.info("单文件上传，客户端IP{}，本机IP{}", IpUtils.getIpAddr(), IpUtils.getHostIp());
            partList.add(url);
            resMap.put("uploadId", "SingleFileUpload");
            resMap.put("urlList", partList);
            return resMap;
        } catch (Exception e) {
            log.error("error message: 单个文件上传失败、原因:", e);
            // 返回 文件上传失败
            return null;
        }
    }

    /**
     * 初始化分片上传
     *
     * @param fileUploadInfo
     * @param objectName     文件全路径名称
     * @param chunkNum       分片数量
     * @param contentType    类型，如果类型使用默认流会导致无法预览
     * @param bucketName     桶名称
     * @return Mono<Map < String, Object>>
     */
    public Map<String, Object> initMultiPartUpload(FileUploadInfo fileUploadInfo, String objectName, int chunkNum, String contentType, String bucketName) {
        log.info("tip message: 通过 <{}-{}-{}-{}> 开始初始化<分片上传>数据", objectName, chunkNum, contentType, bucketName);
        Map<String, Object> resMap = new HashMap<>();
        try {
            if (CharSequenceUtil.isBlank(contentType)) {
                contentType = "application/octet-stream";
            }
            HashMultimap<String, String> headers = HashMultimap.create();

            headers.put("Content-Type", contentType);

            //获取uploadId
            String uploadId = null;
            if (StringUtils.isBlank(fileUploadInfo.getUploadId())) {
                uploadId = customMinioClient.initMultiPartUpload(bucketName, null, objectName, headers, null);
            } else {
                uploadId = fileUploadInfo.getUploadId();
            }

            resMap.put("uploadId", uploadId);

            fileUploadInfo.setUploadId(uploadId);
            fileUploadInfo.setChunkNum(chunkNum);

            List<String> partList = new ArrayList<>();

            Map<String, String> reqParams = new HashMap<>();
            reqParams.put("uploadId", uploadId);
            for (int i = 1; i <= chunkNum; i++) {
                reqParams.put("partNumber", String.valueOf(i));
                String uploadUrl = customMinioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectName)
                                .expiry(1, TimeUnit.DAYS)
                                .extraQueryParams(reqParams)
                                .build());
                partList.add(uploadUrl);
            }
            log.info("tip message: 文件初始化<分片上传>、成功");
            log.info("初始化分片上传，客户端IP{}，本机IP{}", IpUtils.getIpAddr(), IpUtils.getHostIp());
            resMap.put("urlList", partList);
            return resMap;
        } catch (Exception e) {
            log.error("error message: 初始化分片上传失败、原因:", e);
            // 返回 文件上传失败
            return R1.error(RespEnum.UPLOAD_FILE_FAILED);
        }
    }

    /**
     * 分片上传完后合并
     *
     * @param objectName 文件全路径名称
     * @param uploadId   返回的uploadId
     * @param bucketName 桶名称
     * @return boolean
     */
    public boolean mergeMultipartUpload(String objectName, String uploadId, String bucketName) {
        try {
            log.info("tip message: 通过 <{}-{}-{}> 合并<分片上传>数据", objectName, uploadId, bucketName);
            //目前仅做了最大1000分片
            Part[] parts = new Part[1000];
            // 查询上传后的分片数据
            ListPartsResponse partResult = customMinioClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            int partNumber = 1;
            for (Part part : partResult.result().partList()) {
                parts[partNumber - 1] = new Part(partNumber, part.etag());
                partNumber++;
            }
            // 合并分片
            customMinioClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);

        } catch (Exception e) {
            log.error("error message: 合并失败、原因:", e);
            //删除redis的数据 如果发生错误，也可以等待redis过期
            return false;
        }
        return true;
    }

    // 判断桶是否存在 创建桶
    public Boolean isBucketExists(String bucketName) {
        Boolean isExists = false;
        try {
            isExists = customMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExists) {
                customMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return isExists;
    }

    // 上传文件
    public void uploadObject(String bucketName,String path) {
        try {
            customMinioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName).filename(path).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 获取所有桶
    public void getAllBuckets() {
        try {
            List<Bucket> buckets = customMinioClient.listBuckets();
            buckets.forEach(bucket -> {
                System.out.println(bucket.creationDate().toLocalDateTime() + "," + bucket.name());
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 删除桶
    public void removeBucket(String bucketName) {
        try {
            customMinioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 下载文件
    public void downloadObject(String bucketName,String object,String path) {
        try {
            customMinioClient.downloadObject(DownloadObjectArgs.builder()
                    .bucket(bucketName)
                    .object(object) // Saved Pictures/001420yp398.jpg
                    .filename(path)
                    .build());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 删除对象
    public void removeObject(String bucketName,String object) {
        try {
            customMinioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(object).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}