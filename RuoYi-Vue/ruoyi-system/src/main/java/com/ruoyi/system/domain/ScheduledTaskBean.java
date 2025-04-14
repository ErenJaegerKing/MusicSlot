package com.ruoyi.system.domain;

public class ScheduledTaskBean {

    /**
     * 主键 唯一
     */
    private Long id;
    
    /**
     * 任务key值 唯一
     */
    private String taskKey;
    /**
     * 任务描述
     */
    private String taskDesc;
    /**
     * 任务表达式
     */
    private String taskCron;

    /**
     * 程序初始化是否启动 1 是 0 否
     */
    private Integer initStartFlag;

    /**
     * 当前是否已启动
     */
    private boolean startFlag;

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskCron() {
        return taskCron;
    }

    public void setTaskCron(String taskCron) {
        this.taskCron = taskCron;
    }

    public Integer getInitStartFlag() {
        return initStartFlag;
    }

    public void setInitStartFlag(Integer initStartFlag) {
        this.initStartFlag = initStartFlag;
    }

    public boolean isStartFlag() {
        return startFlag;
    }

    public void setStartFlag(boolean startFlag) {
        this.startFlag = startFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}