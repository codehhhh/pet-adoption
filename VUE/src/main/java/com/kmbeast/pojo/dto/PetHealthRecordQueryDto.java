package com.kmbeast.pojo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PetHealthRecordQueryDto {
    private Integer petId;
    private String petName;      // 宠物名称
    private Integer bodyCondition;
    private Integer appetite;
    private Integer energyLevel;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer current;     // 当前页
    private Integer size;        // 每页大小
    private Integer userId;      // 新增：用户ID，用于普通用户筛选
}