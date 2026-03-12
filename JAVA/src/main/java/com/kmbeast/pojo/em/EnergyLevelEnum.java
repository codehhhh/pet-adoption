package com.kmbeast.pojo.em;

public enum EnergyLevelEnum {
    ACTIVE(1, "活跃"),
    NORMAL(2, "正常"),
    LAZY(3, "懒散");

    private final Integer code;
    private final String desc;

    EnergyLevelEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDesc(Integer code) {
        for (EnergyLevelEnum value : values()) {
            if (value.code.equals(code)) {
                return value.desc;
            }
        }
        return "未知";
    }
}