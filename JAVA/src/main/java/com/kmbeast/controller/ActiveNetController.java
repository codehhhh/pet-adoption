package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.ActiveNetQueryDto;
import com.kmbeast.pojo.entity.ActiveNet;
import com.kmbeast.pojo.vo.ChartVO;
import com.kmbeast.service.ActiveNetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 行为互动控制器
 */
@Api(tags = "行为互动接口")
@RestController
@RequestMapping("/active-net")
public class ActiveNetController {

    private static final Logger log = LoggerFactory.getLogger(ActiveNetController.class);

    @Resource
    private ActiveNetService activeNetService;

    /**
     * 新增行为互动记录
     */
    @ApiOperation("新增行为互动记录（收藏/浏览/点赞等）")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "行为互动实体数据", required = true)
            @RequestBody ActiveNet activeNet) {
        log.info("新增行为互动记录：{}", activeNet.getType());
        return activeNetService.saveEntity(activeNet);
    }

    /**
     * 删除用户收藏的宠物信息
     */
    @ApiOperation("删除当前用户收藏的宠物列表")
    @DeleteMapping("/deleteUserPetList")
    public Result<String> deleteUserPetList() {
        Integer userId = LocalThreadHolder.getUserId();
        log.info("用户{}删除收藏的宠物列表", userId);
        return activeNetService.deleteUserPetList();
    }

    /**
     * 删除用户收藏的宠物经验帖子信息
     */
    @ApiOperation("删除当前用户收藏的宠物经验帖子列表")
    @DeleteMapping("/deleteUserPetPostList")
    public Result<String> deleteUserPetPostList() {
        Integer userId = LocalThreadHolder.getUserId();
        log.info("用户{}删除收藏的宠物经验帖子列表", userId);
        return activeNetService.deleteUserPetPostList();
    }

    /**
     * 删除指定行为互动记录
     */
    @ApiOperation("根据ID删除行为互动记录")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "行为互动记录ID", required = true)
            @PathVariable Integer id) {
        // 参数校验
        if (id == null || id <= 0) {
            log.warn("删除行为互动记录参数非法：id={}", id);
            return ApiResult.error("记录ID必须为正整数");
        }
        log.info("删除行为互动记录{}", id);
        activeNetService.removeById(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 查询当前用户的行为互动记录
     */
    @ApiOperation("分页查询当前用户的行为互动记录")
    @Pager
    @PostMapping("/queryUser")
    public Result<List<ActiveNet>> queryUser(
            @ApiParam(value = "行为互动查询条件", required = true)
            @RequestBody ActiveNetQueryDto activeNetQueryDto) {
        Integer userId = LocalThreadHolder.getUserId();
        activeNetQueryDto.setUserId(userId);
        log.info("用户{}查询行为互动记录，条件：{}", userId, activeNetQueryDto.getType());
        return activeNetService.query(activeNetQueryDto);
    }

    /**
     * 管理员查询所有行为互动记录
     */
    @ApiOperation("分页查询所有行为互动记录（管理员）")
    @Pager
    @PostMapping("/query")
    public Result<List<ActiveNet>> query(
            @ApiParam(value = "行为互动查询条件", required = true)
            @RequestBody ActiveNetQueryDto activeNetQueryDto) {
        log.info("管理员查询行为互动记录，条件：{}", activeNetQueryDto);
        return activeNetService.query(activeNetQueryDto);
    }

    /**
     * 查询帖子流量指标数据
     */
    @ApiOperation("查询帖子流量指标图表数据")
    @PostMapping("/listChart")
    public Result<List<ChartVO>> listChart(
            @ApiParam(value = "流量查询条件", required = true)
            @RequestBody ActiveNetQueryDto activeNetQueryDto) {
        log.info("查询帖子流量指标数据，条件：{}", activeNetQueryDto);
        return activeNetService.listChart(activeNetQueryDto);
    }

}