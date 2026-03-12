package com.kmbeast.pojo.vo;

import lombok.Data;

/**
 * 风险等级统计VO
 */
@Data
public class RiskLevelStatsVO {
    private Integer level;      // 风险等级
    private Long count;         // 数量
}