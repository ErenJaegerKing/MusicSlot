package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ScheduledTaskBean;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 定时任务表 mapper
 * demo 项目未使用 xml方式，使用注解方式查询数据以便演示
 */
@Mapper
public interface ScheduledTaskMapper {

    /**
     * 根据key 获取 任务信息
     */
    @Select("select task_key as taskKey,task_desc as taskDesc,task_cron as taskCron,init_start_flag as initStartFlag  from scheduled_task where task_key = '${taskKey}' ")
    ScheduledTaskBean getByKey(@Param("taskKey") String taskKey);

    /**
     * 获取程序初始化需要自启的任务信息
     */
    @Select("select task_key as taskKey,task_desc as taskDesc,task_cron as taskCron,init_start_flag as initStartFlag from scheduled_task where init_start_flag=1 ")
    List<ScheduledTaskBean> getAllNeedStartTask();

    /**
     * 获取所有任务
     */
    @Select("select task_key as taskKey,task_desc as taskDesc,task_cron as taskCron,init_start_flag as initStartFlag  from scheduled_task ")
    List<ScheduledTaskBean> getAllTask();

    @Insert("INSERT INTO scheduled_task (task_key, task_desc, task_cron, init_start_flag, start_flag) " +
            "VALUES (#{taskKey}, #{taskDesc}, #{taskCron}, #{initStartFlag}, #{startFlag})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertTaskBean(ScheduledTaskBean taskBean);

    @Update("UPDATE scheduled_task SET " +
            "task_key = #{taskKey}, " +
            "task_desc = #{taskDesc}, " +
            "task_cron = #{taskCron}, " +
            "init_start_flag = #{initStartFlag}, " +
            "start_flag = #{startFlag} " +
            "WHERE id = #{id}")
    int updateTaskBean(ScheduledTaskBean taskBean);

    @Select("SELECT id, task_key AS taskKey, task_desc AS taskDesc, " +
            "task_cron AS taskCron, init_start_flag AS initStartFlag, " +
            "start_flag AS startFlag FROM scheduled_task WHERE id = #{taskBeanId}")
    ScheduledTaskBean selectTaskBeanById(Long taskBeanId);

    @Delete("DELETE FROM scheduled_task WHERE id = #{taskBeanId}")
    int deleteTaskBeanById(Long taskBeanId);
}
