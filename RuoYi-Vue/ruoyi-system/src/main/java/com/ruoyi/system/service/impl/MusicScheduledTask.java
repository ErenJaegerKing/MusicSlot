package com.ruoyi.system.service.impl;

import com.ruoyi.system.service.ScheduledTaskJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务 01
 */
public class MusicScheduledTask implements ScheduledTaskJob {
    /**
     * 日志
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MusicScheduledTask.class);

    @Override
    public void run() {
        LOGGER.info("ScheduledTask => 01  run  当前线程名称 {} ", Thread.currentThread().getName());
    }
    
}
