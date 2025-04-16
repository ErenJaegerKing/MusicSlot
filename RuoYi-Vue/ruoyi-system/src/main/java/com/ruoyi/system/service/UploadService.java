package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.vo.FileUploadInfo;

public interface UploadService {
    /**
     *  通过 md5 获取已上传的数据
     * @param md5 String
     * @return Mono<Map<String, Object>>
     */
    AjaxResult getByFileMD5(String md5);

    /**
     * 分片上传初始化
     *
     * @param fileUploadInfo
     * @return Map<String, Object>
     */
    Object initMultiPartUpload(FileUploadInfo fileUploadInfo);

    /**
     *  获取文件地址
     * @param bucketName
     * @param fileName
     *
     */
    String getFliePath(String bucketName, String fileName);
    
    /**
     * 完成分片上传
     *
     * @param  fileUploadInfo
     * @return String
     */
    String mergeMultipartUpload(FileUploadInfo fileUploadInfo);
}
