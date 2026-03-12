package com.kmbeast.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarningQuestionnaireVO {

    private Integer id;
    private Integer userId;
    private Integer petId;
    private Integer questionType;
    private String questions;
    private String answers;
    private Integer score;
    private Integer warningLevel;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submitTime;

    // 关联信息
    private String userName;
    private String phone;
    private String petName;
    private String petAvatar;

    // 解析后的问题列表
    private List<QuestionItem> questionList;

    @Data
    public static class QuestionItem {
        private String id;
        private String question;
        private List<String> options;
        private String type;
    }
}