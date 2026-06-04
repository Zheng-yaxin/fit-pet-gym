package com.gym.common.enums;

/**
 * 用户类型枚举
 */
public enum UserType {
    /**
     * 系统管理员
     */
    SYS_USER("SYS_USER"),

    /**
     * 普通会员
     */
    MEMBER("MEMBER"),

    /**
     * 教练
     */
    COACH("COACH");

    private final String code;

    UserType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}