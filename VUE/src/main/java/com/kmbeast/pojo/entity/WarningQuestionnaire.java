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
 * 预警问卷实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "warning_questionnaire")
public class WarningQuestionnaire {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer petId;
    private Integer questionType; // 1-定期关怀 2-随机抽查 3-预警触发 4-领养回访
    private String questions;     // JSON格式的问题
    private String answers;       // JSON格式的答案
    private Integer score;        // 问卷分数 0-100
    private Integer warningLevel; // 预警级别 0-3
    private Integer status;       // 1-待回答 2-已提交 3-已处理

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;
}