package com.kmbeast.mapper;

import com.kmbeast.pojo.entity.WarningRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 预警规则Mapper
 */
@Mapper
public interface WarningRuleMapper {

    List<WarningRule> selectAllActive();

    WarningRule selectByScore(@Param("score") Integer score);
}