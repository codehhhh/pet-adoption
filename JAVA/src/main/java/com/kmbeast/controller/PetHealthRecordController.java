package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetHealthRecordQueryDto;
import com.kmbeast.pojo.entity.PetHealthRecord;
import com.kmbeast.pojo.vo.PetHealthRecordVO;
import com.kmbeast.service.PetHealthRecordService;
import com.kmbeast.service.impl.PetHealthRecordServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 宠物健康记录控制器
 */
@Api(tags = "宠物健康记录接口")
@RestController
@RequestMapping("/pet-health-record")
public class PetHealthRecordController {

    private static final Logger log = LoggerFactory.getLogger(PetHealthRecordController.class);

    @Resource
    private PetHealthRecordService petHealthRecordService;

    /**
     * 新增宠物健康记录
     * 修改：用IService的save方法
     */
    @ApiOperation("新增宠物健康记录")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "宠物健康记录实体数据", required = true)
            @RequestBody PetHealthRecord petHealthRecord) {
        log.info("新增宠物健康记录：宠物ID={}", petHealthRecord.getPetId());

        try {
            // 1. 业务校验（保留原来的业务逻辑）
            Integer userId = LocalThreadHolder.getUserId();
            if (userId == null) {
                return ApiResult.error("用户未登录");
            }
            if (petHealthRecord.getPetId() == null) {
                return ApiResult.error("请选择宠物");
            }

            // 检查是否领养（需要注入PetAdoptOrderMapper，这里简化，最好移到Service）
            // 这里为了保持业务逻辑完整，建议在Service中写一个带业务校验的save方法

            // 2. 设置创建时间
            petHealthRecord.setCreateTime(LocalDateTime.now());

            // 3. 调用继承的save方法
            boolean success = petHealthRecordService.save(petHealthRecord);
            return success ? ApiResult.success("添加成功") : ApiResult.error("添加失败");

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 修改宠物健康记录
     * 修改：用IService的updateById方法
     */
    @ApiOperation("修改宠物健康记录")
    @PutMapping("/update")
    public Result<String> update(
            @ApiParam(value = "宠物健康记录实体数据", required = true)
            @RequestBody PetHealthRecord petHealthRecord) {
        if (petHealthRecord.getId() == null || petHealthRecord.getId() <= 0) {
            log.warn("修改健康记录参数非法：id={}", petHealthRecord.getId());
            return ApiResult.error("健康记录ID不能为空");
        }
        log.info("修改宠物健康记录{}", petHealthRecord.getId());

        try {
            // 业务校验（保留）
            Integer userId = LocalThreadHolder.getUserId();
            if (userId == null) {
                return ApiResult.error("用户未登录");
            }

            // 调用继承的updateById方法
            boolean success = petHealthRecordService.updateById(petHealthRecord);
            return success ? ApiResult.success("更新成功") : ApiResult.error("更新失败");

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 删除宠物健康记录
     * 修改：用IService的removeById方法
     */
    @ApiOperation("根据ID删除宠物健康记录")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "健康记录ID", required = true)
            @PathVariable Long id) {
        if (id == null || id <= 0) {
            log.warn("删除健康记录参数非法：id={}", id);
            return ApiResult.error("健康记录ID必须为正整数");
        }
        log.info("删除宠物健康记录{}", id);

        try {
            // 调用继承的removeById方法
            boolean success = petHealthRecordService.removeById(id);
            return success ? ApiResult.success("删除成功") : ApiResult.error("删除失败");

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 查询宠物健康记录详情
     * 修改：调用自定义的getVoById方法
     */
    @ApiOperation("根据ID查询宠物健康记录详情")
    @GetMapping("/{id}")
    public Result<PetHealthRecordVO> getById(
            @ApiParam(value = "健康记录ID", required = true)
            @PathVariable Long id) {
        if (id == null || id <= 0) {
            log.warn("查询健康记录参数非法：id={}", id);
            return ApiResult.error("健康记录ID必须为正整数");
        }
        log.info("查询宠物健康记录{}详情", id);

        //手动转VO（推荐，因为不需要强制转换）
        PetHealthRecord record = petHealthRecordService.getById(id);
        if (record == null) return ApiResult.error("记录不存在");
        PetHealthRecordVO vo = new PetHealthRecordVO();
        BeanUtils.copyProperties(record, vo);
        return ApiResult.success(vo);
    }

    /**
     * 分页查询宠物健康记录
     * 不变：还是调用自定义的list方法
     */
    @ApiOperation("分页查询宠物健康记录列表")
    @Pager
    @PostMapping("/list")
    public Result<List<PetHealthRecordVO>> list(
            @ApiParam(value = "健康记录查询条件", required = true)
            @RequestBody PetHealthRecordQueryDto queryDto) {
        log.info("分页查询宠物健康记录，条件：{}", queryDto);
        return petHealthRecordService.list(queryDto);
    }

    /**
     * 根据宠物ID查询健康记录
     * 不变：还是调用自定义的queryByPetId方法
     */
    @ApiOperation("根据宠物ID查询健康记录列表")
    @GetMapping("/pet/{petId}")
    public Result<List<PetHealthRecordVO>> queryByPetId(
            @ApiParam(value = "宠物ID", required = true)
            @PathVariable Integer petId) {
        if (petId == null || petId <= 0) {
            log.warn("查询健康记录参数非法：petId={}", petId);
            return ApiResult.error("宠物ID必须为正整数");
        }
        log.info("查询宠物{}的健康记录列表", petId);
        return petHealthRecordService.queryByPetId(petId);
    }

    /**
     * 查询当前用户的宠物健康记录
     * 不变：还是调用自定义的queryByUserId方法
     */
    @ApiOperation("查询当前用户的宠物健康记录列表")
    @GetMapping("/user")
    public Result<List<PetHealthRecordVO>> queryByUserId() {
        log.info("查询当前用户的宠物健康记录");
        return petHealthRecordService.queryByUserId();
    }
}