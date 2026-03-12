package com.kmbeast.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvailableUserVO {

    private Integer userId;
    private String username;
    private String realName;
    private String phone;
    private Integer petId;
    private String petName;
    private String petAvatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime adoptTime;

    private Integer warningLevel;      // 0-安全 1-低风险 2-中风险 3-高风险
    private Integer lastScore;
    private LocalDateTime lastAnswerTime;

    public String getWarningLevelText() {
        if (warningLevel == null) return "未知";
        switch (warningLevel) {
            case 0: return "安全";
            case 1: return "低风险";
            case 2: return "中风险";
            case 3: return "高风险";
            default: return "未知";
        }
    }
}