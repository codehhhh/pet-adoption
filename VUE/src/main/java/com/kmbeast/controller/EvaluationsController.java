package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.EvaluationsQueryDto;
import com.kmbeast.pojo.entity.Evaluations;
import com.kmbeast.service.EvaluationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 评论控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "评论接口")
@RestController
@RequestMapping("/evaluations")
public class EvaluationsController {

    private static final Logger log = LoggerFactory.getLogger(EvaluationsController.class);

    @Resource
    private EvaluationsService evaluationsService;

    /**
     * 新增评论
     */
    @ApiOperation("新增评论")
    @PostMapping("/insert")
    public Result<Object> insert(
            @ApiParam(value = "评论实体数据", required = true)
            @RequestBody Evaluations evaluations) {
        log.info("新增评论：内容ID={}，类型={}", evaluations.getContentId(), evaluations.getContentType());
        return evaluationsService.insert(evaluations);
    }

    /**
     * 点赞/取消点赞评论
     */
    @ApiOperation("点赞或取消点赞评论")
    @PostMapping("/upvoteOperation")
    public Result<Object> upvoteOperation(
            @ApiParam(value = "评论点赞参数", required = true)
            @RequestBody Evaluations evaluations) {
        if (evaluations.getId() == null || evaluations.getId() <= 0) {
            log.warn("评论点赞参数非法：id={}", evaluations.getId());
            return ApiResult.error("评论ID不能为空");
        }
        // ✅ 修复：移除对 isUpvote 的调用（数据库无此字段）
        log.info("评论{}执行点赞/取消点赞操作", evaluations.getId());
        return evaluationsService.upvoteOperation(evaluations);
    }

    /**
     * 修改评论
     */
    @ApiOperation("修改评论内容")
    @PutMapping("/update")
    public Result<Void> update(
            @ApiParam(value = "评论实体数据", required = true)
            @RequestBody Evaluations evaluations) {
        if (evaluations.getId() == null || evaluations.getId() <= 0) {
            log.warn("修改评论参数非法：id={}", evaluations.getId());
            return ApiResult.error("评论ID不能为空");
        }
        log.info("修改评论{}", evaluations.getId());
        return evaluationsService.update(evaluations);
    }

    /**
     * 查询指定内容下的所有评论
     */
    @ApiOperation("查询指定内容下的所有评论")
    @GetMapping("/list/{contentId}/{contentType}")
    public Result<Object> list(
            @ApiParam(value = "内容ID", required = true)
            @PathVariable Integer contentId,
            @ApiParam(value = "内容类型（PET/PET_POST等）", required = true)
            @PathVariable String contentType) {
        if (contentId == null || contentId <= 0 || contentType == null || contentType.trim().isEmpty()) {
            log.warn("查询评论参数非法：contentId={}, contentType={}", contentId, contentType);
            return ApiResult.error("参数不能为空");
        }
        log.info("查询内容{}({})下的评论列表", contentId, contentType);
        return evaluationsService.list(contentId, contentType);
    }

    /**
     * 分页查询评论
     */
    @ApiOperation("分页查询评论列表")
    @Pager
    @PostMapping("/query")
    public Result<Object> query(
            @ApiParam(value = "评论查询条件", required = true)
            @RequestBody EvaluationsQueryDto evaluationsQueryDto) {
        log.info("分页查询评论，条件：{}", evaluationsQueryDto);
        return evaluationsService.query(evaluationsQueryDto);
    }

    /**
     * 删除评论
     */
    @ApiOperation("根据ID删除评论")
    @DeleteMapping("/{id}")
    public Result<String> delete(
            @ApiParam(value = "评论ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除评论参数非法：id={}", id);
            return ApiResult.error("评论ID必须为正整数");
        }
        log.info("删除评论{}", id);
        return evaluationsService.deleteById(id);
    }

}