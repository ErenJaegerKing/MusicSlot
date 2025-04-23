package com.ruoyi.framework.netty.util;

import com.ruoyi.framework.netty.domain.MsgInfo;

public class MsgUtil {

    public static MsgInfo buildMsg(String channelId, String msgContent) {
        return new MsgInfo(channelId,msgContent);
    }

}
