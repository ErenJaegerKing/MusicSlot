<template>
  <div class="app-container">
    <!--    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">-->
    <!--      <el-form-item label="歌曲名称" prop="title">-->
    <!--        <el-input-->
    <!--          v-model="queryParams.title"-->
    <!--          placeholder="请输入歌曲名称"-->
    <!--          clearable-->
    <!--          @keyup.enter.native="handleQuery"-->
    <!--        />-->
    <!--      </el-form-item>-->
    <!--      <el-form-item label="歌手" prop="artist">-->
    <!--        <el-input-->
    <!--          v-model="queryParams.artist"-->
    <!--          placeholder="请输入歌手"-->
    <!--          clearable-->
    <!--          @keyup.enter.native="handleQuery"-->
    <!--        />-->
    <!--      </el-form-item>-->
    <!--      <el-form-item label="时长(秒)" prop="duration">-->
    <!--        <el-input-->
    <!--          v-model="queryParams.duration"-->
    <!--          placeholder="请输入时长(秒)"-->
    <!--          clearable-->
    <!--          @keyup.enter.native="handleQuery"-->
    <!--        />-->
    <!--      </el-form-item>-->
    <!--      <el-form-item label="文件路径" prop="filePath">-->
    <!--        <el-input-->
    <!--          v-model="queryParams.filePath"-->
    <!--          placeholder="请输入文件路径"-->
    <!--          clearable-->
    <!--          @keyup.enter.native="handleQuery"-->
    <!--        />-->
    <!--      </el-form-item>-->
    <!--      <el-form-item>-->
    <!--        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
    <!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
    <!--      </el-form-item>-->
    <!--    </el-form>-->

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
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:music:edit']"
        >修改曲目
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
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="warning"-->
      <!--          plain-->
      <!--          icon="el-icon-download"-->
      <!--          size="mini"-->
      <!--          @click="handleExport"-->
      <!--          v-hasPermi="['system:music:export']"-->
      <!--        >导出</el-button>-->
      <!--      </el-col>-->
      <!--      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
    </el-row>

    <el-table v-loading="loading" :data="musicList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="音乐ID" align="center" prop="musicId"/>
      <el-table-column label="歌曲名称" align="center" prop="title"/>
      <el-table-column label="歌手" align="center" prop="artist"/>
      <!--      <el-table-column label="时长(秒)" align="center" prop="duration"/>-->
      <el-table-column label="路径" align="center" prop="fileUrl"/>
      <el-table-column label="状态" align="center" prop="status"/>
      <!--      <el-table-column label="备注" align="center" prop="remark"/>-->
      <el-table-column label="播放" align="center">
        <template #default="scope">
          <!-- 自定义内容：注入音乐播放器 -->
          <div>
            <audio controls :src="scope.row.filePath" style="width: 100%; max-width: 200px;">
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:music:edit']"
          >修改
          </el-button>
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
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-upload
        class="upload-demo"
        drag
        action="#"
        :auto-upload="false"
        :on-change="handleFileChange"
        :file-list="fileList"
        accept=".mp3,.wav,.ogg,.flac,.aac"
        :limit="1"
        :on-exceed="handleExceed">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将音乐文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">支持MP3、WAV、OGG、FLAC、AAC格式，大小不超过50MB</div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listMusic, getMusic, delMusic, addMusic, updateMusic} from "@/api/music/music";

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
      rules: {
        // title: [
        //   {required: true, message: "歌曲名称不能为空", trigger: "blur"}
        // ],
        // filePath: [
        //   {required: true, message: "文件路径不能为空", trigger: "blur"}
        // ],
      },
      fileList: [],
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询音乐列表 */
    getList() {
      this.loading = true;
      listMusic(this.queryParams).then(response => {
        this.musicList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
        file: null,
        album: '',
      };
      this.resetForm("form");
      this.fileList = [];
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
    parseMusicFilename(filename) {
      // 移除文件扩展名
      const nameWithoutExt = filename.replace(/\.[^/.]+$/, "");

      // 使用"-"分割字符串
      const parts = nameWithoutExt.split("-");

      // 如果分割后有两部分，则第一部分是歌曲名，第二部分是作者
      if (parts.length === 2) {
        return {
          title: parts[0].trim(),
          artist: parts[1].trim()
        };
      }

      // 如果不符合格式，返回默认值或原始文件名
      return {
        title: nameWithoutExt,
        artist: ""
      };
    },
    // 对变更数据进行保存
    handleFileChange(file, fileList) {
      if (fileList.length > 1) {
        fileList.splice(0, 1);
      }
      this.form.file = file.raw;
    },
    // 校验上传文件数量
    handleExceed(files, fileList) {
      this.$message.warning(`只能上传一个文件，请先删除当前文件`);
    },
    /** 提交按钮 */
    submitForm() {
      if (!this.form.file) {
        this.$message.error('请选择音乐文件');
        return;
      }
      const musicInfo = this.parseMusicFilename(this.form.file.name)
      // 创建FormData对象
      const formData = new FormData();
      formData.append('file', this.form.file);
      if (this.form.musicId != null) {
        this.form.title = musicInfo.title;
        this.form.artist = musicInfo.artist;
        formData.append('music', this.form)
        updateMusic(this.form).then(response => {
          this.$modal.msgSuccess(" 修改成功");
          this.open = false;
          this.getList();
        })
      } else {
        formData.append('title', musicInfo.title);
        formData.append('artist', musicInfo.artist);
        addMusic(formData).then(response => {
          this.$modal.msgSuccess("添加成功");
          this.open = false;
          this.getList();
        })
      }
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
    }
  }
};
</script>
<style rel="stylesheet/scss" lang="scss">
.upload-demo {
  text-align: center;
}
</style>
