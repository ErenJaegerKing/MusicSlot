package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.TimeSlotMusic;
import org.apache.ibatis.annotations.Param;


import java.util.List;

public interface TimeSlotMusicMapper extends BaseMapper<TimeSlotMusic> {

    int batchInsert(@Param("list") List<TimeSlotMusic> list);

}
