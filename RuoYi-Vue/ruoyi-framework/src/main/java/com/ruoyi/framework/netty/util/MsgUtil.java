package com.ruoyi.framework.netty.util;

import com.ruoyi.framework.netty.domain.MsgInfo;
import com.ruoyi.system.domain.TimeSlot;

public class MsgUtil {

    public static MsgInfo buildMsg(String channelId, String msgContent) {
        return new MsgInfo(channelId,msgContent);
    }

    public static TimeSlot buildTimeSlotMsg(String slotTime) {
        return new TimeSlot(slotTime);
    }
}
