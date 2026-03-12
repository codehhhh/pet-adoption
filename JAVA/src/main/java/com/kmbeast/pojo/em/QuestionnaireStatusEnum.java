package com.kmbeast.pojo.em;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问卷状态枚举
 */
@Getter
@AllArgsConstructor
public enum QuestionnaireStatusEnum {

    PENDING(1, "待回答"),
    SUBMITTED(2, "已提交"),
    PROCESSED(3, "已处理");

    private final Integer status;
    private final String name;
}