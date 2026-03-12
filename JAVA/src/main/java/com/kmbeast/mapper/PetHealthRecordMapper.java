package com.kmbeast.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;  // 导入BaseMapper
import com.kmbeast.pojo.dto.PetHealthRecordQueryDto;
import com.kmbeast.pojo.entity.PetHealthRecord;
import com.kmbeast.pojo.vo.PetHealthRecordVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 宠物健康档案Mapper接口
 * 继承BaseMapper获得基础CRUD方法
 */
public interface PetHealthRecordMapper extends BaseMapper<PetHealthRecord> {

    PetHealthRecordVO getById(@Param("id") Long id);

    List<PetHealthRecordVO> list(PetHealthRecordQueryDto queryDto);

    Integer listCount(PetHealthRecordQueryDto queryDto);

    List<PetHealthRecordVO> queryByPetId(@Param("petId") Integer petId);

    List<PetHealthRecordVO> queryByUserId(@Param("userId") Integer userId);

    List<PetHealthRecordVO> queryAllForAdmin(PetHealthRecordQueryDto queryDto);

    Integer queryAllForAdminCount(PetHealthRecordQueryDto queryDto);

}