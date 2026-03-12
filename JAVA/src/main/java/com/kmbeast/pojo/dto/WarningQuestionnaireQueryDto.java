package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class WarningQuestionnaireQueryDto {

    private Integer userId;
    private Integer petId;
    private Integer status;       // 1-待回答 2-已提交
    private Integer questionType;
    private Integer warningLevel;

    private String username;      // 用户名模糊查询
    private String petName;       // 宠物名模糊查询

    // 分页参数
    private Integer current = 1;
    private Integer size = 10;
    private Integer offset;
}