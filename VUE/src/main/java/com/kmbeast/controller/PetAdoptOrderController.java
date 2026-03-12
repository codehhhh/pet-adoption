package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetAdoptOrderQueryDto;
import com.kmbeast.pojo.entity.PetAdoptOrder;
import com.kmbeast.pojo.vo.PetAdoptOrderVO;
import com.kmbeast.service.PetAdoptOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宠物领养订单控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "宠物领养订单接口")
@RestController
@RequestMapping("/pet-adopt-order")
public class PetAdoptOrderController {

    private static final Logger log = LoggerFactory.getLogger(PetAdoptOrderController.class);

    @Resource
    private PetAdoptOrderService petAdoptOrderService;

    /**
     * 新增领养订单
     */
    @ApiOperation("新增宠物领养订单")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "领养订单实体数据", required = true)
            @RequestBody PetAdoptOrder petAdoptOrder) {
        log.info("用户{}新增领养订单：宠物ID={}",
                LocalThreadHolder.getUserId(),
                petAdoptOrder.getPetId());
        return petAdoptOrderService.saveEntity(petAdoptOrder);
    }

    /**
     * 修改领养订单
     */
    @ApiOperation("修改宠物领养订单")
    @PutMapping("/update")
    public Result<String> update(
            @ApiParam(value = "领养订单实体数据", required = true)
            @RequestBody PetAdoptOrder petAdoptOrder) {
        if (petAdoptOrder.getId() == null || petAdoptOrder.getId() <= 0) {
            log.warn("修改领养订单参数非法：id={}", petAdoptOrder.getId());
            return ApiResult.error("订单ID不能为空");
        }
        log.info("修改领养订单{}", petAdoptOrder.getId());
        return petAdoptOrderService.update(petAdoptOrder);
    }

    /**
     * 管理员审核领养订单
     */
    @ApiOperation("管理员审核宠物领养订单")
    @PutMapping("/audit")
    public Result<String> audit(
            @ApiParam(value = "领养订单审核参数", required = true)
            @RequestBody PetAdoptOrder petAdoptOrder) {
        if (petAdoptOrder.getId() == null || petAdoptOrder.getId() <= 0) {
            log.warn("审核领养订单参数非法：id={}", petAdoptOrder.getId());
            return ApiResult.error("订单ID不能为空");
        }
        log.info("管理员审核领养订单{}：{}",
                petAdoptOrder.getId(),
                petAdoptOrder.getStatus());
        return petAdoptOrderService.audit(petAdoptOrder);
    }

    /**
     * 用户重新提交订单
     */
    @ApiOperation("用户重新提交领养订单")
    @PutMapping("/againReply")
    public Result<String> againReply(
            @ApiParam(value = "领养订单重新提交参数", required = true)
            @RequestBody PetAdoptOrder petAdoptOrder) {
        if (petAdoptOrder.getId() == null || petAdoptOrder.getId() <= 0) {
            log.warn("重新提交订单参数非法：id={}", petAdoptOrder.getId());
            return ApiResult.error("订单ID不能为空");
        }
        log.info("用户{}重新提交领养订单{}",
                LocalThreadHolder.getUserId(),
                petAdoptOrder.getId());
        return petAdoptOrderService.againReply(petAdoptOrder);
    }

    /**
     * 删除领养订单
     */
    @ApiOperation("根据ID删除领养订单")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "领养订单ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除领养订单参数非法：id={}", id);
            return ApiResult.error("订单ID必须为正整数");
        }
        log.info("删除领养订单{}", id);
        petAdoptOrderService.removeById(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 查询当前用户的领养订单
     */
    @ApiOperation("分页查询当前用户的领养订单")
    @Pager
    @PostMapping("/queryUser")
    public Result<List<PetAdoptOrderVO>> queryUser(
            @ApiParam(value = "订单查询条件", required = true)
            @RequestBody PetAdoptOrderQueryDto petAdoptOrderQueryDto) {
        Integer userId = LocalThreadHolder.getUserId();
        petAdoptOrderQueryDto.setUserId(userId);
        log.info("用户{}查询领养订单，条件：{}", userId, petAdoptOrderQueryDto.getStatus());
        return petAdoptOrderService.query(petAdoptOrderQueryDto);
    }

    /**
     * 管理员查询所有领养订单
     */
    @ApiOperation("分页查询所有领养订单（管理员）")
    @Pager
    @PostMapping("/query")
    public Result<List<PetAdoptOrderVO>> query(
            @ApiParam(value = "订单查询条件", required = true)
            @RequestBody PetAdoptOrderQueryDto petAdoptOrderQueryDto) {
        log.info("管理员查询领养订单，条件：{}", petAdoptOrderQueryDto);
        return petAdoptOrderService.query(petAdoptOrderQueryDto);
    }

}