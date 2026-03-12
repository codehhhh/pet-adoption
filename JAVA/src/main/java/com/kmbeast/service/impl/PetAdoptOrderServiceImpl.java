package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.mapper.AddressMapper;
import com.kmbeast.mapper.PetAdoptOrderMapper;
import com.kmbeast.mapper.PetMapper;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetAdoptOrderQueryDto;
import com.kmbeast.pojo.em.IsAgainPostEnum;
import com.kmbeast.pojo.em.PetAdoptOrderStatus;
import com.kmbeast.pojo.entity.Address;
import com.kmbeast.pojo.entity.Pet;
import com.kmbeast.pojo.entity.PetAdoptOrder;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.pojo.vo.PetAdoptOrderVO;
import com.kmbeast.pojo.vo.PetVO;
import com.kmbeast.service.PetAdoptOrderService;
import com.kmbeast.utils.AssertUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 宠物领养订单业务逻辑实现类
 */
@Slf4j
@Service
public class PetAdoptOrderServiceImpl extends ServiceImpl<PetAdoptOrderMapper, PetAdoptOrder> implements PetAdoptOrderService {

    @Resource
    private PetMapper petMapper;
    @Resource
    private AddressMapper addressMapper;
    @Resource
    private UserMapper userMapper;

    /**
     * 宠物领养订单生成
     *
     * @param petAdoptOrder 实体信息
     * @return Result<String>
     */
    @Override
    public Result<String> saveEntity(PetAdoptOrder petAdoptOrder) {
        AssertUtils.notNull(petAdoptOrder.getPetId(), "宠物ID不能为空");
        AssertUtils.notNull(petAdoptOrder.getAddressId(), "收货地址不能为空");
        AssertUtils.hasText(petAdoptOrder.getDetail(), "证明材料不能为空");

        // 宠物信息校验
        PetVO petVO = petMapper.getById(petAdoptOrder.getPetId());
        AssertUtils.notNull(petVO, "宠物信息异常");

        // 检查宠物是否已被领养（isAdopt字段）
        Boolean isAdopt = petVO.getIsAdopt();
        AssertUtils.isTrue(isAdopt != null && !isAdopt, "宠物已被领养");

        // 新增：检查是否有已完成（status=4）的领养订单
        Integer completedCount = this.baseMapper.countCompletedOrdersByPetId(petAdoptOrder.getPetId());
        AssertUtils.isTrue(completedCount == null || completedCount == 0, "宠物已被其他用户领养完成");

        // 收货地址校验
        Address address = addressMapper.selectById(petAdoptOrder.getAddressId());
        AssertUtils.notNull(address, "收货地址信息异常");

        // 宠物现在绑定的订单
        PetAdoptOrderQueryDto petAdoptOrderQueryDto = new PetAdoptOrderQueryDto();
        petAdoptOrderQueryDto.setPetId(petAdoptOrder.getPetId());
        orderStatusConfirm(petAdoptOrderQueryDto);

        // 创建订单信息
        petAdoptOrder.setStatus(PetAdoptOrderStatus.REPLYING.getStatus()); // 初始领养订单生成，是申请中状态
        petAdoptOrder.setUserId(LocalThreadHolder.getUserId()); // 设置用户ID
        petAdoptOrder.setCreateTime(LocalDateTime.now()); // 设置创建时间
        petAdoptOrder.setPostNumber(1); // 初次提交1次
        petAdoptOrder.setIsAgainPost(IsAgainPostEnum.ORIGIN_REPLY.getStatus()); // 设置为初次提交状态

        save(petAdoptOrder);
        return ApiResult.success("宠物订单领养成功，请耐心等待审核");
    }
    private void orderStatusConfirm(PetAdoptOrderQueryDto petAdoptOrderQueryDto) {
        List<PetAdoptOrderVO> petAdoptOrderVOS = this.baseMapper.list(petAdoptOrderQueryDto);
        if (!petAdoptOrderVOS.isEmpty()) {
            for (PetAdoptOrderVO petAdoptOrderVO : petAdoptOrderVOS) {
                AssertUtils.isTrue(
                        !Objects.equals(petAdoptOrderVO.getStatus(), PetAdoptOrderStatus.REPLYING.getStatus()),
                        "已有用户正在申请领养该宠物，请关注后续情况"
                );
            }
        }
    }

    /**
     * 宠物领养订单修改
     * 1、 管理员审核用户创建的订单（申请中...）
     * 2、如果是审核不通过，用户调用这一块逻辑进行材料补充
     * 3、用户确认完成订单（状态从2变为4）
     *
     * @param petAdoptOrder 实体信息
     * @return Result<String>
     */
    @Override
    public Result<String> update(PetAdoptOrder petAdoptOrder) {
        // 获取原始订单状态（用于判断状态变化）
        PetAdoptOrder originalOrder = null;
        if (petAdoptOrder.getId() != null) {
            originalOrder = getById(petAdoptOrder.getId());
        }

        // 更新订单
        updateById(petAdoptOrder);

        // 检查是否是用户确认完成（状态从2变为4）
        if (originalOrder != null && petAdoptOrder.getStatus() != null) {
            boolean wasAudited = originalOrder.getStatus() == 2;  // 原状态是已审核
            boolean isNowCompleted = petAdoptOrder.getStatus() == 4;  // 新状态是已完成

            if (wasAudited && isNowCompleted) {
                log.info("========== 用户确认完成订单 ==========");
                log.info("订单ID: {}, 宠物ID: {}, 用户ID: {}",
                        petAdoptOrder.getId(), originalOrder.getPetId(), originalOrder.getUserId());

                // 1. 更新宠物状态为已领养
                updatePetAdoptStatus(originalOrder.getPetId(), true);

                // 2. 发放问卷
                generateAdoptQuestionnaire(originalOrder.getUserId(), originalOrder.getPetId());

                log.info("========== 订单完成后续处理成功 ==========");
            }
        }

        return ApiResult.success("操作成功");
    }

    /**
     * 宠物领养订单查询
     *
     * @param petAdoptOrderQueryDto 宠物领养信息查询条件拓展类
     * @return Result<List < PetAdoptOrderVO>>
     */
    @Override
    public Result<List<PetAdoptOrderVO>> query(PetAdoptOrderQueryDto petAdoptOrderQueryDto) {
        List<PetAdoptOrderVO> petAdoptOrderVOS = this.baseMapper.list(petAdoptOrderQueryDto);
        Integer count = this.baseMapper.listCount(petAdoptOrderQueryDto);
        return ApiResult.success(petAdoptOrderVOS, count);
    }
    /**
     * 管理员审核用户创建的订单
     *
     * @param petAdoptOrder 宠物领养订单信息
     * @return Result<String>
     */
    @Override
    public Result<String> audit(PetAdoptOrder petAdoptOrder) {
        // 判断当前审核者是不是管理员
        User user = userMapper.getUserById(LocalThreadHolder.getUserId());
        AssertUtils.notNull(user, "用户信息查询异常");
        AssertUtils.isTrue(user.getRole() == 1, "无操作权限");

        // 获取原始订单状态
        PetAdoptOrder originalOrder = getById(petAdoptOrder.getId());

        // 更新订单
        Result<String> result = update(petAdoptOrder);

        // 同步宠物状态：如果订单状态变为"已完成"(4)，则更新宠物为已领养
        if (originalOrder != null && petAdoptOrder.getStatus() != null) {
            // 检查状态是否从非4变为4
            boolean wasNotCompleted = !PetAdoptOrderStatus.COMPLETE.getStatus().equals(originalOrder.getStatus());
            boolean isNowCompleted = PetAdoptOrderStatus.COMPLETE.getStatus().equals(petAdoptOrder.getStatus());

            if (wasNotCompleted && isNowCompleted) {
                // 更新宠物状态为已领养
                updatePetAdoptStatus(petAdoptOrder.getPetId(), true);

                // ===== 自动生成问卷  =====
                generateAdoptQuestionnaire(originalOrder.getUserId(), petAdoptOrder.getPetId());
            }

            // 可选：如果状态从4变为非4，重置宠物状态
            boolean wasCompleted = PetAdoptOrderStatus.COMPLETE.getStatus().equals(originalOrder.getStatus());
            boolean isNowNotCompleted = !PetAdoptOrderStatus.COMPLETE.getStatus().equals(petAdoptOrder.getStatus());

            if (wasCompleted && isNowNotCompleted) {
                // 更新宠物状态为未领养
                updatePetAdoptStatus(petAdoptOrder.getPetId(), false);
            }
        }

        return result;
    }

    /**
     * 生成领养问卷的方法
     */
    private void generateAdoptQuestionnaire(Integer userId, Integer petId) {
        try {
            // 通过Spring上下文获取WarningService
            com.kmbeast.service.WarningService warningService =
                    com.kmbeast.utils.SpringContextHolder.getBean(com.kmbeast.service.WarningService.class);

            // 调用生成问卷的方法
            warningService.generateAdoptFollowUpQuestionnaire(userId, petId);
            warningService.generateMonthlyQuestionnaire(userId, petId);

            log.info("审核通过：已为用户{}的宠物{}生成问卷", userId, petId);
        } catch (Exception e) {
            log.error("生成问卷失败", e);
        }
    }
    /**
     * 更新宠物领养状态
     */
    private void updatePetAdoptStatus(Integer petId, Boolean isAdopted) {
        try {
            // 创建宠物对象并设置状态
            Pet pet = new Pet();
            pet.setId(petId);
            pet.setIsAdopt(isAdopted);

            // 调用Mapper更新
            petMapper.update(pet);

            System.out.println("✅ 宠物状态同步完成：petId=" + petId + ", isAdopt=" + isAdopted);
        } catch (Exception e) {
            System.err.println("❌ 更新宠物状态失败：petId=" + petId + ", error=" + e.getMessage());
            // 这里可以选择记录日志但不影响主流程
        }
    }

    /**
     * 如果是审核不通过，用户调用这一块逻辑进行材料补充
     *
     * @param petAdoptOrder 宠物领养订单信息
     * @return Result<String>
     */
    @Override
    public Result<String> againReply(PetAdoptOrder petAdoptOrder) {
        AssertUtils.notNull(petAdoptOrder.getId(), "订单ID不能为空");
        AssertUtils.hasText(petAdoptOrder.getDetail(), "证明材料不能为空");
        PetAdoptOrder adoptOrder = getById(petAdoptOrder.getId());
        AssertUtils.notNull(adoptOrder, "查询订单信息异常");
        // 宠物现在绑定的订单
        PetAdoptOrderQueryDto petAdoptOrderQueryDto = new PetAdoptOrderQueryDto();
        petAdoptOrderQueryDto.setPetId(petAdoptOrder.getPetId());
        orderStatusConfirm(petAdoptOrderQueryDto);
        adoptOrder.setIsAgainPost(IsAgainPostEnum.AGAIN_REPLY.getStatus()); // 设置为再次提交状态\
        adoptOrder.setPostNumber(adoptOrder.getPostNumber() + 1);
        adoptOrder.setDetail(petAdoptOrder.getDetail());
        return update(adoptOrder);
    }
}
