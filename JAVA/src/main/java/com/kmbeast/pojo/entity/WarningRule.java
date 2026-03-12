package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 预警规则实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "warning_rule")
public class WarningRule {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String ruleName;
    private String conditionDesc;
    private Integer minScore;
    private Integer maxScore;
    private Integer warningLevel; // 0-安全 1-低 2-中 3-高
    private String action;
    private Boolean isActive;
}