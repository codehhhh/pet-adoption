package com.kmbeast.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.kmbeast.mapper.WarningQuestionnaireMapper;
import com.kmbeast.pojo.entity.WarningQuestionnaire;
import com.kmbeast.service.WarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@EnableScheduling
@Slf4j
public class WarningScheduledTask {

    @Autowired
    private WarningService warningService;

    @Autowired
    private WarningQuestionnaireMapper warningQuestionnaireMapper;

    /**
     * 每月1号早上9点：给所有已领养用户发送月度问卷
     */
    @Scheduled(cron = "0 0 9 1 * ?")
    public void sendMonthlyQuestionnaires() {
        log.info("【定时任务】开始发送月度关怀问卷");
        warningService.sendScheduledQuestionnaires(1, "MONTH");
    }

    /**
     * 每季度1号：发送季度健康检查问卷
     */
    @Scheduled(cron = "0 0 9 1 1,4,7,10 ?")
    public void sendQuarterlyQuestionnaires() {
        log.info("【定时任务】开始发送季度健康检查问卷");
        warningService.sendScheduledQuestionnaires(1, "QUARTER");
    }

    /**
     * 每周一：随机抽查20%用户
     */
    @Scheduled(cron = "0 0 10 ? * MON")
    public void sendRandomQuestionnaires() {
        log.info("【定时任务】开始发送随机抽查问卷");
        warningService.sendRandomQuestionnaires();
    }

    /**
     * 每天凌晨2点：处理过期问卷
     * 将截止时间已过的待回答问卷标记为过期
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void processExpiredQuestionnaires() {
        log.info("【定时任务】开始处理过期问卷");

        try {
            // 创建要更新的实体
            WarningQuestionnaire entity = new WarningQuestionnaire();
            entity.setStatus(3);  // 3=已过期

            // 创建更新条件
            LambdaUpdateWrapper<WarningQuestionnaire> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(WarningQuestionnaire::getStatus, 1)  // 待回答
                    .lt(WarningQuestionnaire::getDeadline, LocalDateTime.now()); // 已过截止时间

            // 执行更新
            int updatedCount = warningQuestionnaireMapper.update(entity, updateWrapper);

            log.info("【定时任务】已处理 {} 份过期问卷", updatedCount);

        } catch (Exception e) {
            log.error("处理过期问卷失败", e);
        }
    }
}