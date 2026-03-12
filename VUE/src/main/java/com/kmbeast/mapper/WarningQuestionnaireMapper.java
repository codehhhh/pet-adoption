package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kmbeast.pojo.dto.WarningQuestionnaireQueryDto;
import com.kmbeast.pojo.entity.WarningQuestionnaire;
import com.kmbeast.pojo.vo.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WarningQuestionnaireMapper extends BaseMapper<WarningQuestionnaire> {

    /**
     * 查询用户待回答问卷
     */
    List<WarningQuestionnaireVO> selectPendingByUserId(@Param("userId") Integer userId);

    /**
     * 管理员分页查询所有问卷
     */
    List<WarningQuestionnaireVO> selectAllByAdmin(@Param("dto") WarningQuestionnaireQueryDto dto);

    /**
     * 统计问卷总数（管理员）
     */
    Integer selectCountByAdmin(@Param("dto") WarningQuestionnaireQueryDto dto);

    /**
     * 获取管理员统计
     */
    QuestionnaireStatsVO selectStatsByAdmin();

    /**
     * 获取风险级别统计
     */
    List<RiskLevelStatsVO> selectRiskLevelStats();

    /**
     * 检查月度问卷是否已发送
     */
    Boolean checkMonthlySent(@Param("userId") Integer userId,
                             @Param("petId") Integer petId,
                             @Param("type") Integer type);

    /**
     * 获取可发放问卷的用户列表（已完成领养且无待回答问卷）
     */
    List<AvailableUserVO> selectAvailableUsers();

    /**
     * 根据DTO查询问卷列表（用于用户历史问卷）
     */
    List<WarningQuestionnaireVO> selectListByDto(@Param("dto") WarningQuestionnaireQueryDto dto);

    /**
     * 根据ID查询问卷实体（用于内部操作，不关联用户和宠物表）
     */
    WarningQuestionnaire selectEntityById(@Param("id") Integer id);

    /**
     * 根据ID查询问卷（带关联信息）- 用于前端展示
     */
    WarningQuestionnaireVO selectVoById(@Param("id") Integer id);
}