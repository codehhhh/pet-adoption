package com.kmbeast.service;

import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.ManualSendDto;
import com.kmbeast.pojo.dto.QuestionnaireSubmitDto;
import com.kmbeast.pojo.dto.WarningQuestionnaireQueryDto;
import com.kmbeast.pojo.dto.WarningRecordQueryDto;
import com.kmbeast.pojo.entity.WarningQuestionTemplate;
import com.kmbeast.pojo.vo.*;

import java.util.List;

/**
 * 预警服务接口
 */
public interface WarningService {

    /**
     * 获取用户待回答问卷
     */
    Result<List<WarningQuestionnaireVO>> getUserPendingQuestionnaires();

    /**
     * 提交问卷
     */
    Result<String> submitQuestionnaire(QuestionnaireSubmitDto dto);

    /**
     * 获取用户历史问卷
     */
    Result<List<WarningQuestionnaireVO>> getUserQuestionnaireHistory();


    /**
     * 获取预警记录列表
     */
    Result<List<WarningRecordVO>> getWarningRecords(WarningRecordQueryDto queryDto);

    /**
     * 处理预警记录
     */
    Result<String> processWarning(Integer warningId);

    /**
     * 获取预警统计数据
     */
    Result<WarningStatisticsVO> getWarningStatistics();

    /**
     * 管理员获取问卷列表
     */
    List<WarningQuestionnaireVO> getQuestionnaireListByAdmin(WarningQuestionnaireQueryDto queryDto);

    /**
     * 管理员统计问卷总数
     */
    Integer countQuestionnaireByAdmin(WarningQuestionnaireQueryDto queryDto);

    /**
     * 管理员获取统计概览
     */
    Result<QuestionnaireStatsVO> getAdminStatistics();

    /**
     * 获取可发放问卷的用户列表
     */
    List<AvailableUserVO> getAvailableUsers(Integer warningLevel, String username, String petName);

    /**
     * 手动发放问卷
     */
    void manualSendQuestionnaire(ManualSendDto dto);

    /**
     * 获取问卷模板列表
     */
    List<WarningQuestionTemplate> getTemplates(Integer templateType);


    /**
     * 发送定期关怀问卷
     */
    void sendRegularQuestionnaires();

    /**
     * 发送随机抽查问卷
     */
    void sendRandomQuestionnaires();

    /**
     * 发送定时问卷（通用方法）
     */
    void sendScheduledQuestionnaires(Integer questionType, String frequency);

    /**
     * 生成领养回访问卷
     */
    void generateAdoptFollowUpQuestionnaire(Integer userId, Integer petId);

    /**
     * 生成月度问卷
     */
    void generateMonthlyQuestionnaire(Integer userId, Integer petId);
}