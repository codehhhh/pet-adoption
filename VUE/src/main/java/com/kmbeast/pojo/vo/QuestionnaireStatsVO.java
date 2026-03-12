package com.kmbeast.pojo.vo;

import lombok.Data;

/**
 * 问卷统计VO
 */
@Data
public class QuestionnaireStatsVO {

    private Long totalQuestionnaires;    // 总问卷数
    private Long pendingCount;            // 待回答数量
    private Long completedCount;          // 已完成数量
    private Long riskCount;               // 风险数量
    private Double avgScore;              // 平均分
}