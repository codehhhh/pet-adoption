package com.kmbeast.pojo.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 预警级别枚举
 */
@Getter
@AllArgsConstructor
public enum WarningLevelEnum {

    SAFE(0, "安全"),
    LOW(1, "低风险"),
    MEDIUM(2, "中风险"),
    HIGH(3, "高风险");

    private final Integer level;
    private final String name;
}