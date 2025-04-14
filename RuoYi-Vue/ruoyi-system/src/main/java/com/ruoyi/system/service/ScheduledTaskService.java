package com.ruoyi.system.service;

import com.ruoyi.system.domain.ScheduledTaskBean;

import java.util.List;

/**
 * 定时任务接口
 */
public interface ScheduledTaskService {

    /**
     * 所有任务列表
     */
    List<ScheduledTaskBean> taskList();

    /**
     * 根据任务key 启动任务
     */
    Boolean start(String taskKey);

    /**
     * 根据任务key 停止任务
     */
    Boolean stop(String taskKey);

    /**
     * 根据任务key 重启任务
     */
    Boolean restart(String taskKey);


    /**
     * 程序启动时初始化  ==> 启动所有正常状态的任务
     */
    void initAllTask(List<ScheduledTaskBean> scheduledTaskBeanList);

    /**
     * 新增
     */
    public int insertTaskBean(ScheduledTaskBean taskBean);

    /**
     * 修改
     */
    public int updateTaskBean(ScheduledTaskBean taskBean);


    /**
     * 查询
     */
    public ScheduledTaskBean selectTaskBeanById(Long taskBeanId);

    /**
     * 删除
     */
    public int deleteTaskBeanById(Long taskBeanId);
    

}
