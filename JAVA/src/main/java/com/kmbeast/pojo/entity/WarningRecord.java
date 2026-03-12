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
 * 预警记录实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "warning_record")
public class WarningRecord {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer userId;
    private Integer petId;
    private Integer questionnaireId;
    private Integer warningLevel; // 1-低 2-中 3-高
    private String reason;
    private String suggestion;
    private Integer status;       // 1-待处理 2-已处理
    private Integer processedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}