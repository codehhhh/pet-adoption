package com.kmbeast.mapper;

import com.kmbeast.pojo.dto.WarningRecordQueryDto;
import com.kmbeast.pojo.entity.WarningRecord;
import com.kmbeast.pojo.vo.HighRiskUserVO;
import com.kmbeast.pojo.vo.WarningRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 预警记录Mapper
 */
@Mapper
public interface WarningRecordMapper {

    void insert(WarningRecord record);

    void update(WarningRecord record);

    WarningRecord selectById(@Param("id") Integer id);

    List<WarningRecordVO> selectList(WarningRecordQueryDto queryDto);

    Integer selectCount(WarningRecordQueryDto queryDto);

    /**
     * 获取待处理的预警数量
     */
    @Select("SELECT COUNT(*) FROM warning_record WHERE status = 1")
    Integer countPendingWarnings();

    /**
     * 获取高风险用户列表（预警级别>=2的未处理预警）
     */
    List<HighRiskUserVO> selectHighRiskUsers(@Param("limit") Integer limit);
}