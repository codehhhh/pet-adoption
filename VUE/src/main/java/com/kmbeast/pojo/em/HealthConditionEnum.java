package com.kmbeast.pojo.em;

public enum HealthConditionEnum {
    EXCELLENT(1, "优秀"),
    GOOD(2, "良好"),
    FAIR(3, "一般"),
    POOR(4, "差");

    private final Integer code;
    private final String desc;

    HealthConditionEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (HealthConditionEnum value : values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }
        return "未知";
    }
}