package com.kmbeast.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QuestionnaireSubmitDto {

    @NotNull(message = "问卷ID不能为空")
    private Integer questionnaireId;

    private String answers; // JSON格式的答案
}