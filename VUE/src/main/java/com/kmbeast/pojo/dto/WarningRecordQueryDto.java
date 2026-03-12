package com.kmbeast.pojo.dto;

import lombok.Data;

@Data
public class WarningRecordQueryDto {

    private Integer userId;
    private Integer petId;
    private Integer status;       // 1-待处理 2-已处理
    private Integer warningLevel; // 1-低 2-中 3-高

    // 分页参数
    private Integer current = 1;
    private Integer size = 10;
    private Integer offset;
}