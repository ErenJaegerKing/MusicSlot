package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.ScheduleSettingDTO;

/**
 * @author tanxiong
 * @date 2024/3/22 14:14
 */
public interface ScheduleExecService {

    /**
     * 定时执行的方法
     */
    void scheduleExec(String jobId);

    /**
     * 添加定时任务
     * @param scheduleSettingDTO
     */
    void add(ScheduleSettingDTO scheduleSettingDTO);

    /**
     * 暂停定时任务
     * @param scheduleSettingDTO
     */
    void stop(ScheduleSettingDTO scheduleSettingDTO);

    /**
     * 暂停所有定时任务
     */
    void stopAll();


    /**
     * 刷新定时任务
     */
    void refresh();
}
