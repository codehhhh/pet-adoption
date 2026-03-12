package com.kmbeast.mapper;

import com.kmbeast.pojo.entity.WarningQuestionTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WarningQuestionTemplateMapper {

    @Select("SELECT * FROM warning_question_template WHERE id = #{id}")
    WarningQuestionTemplate selectById(@Param("id") Integer id);

    @Select("SELECT * FROM warning_question_template WHERE template_type = #{templateType} AND frequency = #{frequency} AND is_active = 1 LIMIT 1")
    WarningQuestionTemplate getByTypeAndFrequency(@Param("templateType") Integer templateType, @Param("frequency") String frequency);

    @Select("SELECT * FROM warning_question_template WHERE template_type = #{templateType} AND is_active = 1")
    List<WarningQuestionTemplate> selectByType(@Param("templateType") Integer templateType);

    @Select("SELECT * FROM warning_question_template WHERE is_active = 1")
    List<WarningQuestionTemplate> selectAll();
}