package com.kmbeast.pojo.em;

public enum AppetiteEnum {
    VERY_GOOD(1, "很好"),
    NORMAL(2, "正常"),
    POOR(3, "较差");

    private final Integer code;
    private final String desc;

    AppetiteEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (AppetiteEnum value : values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }
        return "未知";
    }
}