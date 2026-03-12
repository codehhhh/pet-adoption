package com.kmbeast.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 高风险用户VO
 */
@Data
public class HighRiskUserVO {
    private Integer userId;
    private String userName;
    private Integer petId;
    private String petName;
    private Integer warningLevel;
    private String reason;
    private String suggestion;
    private LocalDateTime createTime;
    private Integer warningCount;      // 预警次数
}