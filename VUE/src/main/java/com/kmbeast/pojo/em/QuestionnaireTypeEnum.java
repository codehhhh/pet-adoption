package com.kmbeast.pojo.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问卷类型枚举
 */
@Getter
@AllArgsConstructor
public enum QuestionnaireTypeEnum {

    REGULAR(1, "定期关怀"),
    RANDOM(2, "随机抽查"),
    WARNING_TRIGGER(3, "预警触发");

    private final Integer type;
    private final String name;
}