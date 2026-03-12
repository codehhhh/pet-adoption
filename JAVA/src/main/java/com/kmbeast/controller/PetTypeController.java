package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.PetTypeQueryDto;
import com.kmbeast.pojo.entity.PetType;
import com.kmbeast.service.PetTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 宠物类别控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "宠物类别接口")
@RestController
@RequestMapping("/pet-type")
public class PetTypeController {

    private static final Logger log = LoggerFactory.getLogger(PetTypeController.class);

    @Resource
    private PetTypeService petTypeService;

    /**
     * 新增宠物类别
     */
    @ApiOperation("新增宠物类别")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "宠物类别实体数据", required = true)
            @RequestBody PetType petType) {
        // 参数校验
        if (petType.getName() == null || petType.getName().trim().isEmpty()) {
            log.warn("新增宠物类别参数非法：名称为空");
            return ApiResult.error("宠物类别名称不能为空");
        }
        log.info("新增宠物类别：{}", petType.getName());
        return petTypeService.save(petType);
    }

    /**
     * 修改宠物类别
     */
    @ApiOperation("修改宠物类别")
    @PostMapping("/update")
    public Result<String> update(
            @ApiParam(value = "宠物类别实体数据", required = true)
            @RequestBody PetType petType) {
        if (petType.getId() == null || petType.getId() <= 0) {
            log.warn("修改宠物类别参数非法：id={}", petType.getId());
            return ApiResult.error("类别ID不能为空");
        }
        if (petType.getName() == null || petType.getName().trim().isEmpty()) {
            log.warn("修改宠物类别参数非法：名称为空");
            return ApiResult.error("宠物类别名称不能为空");
        }
        log.info("修改宠物类别{}：{}", petType.getId(), petType.getName());
        return petTypeService.update(petType);
    }

    /**
     * 删除宠物类别
     */
    @ApiOperation("根据ID删除宠物类别")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "类别ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除宠物类别参数非法：id={}", id);
            return ApiResult.error("类别ID必须为正整数");
        }
        log.info("删除宠物类别{}", id);
        return petTypeService.deleteById(id);
    }

    /**
     * 分页查询宠物类别
     */
    @ApiOperation("分页查询宠物类别列表")
    @Pager
    @PostMapping("/query")
    public Result<List<PetType>> query(
            @ApiParam(value = "类别查询条件", required = true)
            @RequestBody PetTypeQueryDto petTypeQueryDto) {
        log.info("分页查询宠物类别，条件：{}", petTypeQueryDto);
        return petTypeService.query(petTypeQueryDto);
    }

}