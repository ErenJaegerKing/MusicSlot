package com.ruoyi.system.config;

import com.ruoyi.common.constant.TimeSlotConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.domain.ScheduleSettingPO;
import com.ruoyi.system.mapper.ScheduleSettingMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ScheduleExecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author tx
 */
@Component
public class ScheduleInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ScheduleInitRunner.class);

    private final ScheduleSettingMapper scheduleSettingMapper;

    private final ScheduleExecService scheduleExecService;

    private final CronTaskRegistrar scheduledTaskRegistrar;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysConfigService configService;


    public ScheduleInitRunner(ScheduleSettingMapper scheduleSettingMapper, ScheduleExecService scheduleExecService, CronTaskRegistrar scheduledTaskRegistrar) {
        this.scheduleSettingMapper = scheduleSettingMapper;
        this.scheduleExecService = scheduleExecService;
        this.scheduledTaskRegistrar = scheduledTaskRegistrar;
    }

    /**
     * 在项目启动的时候，将需要执行的定时任务加载到缓存中
     */
    @Override
    public void run(String... args) throws Exception {
//        String currentStatus = configService.selectTimeSlotEnabledByKey(TimeSlotConstants.TIMESLOT_QUERY_ENABLED);
        String currentStatus = redisCache.getCacheObject(TimeSlotConstants.TIMESLOT_QUERY_ENABLED);
        if (currentStatus.equals(TimeSlotConstants.ENABLED)) {
            List<ScheduleSettingPO> scheduleSettingList = scheduleSettingMapper.findAll();
            log.info("启动定时任务----");
            for (ScheduleSettingPO scheduleSetting : scheduleSettingList) {
                log.info("启动定时任务taskId:{}", scheduleSetting.getJobId());
                scheduledTaskRegistrar.addCronTask(
                        () -> scheduleExecService.scheduleExec(scheduleSetting.getJobId()),
                        scheduleSetting.getCronExpression(),
                        scheduleSetting.getJobId());
            }
        }
    }
}
