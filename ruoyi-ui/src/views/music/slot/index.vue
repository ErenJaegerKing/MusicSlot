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
          v-hasPermi="['system:slot:add']"
        >新增任务
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
          v-hasPermi="['system:slot:remove']"
        >删除任务
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-switch
          v-model="musicStatus"
          active-color="#13ce66"
          inactive-color="#ff4949"
          style="margin-top: 5px"
          @change="handleTimeSlotStatusChange"
        >
        </el-switch>
      </el-col>
      <el-button-group class="card-toggle-table" style="float: right; margin-right: 15px">
        <el-tooltip
          v-if="cardType"
          class="item"
          effect="dark"
          content="切换成表格"
          placement="top-start"
        >
          <el-button size="small" plain icon="el-icon-s-grid" @click="toggle"/>
        </el-tooltip>
        <el-tooltip
          v-else
          class="item"
          effect="dark"
          content="切换成卡片"
          placement="top-start"
        >
          <el-button size="small" plain icon="el-icon-bank-card" @click="toggle"
          />
        </el-tooltip>
      </el-button-group>
    </el-row>
    <!--卡片风格-->
    <el-row :gutter="24" v-if="cardType">
      <el-col v-for="(item,index) in slotList" :key="item.slotId" :span="6" :xs="24" :sm="12" :md="8" :lg="6">
        <div class="card-container" @click="handleUpdate(item)">
          <el-card class="modern-card" shadow="hover">
            <div class="card-header">
              <h3 class="card-title">{{ item.slotName }}</h3>
              <div class="card-actions">
                <i class="el-icon-edit" @click.stop="handleUpdate(item)"/>
              </div>
            </div>
            <!-- 时间部分 -->
            <div class="time-section">
              <div class="time-range">
                <div class="time-item">
                  <i class="el-icon-time time-icon"/>
                  <span class="time-label">开始</span>
                  <span class="time-value">{{ item.startTime }}</span>
                </div>
                <div class="time-separator">
                  <span class="separator-line"></span>
                  <span class="separator-text">至</span>
                  <span class="separator-line"></span>
                </div>
                <div class="time-item">
                  <i class="el-icon-timer time-icon"/>
                  <span class="time-label">结束</span>
                  <span class="time-value">{{ item.endTime }}</span>
                </div>
              </div>
            </div>
            <!-- 播放模式部分 -->
            <div class="info-section">
              <div class="section-header">
                <i class="el-icon-video-play"></i>
                <span>播放模式</span>
                <span class="play-mode-text">{{ getModeName(item.playMode) }}</span>
              </div>
            </div>
            <!-- 星期部分 -->
            <div class="info-section">
              <div class="section-header">
                <i class="el-icon-date"></i>
                <span>适用星期</span>
              </div>
              <div class="tags-container">
                <el-tag
                  v-for="mode in item.weekdays.split(',')"
                  :key="mode"
                  class="weekday-tag"
                  effect="plain"
                  size="small"
                >
                  {{ getWeekdaysName(mode) }}
                </el-tag>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
      <!-- 新增卡片 -->
      <el-col :span="6" :xs="24" :sm="12" :md="8" :lg="6">
        <div class="card-container">
          <div class="add-card" @click="handleAdd">
            <el-card shadow="hover" class="add-card-inner">
              <div class="add-card-content">
                <i class="el-icon-circle-plus-outline add-icon"/>
                <span class="add-text">添加新时段</span>
              </div>
            </el-card>
          </div>
        </div>
      </el-col>
    </el-row>
    <!--    列表风格-->
    <el-table
      v-else
      v-loading="loading"
      :data="slotList"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="时间段ID" align="center" prop="slotId"/>
      <el-table-column label="音乐任务" align="center" prop="slotName"/>
      <el-table-column label="开始时间" align="center" prop="startTime" width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.startTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.endTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="播放方式" align="center" prop="playMode">
        <template slot-scope="scope">
          <div v-if="scope.row.playMode">
            <el-tag
              v-for="mode in scope.row.playMode"
              :key="mode"
              style="margin-right: 5px"
            >
              {{ getModeName(mode) }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="播放周期" align="center" prop="weekdays" min-width="150px">
        <template slot-scope="scope">
          <div v-if="scope.row.weekdays">
            <el-tag
              v-for="mode in scope.row.weekdays.split(',')"
              :key="mode"
              style="margin-right: 5px"
            >
              {{ getWeekdaysName(mode) }}
            </el-tag>
          </div>
        </template>
      </el-table-column>
      <!--      <el-table-column label="状态" align="center" prop="status" />-->
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:slot:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:slot:remove']"
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
    <!-- 添加或修改时间段对话框 -->
    <el-dialog :title="title" :visible.sync="open" :close-on-click-modal="false" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" size="medium" label-width="100px">
        <el-row :gutter="24">
          <el-col :span="24">
            <el-form-item label="任务名称" prop="slotName">
              <el-input v-model="form.slotName" placeholder="请输入任务名称" clearable :style="{width: '100%'}">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker
                v-model="form.startTime"
                placeholder="请选择开始时间"
                value-format="HH:mm:ss"
                style="width: 100%"
              >
              </el-time-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker
                arrow-control
                v-model="form.endTime"
                placeholder="请选择结束时间"
                value-format="HH:mm:ss"
                style="width: 100%"
              >
              </el-time-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="播放模式" prop="playMode">
          <el-radio-group v-model="form.playMode" size="medium">
            <el-radio v-for="(item, index) in playModeOptions" :key="index" :label="item.value"
                      :disabled="item.disabled"
            >{{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label-width="110px" label="每周播放日期" prop="weekdaysArray">
          <el-checkbox-group v-model="form.weekdaysArray" size="medium">
            <el-checkbox v-for="(item, index) in weekdaysOptions" :key="index" :label="item.value"
                         :disabled="item.disabled"
            >{{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="音乐曲目" prop="musicIds">
          <el-select style="width: 660px" v-model="form.musicIds" multiple filterable
                     placeholder="请选择要播放的音乐"
          >
            <el-option
              v-for="item in musicList"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :loading="btnLoading">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSlot, getSlot, delSlot, addSlot, updateSlot } from '@/api/music/slot'
import { getTimeSlotEnabled, toggleTimeSlotEnabled } from '@/api/system/config'
import { listMusic } from '@/api/music/music'

export default {
  name: 'TimeSlot',
  data() {
    return {
      // 遮罩层
      loading: true,
      // 按钮加载
      btnLoading: false,
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
      // 时间段表格数据
      slotList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startTime: null,
        endTime: null,
        slotName: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        slotName: [{
          required: true,
          message: '请输入任务名称',
          trigger: 'blur'
        }],
        startTime: [
          { required: true, message: '开始时间不能为空', trigger: 'blur' }
        ],
        endTime: [
          { required: true, message: '结束时间不能为空', trigger: 'blur' }
        ],
        playMode: [{
          required: true,
          message: '请至少选择一个播放模式',
          trigger: 'blur'
        }],
        weekdaysArray: [{
          required: true,
          message: '请至少选择一个星期',
          trigger: 'blur'
        }],
        musicIds: [{
          required: true,
          message: '请至少选择一首音乐',
          trigger: 'blur'
        }]
      },
      playModeOptions: [{
        'label': '顺序播放',
        'value': '1'
      }, {
        'label': '乱序播放',
        'value': '2'
      }],
      weekdaysOptions: [{
        'label': '星期一',
        'value': 1
      }, {
        'label': '星期二',
        'value': 2
      }, {
        'label': '星期三',
        'value': 3
      }, {
        'label': '星期四',
        'value': 4
      }, {
        'label': '星期五',
        'value': 5
      }, {
        'label': '星期六',
        'value': 6
      }, {
        'label': '星期天',
        'value': 7
      }],
      // 定时任务总开关
      musicStatus: true,
      // 音乐表格数据
      musicList: [],
      // 默认为卡片风格，为false时切换成列表风格
      cardType: true
    }
  },
  created() {
    this.getList()
    this.getTimeSlotStatus()
  },
  methods: {
    // 点击切换风格
    toggle() {
      this.cardType = !this.cardType
    },
    getModeName(mode) {
      const modeMap = {
        '1': '顺序',
        '2': '乱序'
      }
      return modeMap[mode] || mode
    },
    getWeekdaysName(weekdays) {
      const weekdaysMap = {
        '1': '星期一',
        '2': '星期二',
        '3': '星期三',
        '4': '星期四',
        '5': '星期五',
        '6': '星期六',
        '7': '星期天'
      }
      return weekdaysMap[weekdays] || weekdays
    },
    /** 查询时间段状态 */
    getTimeSlotStatus() {
      getTimeSlotEnabled().then(response => {
        this.musicStatus = !!response
      })
    },
    /** 修改时间段状态 */
    handleTimeSlotStatusChange() {
      toggleTimeSlotEnabled()
    },
    /** 查询时间段列表 */
    getList() {
      this.loading = true
      listSlot(this.queryParams).then(response => {
        this.slotList = response.rows
        this.total = response.total
        this.loading = false
        console.log('slotList', response.rows)
      })
    },
    /** 查询音乐列表 */
    getMusicList() {
      listMusic(this.queryParams).then(response => {
        this.musicList = response.rows.map(item => ({
          // label: `${item.title} - ${item.artist}`,
          label: `${item.title}`,
          value: item.musicId
        }))
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        slotId: null,
        slotName: null,
        startTime: null,
        endTime: null,
        weekdaysArray: [],
        // musicIdsArray: [],
        playMode: null,
        weekdays: null,
        // musicIds: null,
        musicIds: [],
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.slotId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加时间段'
      this.getMusicList()
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const slotId = row.slotId || this.ids
      getSlot(slotId).then(response => {
        this.form = response.data
        this.$set(this.form, 'weekdaysArray', this.form.weekdays.split(',').map(Number))
        this.form.musicIds = this.form.musicList.map(music => music.musicId)
        this.open = true
        this.title = '修改时间段'
        this.getMusicList()
      })
    },

    /** 提交按钮 */
    submitForm() {
      // 创建新数组排序（不影响原数组）
      this.form.weekdays = [...this.form.weekdaysArray].sort().join(',')
      if (this.form.startTime > this.form.endTime) {
        this.$message.warning('开始时间不能大于结束时间')
      } else {
        this.$refs['form'].validate(valid => {
          if (valid) {
            // 按钮触发要在校验之后
            this.btnLoading = true
            if (this.form.slotId != null) {
              updateSlot(this.form).then(response => {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getList()
              }).finally(_ => {
                this.btnLoading = false
              })
            } else {
              addSlot(this.form).then(response => {
                this.$modal.msgSuccess('新增成功')
                this.open = false
                this.getList()
              }).finally(_ => {
                this.btnLoading = false
              })
            }
          }
        })
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const slotIds = row.slotId || this.ids
      this.$modal.confirm('是否确认删除时间段编号为"' + slotIds + '"的数据项？').then(function() {
        return delSlot(slotIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/slot/export', {
        ...this.queryParams
      }, `slot_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
<style scoped>
.card-container {
  margin-bottom: 24px;
  height: 100%;
  cursor: pointer;
  .modern-card, .add-card-inner {
    height: 100%;
    min-height: 358px; /* 设置统一的最小高度 */
    display: flex;
    flex-direction: column;
  }
}

.modern-card {
  border-radius: 8px;
  border: 1px solid #dcdfe6; /* 深色边框 */
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    border-color: #c0c4cc; /* 悬停时边框颜色加深 */
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;

    .card-title {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
      color: #333;
    }

    .card-actions {
      color: #909399;
      cursor: pointer;
      transition: color 0.2s;

      &:hover {
        color: #409EFF;
      }
    }
  }

  .time-section {
    padding: 16px 20px;
    background: #f9fafc;
    margin: 0 -20px;

    .time-range {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .time-item {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .time-icon {
      color: #409EFF;
      font-size: 16px;
    }

    .time-label {
      font-size: 12px;
      color: #909399;
      min-width: 30px;
    }

    .time-value {
      font-weight: 500;
      color: #333;
    }

    .time-separator {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      margin: 4px 0;

      .separator-line {
        flex: 1;
        height: 1px;
        background: #e0e0e0;
      }

      .separator-text {
        font-size: 12px;
        color: #909399;
        padding: 0 8px;
      }
    }
  }

  .info-section {
    padding: 12px 20px;
    border-bottom: 1px solid #f5f5f5;
    flex-grow: 1;

    &:last-child {
      border-bottom: none;
    }

    .section-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 10px;

      i {
        color: #909399;
        font-size: 14px;
      }

      span {
        font-size: 13px;
        color: #666;
        font-weight: 500;
      }
    }

    .tags-container {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }

    .weekday-tag {
      background: #f0f9ff;
      color: #409EFF;
      border-color: #d9ecff;
    }

    .mode-tag {
      background: #f4f4f5;
      color: #909399;
    }
  }
}

.info-section {
  padding: 12px 20px;
  border-bottom: 1px solid #f5f5f5;
  flex-grow: 1;

  &:last-child {
    border-bottom: none;
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 10px;

    i {
      color: #909399;
      font-size: 14px;
    }

    .section-title {
      font-size: 13px;
      color: #666;
      font-weight: 500;
      min-width: 60px;
    }

    .play-mode-text {
      font-size: 13px;
      font-weight: 600;
      color: #409EFF;
      background: #f0f9ff;
      padding: 4px 8px;
      border-radius: 4px;
      margin-left: auto;
    }
  }
}

.info-section {
  padding: 12px 20px;
  border-bottom: 1px solid #f5f5f5;
  flex-grow: 1;

  &:last-child {
    border-bottom: none;
  }

  .section-header {
    display: flex;
    align-items: center;
    gap: 10px;

    i {
      color: #909399;
      font-size: 14px;
    }

    .section-title {
      font-size: 13px;
      color: #666;
      font-weight: 500;
      min-width: 60px;
    }

    .play-mode-text {
      font-size: 13px;
      font-weight: 600;
      color: #409EFF;
      background: #f0f9ff;
      padding: 4px 8px;
      border-radius: 4px;
      margin-left: auto;
    }
  }
}

.add-card {
  height: 100%;

  .add-card-inner {
    border: 1px dashed #dcdfe6; /* 与普通卡片一致的边框颜色 */
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;

    &:hover {
      border-color: #409EFF;
      background: #f5f9ff;
      border-style: solid; /* 悬停时变为实线 */
    }
  }

  .add-card-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;

    .add-icon {
      color: #409EFF;
      font-size: 48px;
      margin-bottom: 12px;
    }

    .add-text {
      color: #409EFF;
      font-size: 14px;
      font-weight: 500;
    }
  }
}

/* 确保所有卡片列高度一致 */
.el-col {
  display: flex;
  flex-direction: column;
}

@media (max-width: 768px) {
  .card-container {
    margin-bottom: 16px;
  }
}
</style>
