package com.ruoyi.system.domain.vo;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public class FileUploadInfo {

    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @NotBlank(message = "Content-Type不能为空")
    private String contentType;

    @NotNull(message = "分片数量不能为空")
    private Integer chunkNum;

    @NotBlank(message = "uploadId 不能为空")
    private String uploadId;

    private Long chunkSize;

    // 桶名称
    //private String bucketName;

    //md5
    private String fileMd5;

    //文件类型
    private String fileType;

    //已上传的分片索引+1
    private List<Integer> chunkUploadedList;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getChunkNum() {
        return chunkNum;
    }

    public void setChunkNum(Integer chunkNum) {
        this.chunkNum = chunkNum;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public List<Integer> getChunkUploadedList() {
        return chunkUploadedList;
    }

    public void setChunkUploadedList(List<Integer> chunkUploadedList) {
        this.chunkUploadedList = chunkUploadedList;
    }
}

