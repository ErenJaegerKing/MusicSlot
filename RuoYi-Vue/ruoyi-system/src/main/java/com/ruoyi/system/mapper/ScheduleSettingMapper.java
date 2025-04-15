package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ScheduleSettingPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleSettingMapper {

    @Select("SELECT id, job_id as jobId, cron_expression as cronExpression, " +
            "job_result as jobResult, create_date as createDate, status, " +
            "create_by as createBy, creator, version " +
            "FROM schedule_setting")
    public List<ScheduleSettingPO> findAll();

    @Insert("INSERT INTO schedule_setting " +
            "(id, job_id, cron_expression, job_result, create_date, status, create_by, creator, version) " +
            "VALUES " +
            "(#{id}, #{jobId}, #{cronExpression}, #{jobResult}, #{createDate}, #{status}, #{createBy}, #{creator}, #{version})")
    public int insert(ScheduleSettingPO scheduleSettingPO);

    @Delete("DELETE FROM schedule_setting WHERE id = #{taskId}")
    public int deleteById(String taskId);

    @Select("SELECT id, job_id as jobId, cron_expression as cronExpression, " +
            "job_result as jobResult, create_date as createDate, status, " +
            "create_by as createBy, creator, version " +
            "FROM schedule_setting WHERE id = #{taskId}")
    public ScheduleSettingPO findById(String taskId);
}
