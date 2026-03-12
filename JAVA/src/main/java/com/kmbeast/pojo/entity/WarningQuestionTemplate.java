package com.kmbeast.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 问卷模板实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "warning_question_template")
public class WarningQuestionTemplate {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String templateName;
    private Integer templateType; // 1-定期关怀 2-随机抽查 3-预警触发 4-领养回访
    private String questions;     // JSON格式的问题
    private String frequency;     // ONCE/MONTH/WEEK
    private Integer isActive;     // 1-启用 0-禁用

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}