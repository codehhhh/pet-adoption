package com.kmbeast.controller;

import com.kmbeast.aop.Pager;
import com.kmbeast.context.LocalThreadHolder;
import com.kmbeast.pojo.api.ApiResult;
import com.kmbeast.pojo.api.Result;
import com.kmbeast.pojo.dto.ProposalFeedbackQueryDto;
import com.kmbeast.pojo.entity.ProposalFeedback;
import com.kmbeast.pojo.vo.ProposalFeedbackVO;
import com.kmbeast.service.ProposalFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 意见与反馈控制器
 * 企业级优化：删除冗余注解、添加日志/接口文档、参数校验
 */
@Api(tags = "意见与反馈接口")
@RestController
@RequestMapping("/proposal-feedback")
public class ProposalFeedbackController {

    private static final Logger log = LoggerFactory.getLogger(ProposalFeedbackController.class);

    @Resource
    private ProposalFeedbackService proposalFeedbackService;

    /**
     * 新增意见与反馈
     */
    @ApiOperation("新增意见与反馈")
    @PostMapping("/save")
    public Result<String> save(
            @ApiParam(value = "意见反馈实体数据", required = true)
            @RequestBody ProposalFeedback proposalFeedback) {
        log.info("用户{}新增意见反馈：{}",
                LocalThreadHolder.getUserId(),
                proposalFeedback.getDetail());
        return proposalFeedbackService.saveEntity(proposalFeedback);
    }

    /**
     * 修改意见与反馈
     */
    @ApiOperation("修改意见与反馈内容")
    @PostMapping("/update")
    public Result<String> update(
            @ApiParam(value = "意见反馈实体数据", required = true)
            @RequestBody ProposalFeedback proposalFeedback) {
        if (proposalFeedback.getId() == null || proposalFeedback.getId() <= 0) {
            log.warn("修改意见反馈参数非法：id={}", proposalFeedback.getId());
            return ApiResult.error("反馈ID不能为空");
        }
        log.info("修改意见反馈{}", proposalFeedback.getId());
        return proposalFeedbackService.update(proposalFeedback);
    }

    /**
     * 删除意见与反馈
     */
    @ApiOperation("根据ID删除意见与反馈")
    @DeleteMapping("/{id}")
    public Result<String> deleteById(
            @ApiParam(value = "反馈ID", required = true)
            @PathVariable Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除意见反馈参数非法：id={}", id);
            return ApiResult.error("反馈ID必须为正整数");
        }
        log.info("删除意见反馈{}", id);
        proposalFeedbackService.removeById(id);
        return ApiResult.success("意见与反馈删除成功");
    }

    /**
     * 查询当前用户的意见反馈
     */
    @ApiOperation("分页查询当前用户的意见反馈")
    @Pager
    @PostMapping("/listUser")
    public Result<List<ProposalFeedbackVO>> listUser(
            @ApiParam(value = "反馈查询条件", required = true)
            @RequestBody ProposalFeedbackQueryDto proposalFeedbackQueryDto) {
        Integer userId = LocalThreadHolder.getUserId();
        proposalFeedbackQueryDto.setUserId(userId);
        log.info("用户{}查询意见反馈列表", userId);
        return proposalFeedbackService.query(proposalFeedbackQueryDto);
    }

    /**
     * 管理员查询所有意见反馈
     */
    @ApiOperation("分页查询所有意见反馈（管理员）")
    @Pager
    @PostMapping("/list")
    public Result<List<ProposalFeedbackVO>> list(
            @ApiParam(value = "反馈查询条件", required = true)
            @RequestBody ProposalFeedbackQueryDto proposalFeedbackQueryDto) {
        log.info("管理员查询意见反馈列表，条件：{}", proposalFeedbackQueryDto);
        return proposalFeedbackService.query(proposalFeedbackQueryDto);
    }

}