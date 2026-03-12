package com.kmbeast.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class WarningStatisticsVO {

    // 问卷统计
    private Long totalQuestionnaires;
    private Long pendingCount;
    private Long completedCount;
    private Long riskCount;
    private Double avgScore;

    // 预警统计
    private Long totalWarnings;
    private Long pendingWarnings;

    // 风险级别统计
    private Long lowRiskCount;
    private Long mediumRiskCount;
    private Long highRiskCount;

    // 高风险用户列表
    private List<HighRiskUserVO> highRiskUsers;
}