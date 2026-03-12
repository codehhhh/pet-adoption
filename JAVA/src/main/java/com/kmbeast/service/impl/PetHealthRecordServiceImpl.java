package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;  // 导入ServiceImpl
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.mapper.PetAdoptOrderMapper;
import com.kmbeast.mapper.PetHealthRecordMapper;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetHealthRecordQueryDto;
import com.kmbeast.pojo.entity.PetAdoptOrder;
import com.kmbeast.pojo.entity.PetHealthRecord;
import com.kmbeast.pojo.vo.PetHealthRecordVO;
import com.kmbeast.service.PetHealthRecordService;
import org.springframework.beans.BeanUtils;  // 导入BeanUtils
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物健康档案服务实现类
 * 继承ServiceImpl获得基础CRUD实现
 */
@Service
public class PetHealthRecordServiceImpl
        extends ServiceImpl<PetHealthRecordMapper, PetHealthRecord>  // 继承ServiceImpl
        implements PetHealthRecordService {  // 实现自定义接口

    @Resource
    private PetHealthRecordMapper petHealthRecordMapper;

    @Resource
    private PetAdoptOrderMapper petAdoptOrderMapper;

    @Override
    public Result<List<PetHealthRecordVO>> list(PetHealthRecordQueryDto queryDto) {
        try {
            Integer userId = LocalThreadHolder.getUserId();
            Integer roleId = LocalThreadHolder.getRoleId();

            List<PetHealthRecordVO> vos;

            if (roleId != null && roleId == 1) { // 管理员
                vos = petHealthRecordMapper.queryAllForAdmin(queryDto);
            } else if (userId != null) { // 普通用户
                queryDto.setUserId(userId); // 设置用户ID筛选
                vos = petHealthRecordMapper.list(queryDto);
            } else {
                return ApiResult.error("用户未登录");
            }

            // 设置枚举文本
            setEnumTexts(vos);
            return ApiResult.success(vos);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<PetHealthRecordVO>> queryByPetId(Integer petId) {
        try {
            // 验证用户是否有权限查看此宠物的记录
            Integer userId = LocalThreadHolder.getUserId();
            Integer roleId = LocalThreadHolder.getRoleId();

            if (userId != null && roleId != null && roleId != 1) { // 不是管理员
                boolean isAdopted = checkIfUserAdoptedPet(userId, petId);
                if (!isAdopted) {
                    return ApiResult.error("您未领养此宠物，无法查看健康记录");
                }
            }

            List<PetHealthRecordVO> vos = petHealthRecordMapper.queryByPetId(petId);
            // 设置枚举文本
            setEnumTexts(vos);
            return ApiResult.success(vos);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<PetHealthRecordVO>> queryByUserId() {
        try {
            Integer userId = LocalThreadHolder.getUserId();
            if (userId == null) {
                return ApiResult.error("用户未登录");
            }

            List<PetHealthRecordVO> vos = petHealthRecordMapper.queryByUserId(userId);
            // 设置枚举文本
            setEnumTexts(vos);
            return ApiResult.success(vos);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID查询并返回VO（工具方法）
     */
    public Result<PetHealthRecordVO> getVoById(Long id) {
        try {
            // 1. 用继承来的getById获取实体
            PetHealthRecord record = super.getById(id);
            if (record == null) {
                return ApiResult.error("记录不存在");
            }

            // 2. 验证查看权限
            Integer userId = LocalThreadHolder.getUserId();
            Integer roleId = LocalThreadHolder.getRoleId();

            if (userId != null && roleId != null && roleId != 1) { // 不是管理员
                boolean isAdopted = checkIfUserAdoptedPet(userId, record.getPetId());
                if (!isAdopted) {
                    return ApiResult.error("您没有权限查看此记录");
                }
            }

            // 3. 实体转VO
            PetHealthRecordVO vo = convertToVO(record);

            // 4. 补充关联信息（从Mapper查询）
            PetHealthRecordVO detailVo = petHealthRecordMapper.getById(id);
            if (detailVo != null) {
                // 复制关联信息
                vo.setPetName(detailVo.getPetName());
                vo.setPetCover(detailVo.getPetCover());
                vo.setOwnerName(detailVo.getOwnerName());
                vo.setOwnerAvatar(detailVo.getOwnerAvatar());
            }

            // 5. 设置枚举文本
            setEnumTextsForSingle(vo);

            return ApiResult.success(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("查询失败：" + e.getMessage());
        }
    }

    // ========== 保留原有的工具方法 ==========

    /**
     * 检查用户是否领养了指定的宠物
     */
    private boolean checkIfUserAdoptedPet(Integer userId, Integer petId) {
        try {
            List<PetAdoptOrder> orders = petAdoptOrderMapper.selectList(
                    new QueryWrapper<PetAdoptOrder>()
                            .eq("user_id", userId)
                            .eq("pet_id", petId)
                            .eq("status", 4)
                            .last("LIMIT 1")
            );
            return orders != null && !orders.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 实体转VO
     */
    private PetHealthRecordVO convertToVO(PetHealthRecord record) {
        if (record == null) return null;
        PetHealthRecordVO vo = new PetHealthRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    /**
     * 设置单个VO的枚举文本
     */
    private void setEnumTextsForSingle(PetHealthRecordVO vo) {
        if (vo == null) return;
        if (vo.getBodyCondition() != null) {
            vo.setBodyConditionText(getConditionText(vo.getBodyCondition()));
        }
        if (vo.getAppetite() != null) {
            vo.setAppetiteText(getAppetiteText(vo.getAppetite()));
        }
        if (vo.getEnergyLevel() != null) {
            vo.setEnergyLevelText(getEnergyLevelText(vo.getEnergyLevel()));
        }
    }

    /**
     * 设置VO列表的枚举文本
     */
    private void setEnumTexts(List<PetHealthRecordVO> vos) {
        if (vos == null || vos.isEmpty()) return;
        for (PetHealthRecordVO vo : vos) {
            setEnumTextsForSingle(vo);
        }
    }

    /**
     * 根据身体状况代码获取文本
     */
    private String getConditionText(Integer condition) {
        if (condition == null) return "未知";
        switch (condition) {
            case 1: return "优秀";
            case 2: return "良好";
            case 3: return "一般";
            case 4: return "需关注";
            case 5: return "需治疗";
            default: return "未知";
        }
    }

    /**
     * 根据食欲代码获取文本
     */
    private String getAppetiteText(Integer appetite) {
        if (appetite == null) return "未知";
        switch (appetite) {
            case 1: return "食欲旺盛";
            case 2: return "正常进食";
            case 3: return "食欲一般";
            case 4: return "食欲不振";
            case 5: return "拒绝进食";
            default: return "未知";
        }
    }

    /**
     * 根据活力水平代码获取文本
     */
    private String getEnergyLevelText(Integer energyLevel) {
        if (energyLevel == null) return "未知";
        switch (energyLevel) {
            case 1: return "精力充沛";
            case 2: return "活跃";
            case 3: return "正常";
            case 4: return "懒散";
            case 5: return "疲惫";
            default: return "未知";
        }
    }
}