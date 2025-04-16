
<template>
  <div class="app-container">
    <!--    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">-->
    <!--      <el-form-item label="开始时间" prop="startTime">-->
    <!--        <el-date-picker clearable-->
    <!--                        v-model="queryParams.startTime"-->
    <!--                        type="date"-->
    <!--                        value-format="yyyy-MM-dd"-->
    <!--                        placeholder="请选择开始时间">-->
    <!--        </el-date-picker>-->
    <!--      </el-form-item>-->
    <!--      <el-form-item label="结束时间" prop="endTime">-->
    <!--        <el-date-picker clearable-->
    <!--                        v-model="queryParams.endTime"-->
    <!--                        type="date"-->
    <!--                        value-format="yyyy-MM-dd"-->
    <!--                        placeholder="请选择结束时间">-->
    <!--        </el-date-picker>-->
    <!--      </el-form-item>-->
    <!--      <el-form-item label="时间段名称" prop="slotName">-->
    <!--        <el-input-->
    <!--          v-model="queryParams.slotName"-->
    <!--          placeholder="请输入时间段名称"-->
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
          v-hasPermi="['system:slot:add']"
        >新增任务
        </el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--          type="success"-->
<!--          plain-->
<!--          icon="el-icon-edit"-->
<!--          size="mini"-->
<!--          :disabled="single"-->
<!--          @click="handleUpdate"-->
<!--          v-hasPermi="['system:slot:edit']"-->
<!--        >修改任务-->
<!--        </el-button>-->
<!--      </el-col>-->
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
      <!--      <el-col :span="1.5">-->
      <!--        <el-button-->
      <!--          type="warning"-->
      <!--          plain-->
      <!--          icon="el-icon-download"-->
      <!--          size="mini"-->
      <!--          @click="handleExport"-->
      <!--          v-hasPermi="['system:slot:export']"-->
      <!--        >导出</el-button>-->
      <!--      </el-col>-->
      <!--      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>-->
    </el-row>

    <el-table v-loading="loading" :data="slotList" @selection-change="handleSelectionChange">
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
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
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
                         :disabled="item.disabled">{{ item.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label-width="110px" label="每周播放日期" prop="weekdaysArray">
          <el-checkbox-group v-model="form.weekdaysArray" size="medium">
            <el-checkbox v-for="(item, index) in weekdaysOptions" :key="index" :label="item.value"
                         :disabled="item.disabled">{{ item.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="音乐曲目" prop="musicIdsArray">
          <el-select style="width: 660px" v-model="form.musicIdsArray" multiple filterable
                     placeholder="请选择要播放的音乐">
            <el-option
              v-for="item in musicList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {listSlot, getSlot, delSlot, addSlot, updateSlot} from "@/api/music/slot";
import {getTimeSlotEnabled, toggleTimeSlotEnabled} from "@/api/system/config";
import {listMusic} from "@/api/music/music";

export default {
  name: "TimeSlot",
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
      // 时间段表格数据
      slotList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startTime: null,
        endTime: null,
        slotName: null,
        status: null,
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
          {required: true, message: "开始时间不能为空", trigger: "blur"}
        ],
        endTime: [
          {required: true, message: "结束时间不能为空", trigger: "blur"}
        ],
        playMode: [{
          required: true,
          message: '请至少选择一个播放模式',
          trigger: 'blur'
        }],
        weekdaysArray: [{
          required: true,
          message: '请至少选择一个播放模式',
          trigger: 'blur'
        }],
        musicIdsArray: [{
          required: true,
          message: '请至少选择一个星期',
          trigger: 'blur'
        }],
      },
      playModeOptions: [{
        "label": "顺序播放",
        "value": "1"
      }, {
        "label": "乱序播放",
        "value": "2"
      }],
      weekdaysOptions: [{
        "label": "星期一",
        "value": 1
      }, {
        "label": "星期二",
        "value": 2
      }, {
        "label": "星期三",
        "value": 3
      }, {
        "label": "星期四",
        "value": 4
      }, {
        "label": "星期五",
        "value": 5
      }, {
        "label": "星期六",
        "value": 6
      }, {
        "label": "星期天",
        "value": 7
      }],
      // 定时任务总开关
      musicStatus: true,
      // 音乐表格数据
      musicList: [],
    };
  },
  created() {
    this.getList();
    this.getTimeSlotStatus();
  },
  methods: {
    getModeName(mode) {
      const modeMap = {
        "1": "顺序",
        "2": "乱序",
      };
      return modeMap[mode] || mode
    },
    getWeekdaysName(weekdays) {
      const weekdaysMap = {
        "1": "星期一",
        "2": "星期二",
        "3": "星期三",
        "4": "星期四",
        "5": "星期五",
        "6": "星期六",
        "7": "星期天"
      }
      return weekdaysMap[weekdays] || weekdays
    },
    /** 查询时间段状态 */
    getTimeSlotStatus() {
      getTimeSlotEnabled().then(response => {
        this.musicStatus = !!response;
      })
    },
    /** 修改时间段状态 */
    handleTimeSlotStatusChange() {
      toggleTimeSlotEnabled();
    },
    /** 查询时间段列表 */
    getList() {
      this.loading = true;
      listSlot(this.queryParams).then(response => {
        this.slotList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询音乐列表 */
    getMusicList() {
      listMusic(this.queryParams).then(response => {
        this.musicList = response.rows.map(item => ({
          label: `${item.title} - ${item.artist}`,
          value: item.musicId,
        }));
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
        slotId: null,
        slotName: null,
        startTime: null,
        endTime: null,
        weekdaysArray: [],
        musicIdsArray: [],
        playMode: null,
        weekdays: null,
        musicIds: null,
        status: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null,
      };
      this.resetForm("form");
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
      this.ids = selection.map(item => item.slotId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加时间段";
      this.getMusicList();
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const slotId = row.slotId || this.ids
      getSlot(slotId).then(response => {
        this.form = response.data;
        this.$set(this.form,'weekdaysArray',this.form.weekdays.split(',').map(Number));
        this.$set(this.form,'musicIdsArray',this.form.musicIds.split(',').map(Number));
        this.open = true;
        this.title = "修改时间段";
        this.getMusicList();
      });
    },

    /** 提交按钮 */
    submitForm() {
      console.log(this.form)
      // 创建新数组排序（不影响原数组）
      this.form.weekdays = [...this.form.weekdaysArray].sort().join(",")
      this.form.musicIds = this.form.musicIdsArray.join(",");
      if (this.form.startTime > this.form.endTime) {
        this.$message.warning("开始时间不能大于结束时间")
      } else {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.slotId != null) {
              updateSlot(this.form).then(response => {
                this.$modal.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addSlot(this.form).then(response => {
                this.$modal.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const slotIds = row.slotId || this.ids;
      this.$modal.confirm('是否确认删除时间段编号为"' + slotIds + '"的数据项？').then(function () {
        return delSlot(slotIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/slot/export', {
        ...this.queryParams
      }, `slot_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
