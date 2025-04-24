package org.imusic.netty.util;

import org.imusic.netty.domain.MsgInfo;
import org.imusic.netty.domain.TimeSlot;

public class MsgUtil {

    public static MsgInfo buildMsg(String channelId, String msgContent) {
        return new MsgInfo(channelId,msgContent);
    }

    public static TimeSlot buildTimeSlotMsg(String slotTime) {
        return new TimeSlot(slotTime);
    }
}
