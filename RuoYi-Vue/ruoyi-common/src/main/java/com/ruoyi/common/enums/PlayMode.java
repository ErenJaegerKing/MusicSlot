package com.ruoyi.common.enums;

public enum PlayMode {
    SEQUENCE("1"), // 顺序
    SHUFFLE("2");//乱序
    
    private final String value;
    
    PlayMode(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
}
