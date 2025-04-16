package com.ruoyi.web.controller.music;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.RespEnum;
import com.ruoyi.system.domain.vo.FileUploadInfo;
import com.ruoyi.system.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * minio上传流程
 * <p>
 * 1.检查数据库中是否存在上传文件
 * <p>
 * 2.根据文件信息初始化，获取分片预签名url地址，前端根据url地址上传文件
 * <p>
 * 3.上传完成后，将分片上传的文件进行合并
 * <p>
 * 4.保存文件信息到数据库
 */
@RestController
@RequestMapping("upload")
public class FileMinioController {

    private static Logger log = LoggerFactory.getLogger(FileMinioController.class);

    @Resource
    private UploadService uploadService;

    /**
     * 校验文件是否存在
     *
     * @param md5 String
     * @return ResponseResult<Object>
     */
    @GetMapping("/multipart/check")
    public AjaxResult checkFileUploadedByMd5(@RequestParam("md5") String md5) {
        log.info("REST: 通过查询 <{}> 文件是否存在、是否进行断点续传", md5);
        if (StringUtils.isEmpty(md5)) {
            log.error("查询文件是否存在、入参无效");
            return AjaxResult.error(RespEnum.ACCESS_PARAMETER_INVALID.getCode(), RespEnum.ACCESS_PARAMETER_INVALID.getMessage());
        }
        return uploadService.getByFileMD5(md5);
    }

    /**
     * 分片初始化
     *
     * @param fileUploadInfo 文件信息
     * @return ResponseResult<Object>
     */
    @PostMapping("/multipart/init")
    public AjaxResult initMultiPartUpload(@RequestBody FileUploadInfo fileUploadInfo) {
        log.info("REST: 通过 <{}> 初始化上传任务", fileUploadInfo);
        return AjaxResult.success(uploadService.initMultiPartUpload(fileUploadInfo));
    }

    /**
     * 完成上传
     *
     * @param fileUploadInfo  文件信息
     * @return ResponseResult<Object>
     */
    @PostMapping("/multipart/merge")
    public AjaxResult completeMultiPartUpload(@RequestBody FileUploadInfo fileUploadInfo) {
        log.info("REST: 通过 {} 合并上传任务", fileUploadInfo);
        //合并文件
        String url = uploadService.mergeMultipartUpload(fileUploadInfo);
        //获取上传文件地址
        if (StringUtils.isNotBlank(url)) {
            return AjaxResult.success(url);
        }
        return AjaxResult.error();
    }
    
}
