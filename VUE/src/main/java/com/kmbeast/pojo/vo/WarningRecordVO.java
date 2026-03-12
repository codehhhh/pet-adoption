package com.kmbeast.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WarningRecordVO {

    private Integer id;
    private Integer userId;
    private Integer petId;
    private Integer questionnaireId;
    private Integer warningLevel;
    private String reason;
    private String suggestion;
    private Integer status;
    private Integer processedBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    // 关联信息
    private String userName;
    private String realName;
    private String phone;
    private String petName;

    public String getWarningLevelText() {
        if (warningLevel == null) return "未知";
        switch (warningLevel) {
            case 1: return "低风险";
            case 2: return "中风险";
            case 3: return "高风险";
            default: return "未知";
        }
    }

    public String getStatusText() {
        if (status == null) return "未知";
        return status == 1 ? "待处理" : "已处理";
    }
}