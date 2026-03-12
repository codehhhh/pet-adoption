package com.kmbeast.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 宠物健康档案实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetHealthRecord {
    private Long id;
    private Integer petId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;

    private BigDecimal weight;
    private BigDecimal height;
    private Integer bodyCondition; // 1:优秀 2:良好 3:一般 4:差
    private Integer appetite;      // 1:很好 2:正常 3:较差
    private Integer energyLevel;   // 1:活跃 2:正常 3:懒散
    private String notes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}