package com.ruoyi.system.config;

import com.ruoyi.system.domain.ScheduleSettingPO;
import com.ruoyi.system.mapper.ScheduleSettingMapper;
import com.ruoyi.system.service.ScheduleExecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author tx
 */

@Component
@Lazy
public class ScheduledTaskInit {

    private static final Logger log = LoggerFactory.getLogger(CronTaskRegistrar.class);
    
    private final ScheduleSettingMapper scheduleSettingMapper;

    private final CronTaskRegistrar scheduledTaskRegistrar;

    public ScheduledTaskInit(ScheduleSettingMapper scheduleSettingMapper, CronTaskRegistrar scheduledTaskRegistrar) {
        this.scheduleSettingMapper = scheduleSettingMapper;
        this.scheduledTaskRegistrar = scheduledTaskRegistrar;
    }
    
    public void refresh(ScheduleExecService scheduleExecService) {
        synchronized (ScheduledTaskInit.class) {
            List<ScheduleSettingPO> list = scheduleSettingMapper.findAll();
            //销毁任务
            scheduledTaskRegistrar.destroy();
            if (CollectionUtils.isEmpty(list)) return;

            for (ScheduleSettingPO scheduleSetting : list) {
                scheduledTaskRegistrar.addCronTask(() -> {
                    scheduleExecService.scheduleExec(scheduleSetting.getJobId());
                }, scheduleSetting.getCronExpression(), scheduleSetting.getJobId());
            }
            log.info("//// 定时任务初始化，加载数量：{}", list.size());
        }
    }

}
