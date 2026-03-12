package com.kmbeast.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PetHealthRecordVO {
    private Long id;
    private Integer petId;
    private String petName;
    private String petCover;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private BigDecimal weight;
    private BigDecimal height;
    private Integer bodyCondition;
    private String bodyConditionText;
    private Integer appetite;
    private String appetiteText;
    private Integer energyLevel;
    private String energyLevelText;
    private String notes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private String ownerName;   // 领养人姓名
    private String ownerAvatar; // 领养人头像
}