package com.ruoyi.common.enums;

public enum PlayMode {
    LOOP("1"), // 循环
    SHUFFLE("2");//乱序
    
    private final String value;
    
    PlayMode(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
