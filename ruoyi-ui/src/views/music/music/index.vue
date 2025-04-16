<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:music:add']"
        >新增曲目
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:music:remove']"
        >删除曲目
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="musicList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="音乐ID" align="center" prop="musicId" width="100px"/>
      <el-table-column label="歌曲" align="center" prop="title"/>
      <!--      <el-table-column label="时长(秒)" align="center" prop="duration"/>-->
      <!--      <el-table-column label="路径" align="center" prop="fileUrl"/>-->
      <!--      <el-table-column label="状态" align="center" prop="status"/>-->
      <!--      <el-table-column label="备注" align="center" prop="remark"/>-->
      <el-table-column label="播放" align="center">
        <template #default="scope">
          <!-- 自定义内容：注入音乐播放器 -->
          <div>
            <audio controls :src="modifiedPath(scope.row.filePath)" style="width: 100%; max-width: 200px;">
              您的浏览器不支持音频播放。
            </audio>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:music:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改音乐对话框 -->
    <el-dialog :title="title" :visible.sync="open" append-to-body @close="handleClose">
      <el-upload class="upload-demo"
                 ref="upload"
                 action="#"
                 :on-remove="handleRemove"
                 :on-change="handleFileChange"
                 :file-list="uploadFileList"
                 :show-file-list="false"
                 :auto-upload="false"
                 multiple>
        <el-button slot="trigger" type="primary" plain>选择文件</el-button>
        <el-button style="margin-left: 5px;" type="success" @click="handler" plain>上传</el-button>
        <el-button type="danger" @click="clearFileHandler" plain>清空</el-button>
        <table style="margin-top: 20px">
          <th style="display:inline-block;font-size: 12px;color: #909399;margin-left: 100px">文件名</th>
          <th style="display:inline-block;;font-size: 12px;color: #909399;margin-left: 130px;">文件大小</th>
          <th style="display:inline-block;;font-size: 12px;color: #909399;margin-left: 130px">上传进度</th>
          <th style="display:inline-block;;font-size: 12px;color: #909399;margin-left: 150px">状态</th>
        </table>
        <!-- 文件列表 -->
        <div class="file-list-wrapper">
          <el-collapse>
            <el-collapse-item v-for="(item, index) in uploadFileList" :key="index">
              <template slot="title">
                <div class="upload-file-item">
                  <div class="file-info-item file-name" :title="item.name">{{ item.name }}</div>
                  <div class="file-info-item file-size">{{ formatBytes(item.size) }}</div>
                  <div class="file-info-item file-progress">
                    <span class="file-progress-label"></span>
                    <el-progress :percentage="item.uploadProgress" class="file-progress-value"/>
                  </div>
                  <div class="file-info-item file-size"><span></span>
                    <el-tag v-if="item.status === '等待上传'" size="medium" type="info">等待上传</el-tag>
                    <el-tag v-else-if="item.status === '校验MD5'" size="medium" type="warning">校验MD5</el-tag>
                    <el-tag v-else-if="item.status === '正在上传'" size="medium">正在上传</el-tag>
                    <el-tag v-else-if="item.status === '上传成功'" size="medium" type="success">上传完成</el-tag>
                    <el-tag v-else size="medium">正在上传</el-tag>
                    <!--                                <el-tag v-else size="medium" type="danger">上传错误</el-tag>-->
                  </div>
                </div>
              </template>
              <div class="file-chunk-list-wrapper">
                <!-- 分片列表 -->
                <el-table :data="item.chunkList" max-height="400" style="width: 100%">
                  <el-table-column prop="chunkNumber" label="分片序号" width="180">
                  </el-table-column>
                  <el-table-column prop="progress" label="上传进度">
                    <template v-slot="{ row }">
                      <el-progress v-if="!row.status || row.progressStatus === 'normal'"
                                   :percentage="row.progress"/>
                      <el-progress v-else :percentage="row.progress" :status="row.progressStatus"
                                   :text-inside="true" :stroke-width="16"/>
                    </template>
                  </el-table-column>
                  <el-table-column prop="status" label="状态" width="180">
                  </el-table-column>
                </el-table>
              </div>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-upload>
    </el-dialog>
  </div>
</template>

<script>
import {listMusic, getMusic, delMusic} from "@/api/music/music";
import axios from "axios";
import SparkMD5 from 'spark-md5';
import {checkUpload, initUpload, mergeUpload} from "@/api/system/upload";
import {fileSuffixTypeUtil} from "@/utils/FileUtil";

export default {
  name: "Music",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 音乐表格数据
      musicList: [],
      // 弹出层标题
      title: "新增音乐",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        artist: null,
        duration: null,
        filePath: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {},

      // Minio分片上传 ----------------------------------------------------------------

      // 上传文件列表
      uploadFileList: [],
      // 文件状态
      FileStatus: {
        wait: '等待上传',
        getMd5: '校验MD5',
        chip: '正在创建序列',
        uploading: '正在上传',
        success: '上传成功',
        error: '上传错误'
      },
      // ?
      uploadIdInfo: null,
      // 上传并发数
      simultaneousUploads: 3,
      // 当前文件索引
      currentFileIndex: 0,
      // 文件上传Id主键
      FILE_UPLOAD_ID_KEY: 'file_upload_id',
      // 分片大小
      chunkSize: 30 * 1024 * 1024,
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /**
     * 移除文件列表
     * @param {*} file
     * @param {*} fileList
     */
    handleRemove(file, fileList) {
      this.uploadFileList = fileList;
    },
    /**
     * 上传文件列表
     * @param {*} file
     * @param {*} fileList
     */
    handleFileChange(file, fileList) {
      this.initFileProperties(file)
      this.uploadFileList = fileList
    },

    //初始化文件属性
    initFileProperties(file) {
      file.chunkList = []
      file.status = this.FileStatus.wait
      file.progressStatus = 'warning'
      file.uploadProgress = 0
    },

    /**
     * 清空列表
     */
    clearFileHandler() {
      this.uploadFileList = []
      this.uploadIdInfo = null
      this.currentFileIndex = 0
    },

    // 字节大小格式化工具
    formatBytes(size) {
      if (!size) return '0B';
      const unitSize = 1024;
      if (size < unitSize) return size + ' B';
      if (size < Math.pow(unitSize, 2)) return (size / unitSize).toFixed(2) + ' KB';
      if (size < Math.pow(unitSize, 3)) return (size / Math.pow(unitSize, 2)).toFixed(2) + ' MB';
      if (size < Math.pow(unitSize, 4)) return (size / Math.pow(unitSize, 3)).toFixed(2) + ' GB';
      return (size / Math.pow(unitSize, 4)).toFixed(2) + ' TB';
    },

    /**
     * 开始上传文件
     */
    handler() {
      const self = this
      //判断文件列表是否为空
      if (this.uploadFileList.length === 0) {
        this.$message.error('请先选择文件')
        return
      }
      if (this.currentFileIndex >= this.uploadFileList.length) {
        this.$message.success("文件上传成功")
        return;
      }
      //当前操作文件
      // l 2获得当前文件（设置文件状态为获取MD5 | 初始化已上传分片列表）
      const currentFile = this.uploadFileList[this.currentFileIndex]
      console.log("当前操作文件：", currentFile)
      //debugger
      //更新上传标签
      currentFile.status = this.FileStatus.getMd5
      currentFile.chunkUploadedList = []

      //截取封面图片
      //this.ScreenshotVideo(currentFile.raw);

      // 1. 计算文件MD5
      // l 3计算文件的MD5值
      this.getFileMd5(currentFile.raw, async (md5, totalChunks) => {
        console.log("md5值", md5)
        // 2. 检查是否已上传
        // l 4检查文件是否已上传 [已上传code=1直接跳到下一个文件 部分上传code=2获取已上传分片列表 未上传继续上传流程]
        const checkResult = await self.checkFileUploadedByMd5(md5)
        console.log("检查是否已上传-->", checkResult)
        // debugger
        if (checkResult.code1 === 1) {
          //self.$message.success(`上传成功，文件地址：${checkResult.data.url}`)
          console.log('上传成功文件访问地址：' + checkResult.data.url)
          currentFile.status = this.FileStatus.success
          currentFile.uploadProgress = 100
          //如果此文件上传过，就跳到下一个文件
          this.currentFileIndex++;
          this.handler()
          return
        } else if (checkResult.code1 === 2) {  // "上传中" 状态
          // 获取已上传分片列表
          console.log("上传中：", checkResult)
          currentFile.status = this.FileStatus.uploading
          let chunkUploadedList = checkResult.data.chunkUploadedList
          console.log("chunkUploadedList", chunkUploadedList)
          currentFile.chunkUploadedList = chunkUploadedList
          console.log("成功上传的分片信息", chunkUploadedList)
        } else {   // 未上传
          console.log('未上传')
        }
        // 3. 正在创建分片
        currentFile.status = this.FileStatus.chip;

        //创建分片
        // l 5创建文件分片 将文件分割成指定大小的分片
        let fileChunks = self.createFileChunk(currentFile.raw, this.chunkSize);

        //重命名文件
        //let fileName = this.getNewFileName(currentFile)

        // 获取文件类型
        //let type = currentFile.name.substring(currentFile.name.lastIndexOf(".") + 1)
        let type = fileSuffixTypeUtil(currentFile.name)

        // l 6准备上传参数
        let param = {
          fileName: currentFile.name,
          fileSize: currentFile.size,
          chunkSize: this.chunkSize,
          chunkNum: totalChunks,
          fileMd5: md5,
          contentType: 'application/octet-stream',
          fileType: type,
          //uploadId:localStorage.getItem("file_upload_id"),
          chunkUploadedList: currentFile.chunkUploadedList//已上传的分片索引+1
        }
        // debugger
        // 4. 获取上传url
        // l 7获取上传URL 从服务器获取每个分片的上传地址
        let uploadIdInfoResult = await self.getFileUploadUrls(param)
        //debugger

        let uploadIdInfo = uploadIdInfoResult.data
        console.log("获取上传url-->", uploadIdInfo)

        let uploadUrls = uploadIdInfo.urlList
        // l 8初始化分片列表（为每个分片创建上传状态对象 | 标记已上传的分片）
        self.$set(currentFile, 'chunkList', [])

        if (uploadUrls !== undefined) {
          if (fileChunks.length !== uploadUrls.length) {
            self.$message.error('文件分片上传地址获取错误')
            return
          }
        }
        fileChunks.map((chunkItem, index) => {
          if (currentFile.chunkUploadedList.indexOf(index + 1) !== -1) {
            currentFile.chunkList.push({
              chunkNumber: index + 1,
              chunk: chunkItem,
              uploadUrl: uploadUrls[index],
              progress: 100,
              progressStatus: 'success',
              status: '上传成功'
            })
          } else {
            currentFile.chunkList.push({
              chunkNumber: index + 1,
              chunk: chunkItem,
              uploadUrl: uploadUrls[index],
              progress: 0,
              status: '—'
            })
          }
        })
        console.log("所有分片信息：", currentFile.chunkList)
        let tempFileChunks = []

        currentFile.chunkList.forEach((item) => {
          tempFileChunks.push(item)
        })

        //更新状态
        currentFile.status = this.FileStatus.uploading

        // 处理分片列表，删除已上传的分片
        // l 9过滤已上传分片
        tempFileChunks = self.processUploadChunkList(tempFileChunks)
        console.log("删除已上传的分片-->", tempFileChunks)
        // 5. 上传
        // l 10上传分片
        await self.uploadChunkBase(tempFileChunks)


        console.log('---上传完成---')

        //判断是否单文件上传或者分片上传
        if (uploadIdInfo.uploadId === "SingleFileUpload") {
          console.log("单文件上传");
          //更新状态
          currentFile.status = this.FileStatus.success
          //文件下标偏移
          this.currentFileIndex++;
          //递归上传下一个文件
          this.handler()
          return
        } else {
          // 6. 合并文件
          console.log("合并文件-->", currentFile)
          // l 11合并文件，所有分片上传完成后，请求服务器合并文件
          const mergeResult = await self.mergeFile({
            uploadId: uploadIdInfo.uploadId,
            fileName: currentFile.name,
            fileMd5: md5,
            fileType: type,
            chunkNum: uploadIdInfo.urlList.length,
            chunkSize: this.chunkSize,
            fileSize: currentFile.size
          })

          //合并文件状态
          if (!mergeResult.data) {
            currentFile.status = this.FileStatus.error
            self.$message.error(mergeResult.error)
          } else {
            localStorage.removeItem(this.FILE_UPLOAD_ID_KEY)
            currentFile.status = this.FileStatus.success
            console.log('文件访问地址：' + mergeResult.data)
            // l 12处理下一个文件
            //文件下标偏移
            this.currentFileIndex++;
            //递归上传下一个文件
            this.handler()
          }
        }
      })
    },

    /**
     * 分片读取文件 获取文件的MD5
     * @param file
     * @param callback
     */
    getFileMd5(file, callback) {
      const self = this
      // l 获取文件切片方法的兼容性写法
      const blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice
      // l 创建一个 FileReader 对象用于读取文件内容
      const fileReader = new FileReader()
      // 计算分片数
      const totalChunks = Math.ceil(file.size / this.chunkSize)
      console.log('总分片数：' + totalChunks)
      let currentChunk = 0
      const spark = new SparkMD5.ArrayBuffer()
      // l 加载和处理分片
      loadNext()
      // l 文件读取回调
      fileReader.onload = function (e) {
        try {
          spark.append(e.target.result)
        } catch (error) {
          console.log('获取Md5错误：' + currentChunk)
        }
        if (currentChunk < totalChunks) {
          currentChunk++
          loadNext()
        } else {
          callback(spark.end(), totalChunks)
        }
      }
      fileReader.onerror = function () {
        console.warn('读取Md5失败，文件读取错误')
      }

      function loadNext() {
        const start = currentChunk * self.chunkSize
        const end = ((start + self.chunkSize) >= file.size) ? file.size : start + self.chunkSize
        // 注意这里的 fileRaw
        // l FileReader 是单次操作的，但每次调用 readAsArrayBuffer() 都会重新触发 onload
        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end))
      }
    },

    /**
     * 根据MD5查看文件是否上传过
     * @param md5
     * @returns {Promise<unknown>}
     */
    checkFileUploadedByMd5(md5) {
      return new Promise((resolve, reject) => {
        checkUpload(md5).then(response => {
          console.log("md5-->:", response);
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    /**
     * 创建文件分片
     */
    createFileChunk(file, size = this.chunkSize) {
      const fileChunkList = []
      let count = 0
      while (count < file.size) {
        fileChunkList.push({
          // l 切割当前分片
          file: file.slice(count, count + size),
        })
        count += size
      }
      return fileChunkList
    },

    /**
     * 获取直接上传的uri链接
     * @param fileParam
     * @returns {Promise<AxiosResponse<any>>}
     */
    getFileUploadUrls(fileParam) {
      return initUpload(fileParam)
    },

    /**
     * 处理即将上传的分片列表，判断是否有已上传的分片，有则从列表中删除
     */
    processUploadChunkList(chunkList) {
      const currentFile = this.uploadFileList[this.currentFileIndex]
      let chunkUploadedList = currentFile.chunkUploadedList
      if (chunkUploadedList === undefined || chunkUploadedList === null || chunkUploadedList.length === 0) {
        return chunkList
      }
      //
      for (let i = chunkList.length - 1; i >= 0; i--) {
        const chunkItem = chunkList[i]
        for (let j = 0; j < chunkUploadedList.length; j++) {
          if (chunkItem.chunkNumber === chunkUploadedList[j]) {
            chunkList.splice(i, 1)
            break
          }
        }
      }
      return chunkList
    },

    /**
     * 上传分片
     * @param chunkList
     * @returns {Promise<unknown>}
     */
    uploadChunkBase(chunkList) {
      const self = this
      let successCount = 0
      let totalChunks = chunkList.length
      return new Promise((resolve, reject) => {
        const handler = () => {
          if (chunkList.length) {

            const chunkItem = chunkList.shift()
            // 直接上传二进制，不需要构造 FormData，否则上传后文件损坏
            axios.put(chunkItem.uploadUrl, chunkItem.chunk.file, {
              // 上传进度处理
              onUploadProgress: self.checkChunkUploadProgress(chunkItem),
              headers: {
                'Content-Type': 'application/octet-stream'
              }
            }).then(response => {
              if (response.status === 200) {
                console.log('分片：' + chunkItem.chunkNumber + ' 上传成功')
                successCount++
                // 继续上传下一个分片
                //  debugger
                handler()
              } else {
                console.log('上传失败：' + response.status + '，' + response.statusText)
              }
            })
              .catch(error => {
                // 更新状态
                console.log('分片：' + chunkItem.chunkNumber + ' 上传失败，' + error)
                // 重新添加到队列中
                chunkList.push(chunkItem)
                handler()
              })
          }
          if (successCount >= totalChunks) {
            resolve()
          }
        }
        // 并发
        for (let i = 0; i < this.simultaneousUploads; i++) {
          handler()
        }
      })
    },

    /**
     * 检查分片上传进度
     */
    checkChunkUploadProgress(item) {
      return p => {
        item.progress = parseInt(String((p.loaded / p.total) * 100))
        console.log("进度：", this.uploadFileList[this.currentFileIndex].uploadProgress)
        this.updateChunkUploadStatus(item)
      }
    },
    updateChunkUploadStatus(item) {
      let status = this.FileStatus.uploading
      let progressStatus = 'normal'
      if (item.progress >= 100) {
        status = this.FileStatus.success
        progressStatus = 'success'
      }
      let chunkIndex = item.chunkNumber - 1
      let currentChunk = this.uploadFileList[this.currentFileIndex].chunkList[chunkIndex]
      // 修改状态
      currentChunk.status = status
      currentChunk.progressStatus = progressStatus
      // 更新状态
      this.$set(this.uploadFileList[this.currentFileIndex].chunkList, chunkIndex, currentChunk)
      // 获取文件上传进度
      this.getCurrentFileProgress()
    },
    getCurrentFileProgress() {
      const currentFile = this.uploadFileList[this.currentFileIndex]
      if (!currentFile || !currentFile.chunkList) {
        return
      }
      const chunkList = currentFile.chunkList
      const uploadedSize = chunkList.map((item) => item.chunk.file.size * item.progress).reduce((acc, cur) => acc + cur)
      // 计算方式：已上传大小 / 文件总大小
      let progress = parseInt((uploadedSize / currentFile.size).toFixed(2))
      // debugger
      currentFile.uploadProgress = progress
      this.$set(this.uploadFileList, this.currentFileIndex, currentFile)
    },

    /**
     * 合并文件
     *   uploadId: self.uploadIdInfo.uploadId,
     *   fileName: currentFile.name,
     *   //fileMd5: fileMd5,
     *   bucketName: 'bucket'
     */
    mergeFile(fileParam) {
      const self = this;
      return new Promise((resolve, reject) => {
        mergeUpload(fileParam).then(response => {
          console.log(response);
          let data = response
          console.log("@@@", data)
          if (!data) {
            data.msg = this.FileStatus.error
            resolve(data)
          } else {
            data.msg = this.FileStatus.success
            resolve(data)
          }
        })
        // .catch(error => {
        //     self.$message.error('合并文件失败：' + error)
        //     file.status = FileStatus.error
        //     reject()
        // })
      })
    },

    // Minio分片上传 ----------------------------------------------------------
    handleClose() {
      this.open = false;
      this.reset();
      this.getList()
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        musicId: null,
        title: null,
        artist: null,
        duration: null,
        filePath: null,
        fileUrl: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
      };
      this.resetForm("form");
      this.uploadFileList = [];
      this.progressFlag = false;
      this.progressPercent = 0
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.musicId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加音乐";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const musicId = row.musicId || this.ids
      getMusic(musicId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改音乐";
      });
    },
    /** 提交按钮 */
    submitForm() {
      if (!this.fileList || this.fileList.length === 0) {
        this.$message.warning('请选择要上传的文件');
        return;
      }
      // 创建FormData对象
      let formData = new FormData();
      this.fileList.forEach(file => {
        formData.append("files", file.raw);
        formData.append("filesName", file.name);
      });

      // 进度条开始
      this.progressFlag = true;
      axios({
        url: '/music',
        method: 'post',
        data: formData,
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        onUploadProgress: progressEvent => {
          this.progressPercent = ((progressEvent.loaded / progressEvent.total) * 100) | 0;
        }
      }).then(response => {
        this.$modal.msgSuccess("添加成功");
        this.open = false;
        this.getList();
      })
    },
    // 文件相对路径
    modifiedPath(originalPath) {
      const baseUrl = window.location.protocol + "//" + window.location.hostname;
      const urlObj = new URL(originalPath);
      return baseUrl + `:${urlObj.port}${urlObj.pathname}`
    },
    /** 查询音乐列表 */
    getList() {
      this.loading = true;
      listMusic(this.queryParams).then(response => {
        this.musicList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const musicIds = row.musicId || this.ids;
      this.$modal.confirm('是否确认删除音乐编号为"' + musicIds + '"的数据项？').then(function () {
        return delMusic(musicIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/music/export', {
        ...this.queryParams
      }, `music_${new Date().getTime()}.xlsx`)
    },
  }
};
</script>
<style rel="stylesheet/scss" lang="scss">
.upload-demo {
  text-align: center;
}

.container {
  width: 750px;
  margin: 0 auto;
}

.file-list-wrapper {
  margin-top: 20px;
}

h2 {
  text-align: center;
}

.file-info-item {

  margin: 0 10px;
}

.upload-file-item {
  display: flex;
}

.file-progress {
  display: flex;
  align-items: center;
}

.file-progress-value {
  width: 150px;
}

.file-name {
  width: 250px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  width: 100px;
  margin-left: 60px;
}

.uploader-example {
  width: 880px;
  padding: 15px;
  margin: 40px auto 0;
  font-size: 12px;
  box-shadow: 0 0 10px rgba(0, 0, 0, .4);
}

.uploader-example .uploader-btn {
  margin-right: 4px;
}

.uploader-example .uploader-list {
  max-height: 440px;
  overflow: auto;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
