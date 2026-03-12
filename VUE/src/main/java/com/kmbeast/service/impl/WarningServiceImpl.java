package com.kmbeast.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.exception.BusinessException;
import com.kmbeast.mapper.*;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.ManualSendDto;
import com.kmbeast.pojo.dto.QuestionnaireSubmitDto;
import com.kmbeast.pojo.dto.WarningQuestionnaireQueryDto;
import com.kmbeast.pojo.dto.WarningRecordQueryDto;
import com.kmbeast.pojo.entity.WarningQuestionTemplate;
import com.kmbeast.pojo.entity.WarningQuestionnaire;
import com.kmbeast.pojo.entity.WarningRecord;
import com.kmbeast.pojo.vo.*;
import com.kmbeast.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预警服务实现
 * 功能分区：用户功能 | 管理员功能 | 系统功能 | 私有工具方法
 */
@Service
@Slf4j
public class WarningServiceImpl implements WarningService {

    @Autowired
    private WarningQuestionnaireMapper warningQuestionnaireMapper;

    @Autowired
    private WarningRecordMapper warningRecordMapper;

    @Autowired
    private WarningQuestionTemplateMapper templateMapper;

    @Autowired
    private PetAdoptOrderMapper petAdoptOrderMapper;

    // ==================== 用户功能实现 ====================

    @Override
    public Result<List<WarningQuestionnaireVO>> getUserPendingQuestionnaires() {
        try {
            Integer userId = LocalThreadHolder.getUserId();
            if (userId == null) {
                return ApiResult.error("用户未登录");
            }

            log.info("获取用户{}的待回答问卷", userId);

            List<WarningQuestionnaireVO> questionnaires =
                    warningQuestionnaireMapper.selectPendingByUserId(userId);

            // 解析questions字段
            if (questionnaires != null && !questionnaires.isEmpty()) {
                for (WarningQuestionnaireVO vo : questionnaires) {
                    parseQuestionList(vo);
                }
            }

            return ApiResult.success(questionnaires);
        } catch (Exception e) {
            log.error("获取用户待回答问卷失败", e);
            return ApiResult.error("获取问卷失败");
        }
    }

    @Override
    public Result<String> submitQuestionnaire(QuestionnaireSubmitDto dto) {
        try {
            Integer userId = LocalThreadHolder.getUserId();
            if (userId == null) {
                return ApiResult.error("用户未登录");
            }

            // 查询问卷
            WarningQuestionnaire questionnaire = warningQuestionnaireMapper.selectById(dto.getQuestionnaireId());
            if (questionnaire == null) {
                return ApiResult.error("问卷不存在");
            }

            // 权限检查
            if (!questionnaire.getUserId().equals(userId)) {
                return ApiResult.error("无权提交此问卷");
            }

            // 更新问卷状态
            questionnaire.setStatus(2);  // 已提交
            questionnaire.setAnswers(dto.getAnswers());
            questionnaire.setSubmitTime(LocalDateTime.now());

            // 计算分数和预警级别
            int score = calculateScore(dto.getAnswers());
            int warningLevel = getWarningLevel(score);

            questionnaire.setScore(score);
            questionnaire.setWarningLevel(warningLevel);

            // 保存到数据库
            warningQuestionnaireMapper.updateById(questionnaire);

            // 如果产生预警，创建预警记录
            if (warningLevel > 0) {
                createWarningRecord(questionnaire, warningLevel);
            }

            log.info("用户{}提交问卷{}成功，分数：{}，预警级别：{}",
                    userId, dto.getQuestionnaireId(), score, warningLevel);

            return ApiResult.success("问卷提交成功");
        } catch (Exception e) {
            log.error("提交问卷失败", e);
            return ApiResult.error("提交问卷失败");
        }
    }

    @Override
    public Result<List<WarningQuestionnaireVO>> getUserQuestionnaireHistory() {
        try {
            Integer userId = LocalThreadHolder.getUserId();
            if (userId == null) {
                return ApiResult.error("用户未登录");
            }

            log.info("获取用户{}的历史问卷", userId);

            LambdaQueryWrapper<WarningQuestionnaire> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(WarningQuestionnaire::getUserId, userId)
                    .eq(WarningQuestionnaire::getStatus, 2)  // 已提交
                    .orderByDesc(WarningQuestionnaire::getCreateTime);

            List<WarningQuestionnaire> entities = warningQuestionnaireMapper.selectList(wrapper);

            List<WarningQuestionnaireVO> history = new ArrayList<>();
            for (WarningQuestionnaire entity : entities) {
                WarningQuestionnaireVO vo = new WarningQuestionnaireVO();
                org.springframework.beans.BeanUtils.copyProperties(entity, vo);
                history.add(vo);
            }

            return ApiResult.success(history);
        } catch (Exception e) {
            log.error("获取用户历史问卷失败", e);
            return ApiResult.error("获取历史问卷失败");
        }
    }

    // ==================== 管理员功能实现 ====================

    @Override
    public Result<List<WarningRecordVO>> getWarningRecords(WarningRecordQueryDto queryDto) {
        try {
            Integer roleId = LocalThreadHolder.getRoleId();
            if (roleId == null || roleId != 1) {
                return ApiResult.error("无权限访问");
            }

            log.info("管理员获取预警记录");

            if (queryDto == null) {
                queryDto = new WarningRecordQueryDto();
            }
            if (queryDto.getCurrent() == null) {
                queryDto.setCurrent(1);
            }
            if (queryDto.getSize() == null) {
                queryDto.setSize(10);
            }
            queryDto.setOffset((queryDto.getCurrent() - 1) * queryDto.getSize());

            List<WarningRecordVO> records = warningRecordMapper.selectList(queryDto);
            return ApiResult.success(records);
        } catch (Exception e) {
            log.error("获取预警记录失败", e);
            return ApiResult.error("获取预警记录失败");
        }
    }

    @Override
    public Result<String> processWarning(Integer warningId) {
        try {
            Integer roleId = LocalThreadHolder.getRoleId();
            if (roleId == null || roleId != 1) {
                return ApiResult.error("无权限处理");
            }

            log.info("处理预警记录: {}", warningId);

            WarningRecord record = new WarningRecord();
            record.setId(warningId);
            record.setStatus(2);
            record.setProcessedBy(LocalThreadHolder.getUserId());
            record.setProcessedTime(LocalDateTime.now());

            warningRecordMapper.update(record);

            return ApiResult.success("预警处理成功");
        } catch (Exception e) {
            log.error("处理预警失败", e);
            return ApiResult.error("处理预警失败");
        }
    }

    @Override
    public Result<WarningStatisticsVO> getWarningStatistics() {
        try {
            Integer roleId = LocalThreadHolder.getRoleId();
            if (roleId == null || roleId != 1) {
                return ApiResult.error("无权限查看统计");
            }

            log.info("获取预警统计");

            WarningStatisticsVO stats = new WarningStatisticsVO();

            QuestionnaireStatsVO questionnaireStats = warningQuestionnaireMapper.selectStatsByAdmin();
            if (questionnaireStats != null) {
                stats.setTotalQuestionnaires(questionnaireStats.getTotalQuestionnaires());
                stats.setPendingCount(questionnaireStats.getPendingCount());
                stats.setCompletedCount(questionnaireStats.getCompletedCount());
                stats.setRiskCount(questionnaireStats.getRiskCount());
                stats.setAvgScore(questionnaireStats.getAvgScore());
            }

            WarningRecordQueryDto queryDto = new WarningRecordQueryDto();
            stats.setTotalWarnings((long) warningRecordMapper.selectCount(queryDto));
            stats.setPendingWarnings((long) warningRecordMapper.countPendingWarnings());

            List<RiskLevelStatsVO> riskStats = warningQuestionnaireMapper.selectRiskLevelStats();
            if (riskStats != null) {
                for (RiskLevelStatsVO r : riskStats) {
                    switch (r.getLevel()) {
                        case 1: stats.setLowRiskCount(r.getCount()); break;
                        case 2: stats.setMediumRiskCount(r.getCount()); break;
                        case 3: stats.setHighRiskCount(r.getCount()); break;
                    }
                }
            }

            List<HighRiskUserVO> highRiskUsers = warningRecordMapper.selectHighRiskUsers(10);
            stats.setHighRiskUsers(highRiskUsers);

            return ApiResult.success(stats);
        } catch (Exception e) {
            log.error("获取预警统计失败", e);
            return ApiResult.error("获取统计失败");
        }
    }

    @Override
    public List<WarningQuestionnaireVO> getQuestionnaireListByAdmin(WarningQuestionnaireQueryDto queryDto) {
        try {
            List<WarningQuestionnaireVO> list = warningQuestionnaireMapper.selectAllByAdmin(queryDto);

            if (list != null && !list.isEmpty()) {
                log.info("查询到 {} 条问卷记录", list.size());
                for (WarningQuestionnaireVO vo : list) {
                    log.debug("问卷ID: {}, 用户ID: {}, 用户名: {}, 宠物名: {}",
                            vo.getId(), vo.getUserId(), vo.getUserName(), vo.getPetName());
                }
            }

            if (list != null && !list.isEmpty()) {
                for (WarningQuestionnaireVO vo : list) {
                    parseQuestionList(vo);
                }
            }

            return list;
        } catch (Exception e) {
            log.error("管理员查询问卷列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Integer countQuestionnaireByAdmin(WarningQuestionnaireQueryDto queryDto) {
        try {
            return warningQuestionnaireMapper.selectCountByAdmin(queryDto);
        } catch (Exception e) {
            log.error("管理员统计问卷总数失败", e);
            return 0;
        }
    }

    @Override
    public Result<QuestionnaireStatsVO> getAdminStatistics() {
        try {
            QuestionnaireStatsVO stats = warningQuestionnaireMapper.selectStatsByAdmin();
            return ApiResult.success(stats);
        } catch (Exception e) {
            log.error("获取管理员统计失败", e);
            return ApiResult.error("获取统计失败");
        }
    }

    @Override
    public List<AvailableUserVO> getAvailableUsers(Integer warningLevel, String username, String petName) {
        try {
            List<AvailableUserVO> users = warningQuestionnaireMapper.selectAvailableUsers();

            if (warningLevel != null) {
                users = users.stream()
                        .filter(u -> u.getWarningLevel() != null && u.getWarningLevel().equals(warningLevel))
                        .collect(Collectors.toList());
            }

            if (StringUtils.hasText(username)) {
                users = users.stream()
                        .filter(u -> u.getUsername() != null && u.getUsername().contains(username))
                        .collect(Collectors.toList());
            }

            if (StringUtils.hasText(petName)) {
                users = users.stream()
                        .filter(u -> u.getPetName() != null && u.getPetName().contains(petName))
                        .collect(Collectors.toList());
            }

            return users;
        } catch (Exception e) {
            log.error("获取可发放用户列表失败", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void manualSendQuestionnaire(ManualSendDto dto) {
        try {
            WarningQuestionTemplate template = templateMapper.selectById(dto.getTemplateId());
            if (template == null) {
                throw new BusinessException("问卷模板不存在");
            }

            for (int i = 0; i < dto.getUserIds().size(); i++) {
                Integer userId = dto.getUserIds().get(i);
                Integer petId = dto.getPetIds().get(i);

                WarningQuestionnaireQueryDto queryDto = new WarningQuestionnaireQueryDto();
                queryDto.setUserId(userId);
                queryDto.setPetId(petId);
                queryDto.setStatus(1);
                Integer pendingCount = warningQuestionnaireMapper.selectCountByAdmin(queryDto);

                if (pendingCount > 0) {
                    log.warn("用户{}宠物{}已有待回答问卷，跳过发放", userId, petId);
                    continue;
                }

                WarningQuestionnaire questionnaire = new WarningQuestionnaire();
                questionnaire.setUserId(userId);
                questionnaire.setPetId(petId);
                questionnaire.setQuestionType(dto.getQuestionType() != null ? dto.getQuestionType() : 3);
                questionnaire.setQuestions(template.getQuestions());
                questionnaire.setStatus(1);
                questionnaire.setCreateTime(LocalDateTime.now());
                questionnaire.setDeadline(LocalDateTime.now().plusDays(7));

                warningQuestionnaireMapper.insert(questionnaire);
                log.info("手动发放问卷成功：用户{}，宠物{}", userId, petId);
            }
        } catch (Exception e) {
            log.error("手动发放问卷失败", e);
            throw new BusinessException("发放失败：" + e.getMessage());
        }
    }

    @Override
    public List<WarningQuestionTemplate> getTemplates(Integer templateType) {
        try {
            if (templateType != null) {
                return templateMapper.selectByType(templateType);
            } else {
                return templateMapper.selectAll();
            }
        } catch (Exception e) {
            log.error("获取模板列表失败", e);
            return new ArrayList<>();
        }
    }

    // ==================== 系统功能实现 ====================

    @Override
    public void sendRegularQuestionnaires() {
        log.info("发送定期关怀问卷");
        sendScheduledQuestionnaires(1, "MONTH");
    }

    @Override
    public void sendRandomQuestionnaires() {
        log.info("发送随机抽查问卷");
        sendScheduledQuestionnaires(2, "WEEK");
    }

    @Override
    public void sendScheduledQuestionnaires(Integer questionType, String frequency) {
        try {
            WarningQuestionTemplate template = templateMapper.getByTypeAndFrequency(questionType, frequency);
            if (template == null) {
                log.error("未找到对应模板: type={}, frequency={}", questionType, frequency);
                return;
            }

            List<AdoptRelationVO> relations = petAdoptOrderMapper.selectAdoptedUsers();

            int successCount = 0;
            for (AdoptRelationVO relation : relations) {
                try {
                    boolean hasSent = warningQuestionnaireMapper.checkMonthlySent(
                            relation.getUserId(),
                            relation.getPetId(),
                            questionType
                    );

                    if (!hasSent) {
                        WarningQuestionnaire questionnaire = new WarningQuestionnaire();
                        questionnaire.setUserId(relation.getUserId());
                        questionnaire.setPetId(relation.getPetId());
                        questionnaire.setQuestionType(questionType);
                        questionnaire.setQuestions(template.getQuestions());
                        questionnaire.setStatus(1);
                        questionnaire.setCreateTime(LocalDateTime.now());
                        questionnaire.setDeadline(LocalDateTime.now().plusDays(7));

                        warningQuestionnaireMapper.insert(questionnaire);
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("为用户{}的宠物{}生成问卷失败", relation.getUserId(), relation.getPetId(), e);
                }
            }

            log.info("定时问卷发送完成，共发送{}份", successCount);
        } catch (Exception e) {
            log.error("发送定时问卷失败", e);
        }
    }

    @Override
    public void generateAdoptFollowUpQuestionnaire(Integer userId, Integer petId) {
        try {
            WarningQuestionTemplate template = templateMapper.getByTypeAndFrequency(3, "ONCE");
            if (template == null) {
                log.error("未找到领养回访模板");
                return;
            }

            WarningQuestionnaire questionnaire = new WarningQuestionnaire();
            questionnaire.setUserId(userId);
            questionnaire.setPetId(petId);
            questionnaire.setQuestionType(3);
            questionnaire.setQuestions(template.getQuestions());
            questionnaire.setStatus(1);
            questionnaire.setCreateTime(LocalDateTime.now());
            questionnaire.setDeadline(LocalDateTime.now().plusDays(7));

            warningQuestionnaireMapper.insert(questionnaire);
            log.info("已为用户{}的宠物{}生成领养回访问卷", userId, petId);
        } catch (Exception e) {
            log.error("生成领养回访问卷失败", e);
        }
    }

    @Override
    public void generateMonthlyQuestionnaire(Integer userId, Integer petId) {
        try {
            WarningQuestionTemplate template = templateMapper.getByTypeAndFrequency(1, "MONTH");
            if (template == null) {
                log.error("未找到月度关怀模板");
                return;
            }

            WarningQuestionnaire questionnaire = new WarningQuestionnaire();
            questionnaire.setUserId(userId);
            questionnaire.setPetId(petId);
            questionnaire.setQuestionType(1);
            questionnaire.setQuestions(template.getQuestions());
            questionnaire.setStatus(1);
            questionnaire.setCreateTime(LocalDateTime.now());
            questionnaire.setDeadline(LocalDateTime.now().plusDays(7));

            warningQuestionnaireMapper.insert(questionnaire);
            log.info("已为用户{}的宠物{}生成月度问卷", userId, petId);
        } catch (Exception e) {
            log.error("生成月度问卷失败", e);
        }
    }

    // ==================== 私有工具方法 ====================

    private void parseQuestionList(WarningQuestionnaireVO vo) {
        String questionsJson = vo.getQuestions();
        if (questionsJson != null && !questionsJson.isEmpty()) {
            try {
                List<WarningQuestionnaireVO.QuestionItem> questionList =
                        JSON.parseArray(questionsJson, WarningQuestionnaireVO.QuestionItem.class);
                vo.setQuestionList(questionList);
            } catch (Exception e) {
                log.error("解析问卷问题失败, id: {}", vo.getId(), e);
            }
        }
    }

    private int calculateScore(String answersJson) {
        try {
            if (!StringUtils.hasText(answersJson)) {
                return 0;
            }

            JSONObject answers = JSON.parseObject(answersJson);
            int totalScore = 0;
            int count = 0;

            String answer1 = answers.getString("101");
            if (answer1 != null) {
                if ("3小时以上".equals(answer1)) totalScore += 100;
                else if ("2-3小时".equals(answer1)) totalScore += 80;
                else if ("1-2小时".equals(answer1)) totalScore += 60;
                else if ("小于1小时".equals(answer1)) totalScore += 30;
                count++;
            }

            String answer2 = answers.getString("102");
            if (answer2 != null) {
                if ("正常".equals(answer2)) totalScore += 100;
                else if ("减少".equals(answer2)) totalScore += 40;
                else if ("增加".equals(answer2)) totalScore += 70;
                else if ("废食".equals(answer2)) totalScore += 10;
                count++;
            }

            String answer3 = answers.getString("103");
            if (answer3 != null) {
                if ("很好".equals(answer3)) totalScore += 100;
                else if ("正常".equals(answer3)) totalScore += 80;
                else if ("萎靡".equals(answer3)) totalScore += 30;
                else if ("异常兴奋".equals(answer3)) totalScore += 20;
                count++;
            }

            return count > 0 ? totalScore / count : 0;
        } catch (Exception e) {
            log.error("计算分数失败", e);
            return 0;
        }
    }

    private int getWarningLevel(int score) {
        if (score >= 80) return 0;
        if (score >= 60) return 1;
        if (score >= 30) return 2;
        return 3;
    }

    private void createWarningRecord(WarningQuestionnaire questionnaire, int warningLevel) {
        try {
            WarningRecord record = new WarningRecord();
            record.setUserId(questionnaire.getUserId());
            record.setPetId(questionnaire.getPetId());
            record.setQuestionnaireId(questionnaire.getId());
            record.setWarningLevel(warningLevel);
            record.setStatus(1);
            record.setCreateTime(LocalDateTime.now());

            switch (warningLevel) {
                case 1:
                    record.setReason("陪伴时间或照顾情况略有不足");
                    record.setSuggestion("建议增加每日陪伴时间，关注宠物状态");
                    break;
                case 2:
                    record.setReason("存在潜在的弃养风险");
                    record.setSuggestion("需要进行电话回访，了解具体情况");
                    break;
                case 3:
                    record.setReason("高度弃养风险");
                    record.setSuggestion("立即联系用户，安排家访或面谈");
                    break;
            }

            warningRecordMapper.insert(record);
            log.info("创建预警记录成功，问卷ID: {}, 预警级别: {}", questionnaire.getId(), warningLevel);
        } catch (Exception e) {
            log.error("创建预警记录失败", e);
        }
    }
}