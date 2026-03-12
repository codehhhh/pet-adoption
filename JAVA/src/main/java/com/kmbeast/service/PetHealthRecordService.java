package com.kmbeast.service;

import com.baomidou.mybatisplus.extension.service.IService;  // 导入IService
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetHealthRecordQueryDto;
import com.kmbeast.pojo.entity.PetHealthRecord;  // 导入实体类
import com.kmbeast.pojo.vo.PetHealthRecordVO;

import java.util.List;

/**
 * 宠物健康档案服务接口
 * 继承IService获得基础CRUD方法
 */
public interface PetHealthRecordService extends IService<PetHealthRecord> {

    /**
     * 分页查询健康档案（返回VO）
     * 业务说明：根据用户角色返回不同数据，管理员看所有，用户只看自己的
     */
    Result<List<PetHealthRecordVO>> list(PetHealthRecordQueryDto queryDto);

    /**
     * 根据宠物ID查询（返回VO）
     * 业务说明：查询指定宠物的所有健康记录
     */
    Result<List<PetHealthRecordVO>> queryByPetId(Integer petId);

    /**
     * 查询当前用户的宠物健康档案（返回VO）
     * 业务说明：查询当前登录用户所领养宠物的所有健康记录
     */
    Result<List<PetHealthRecordVO>> queryByUserId();

}